package io.github.cleveroze.otherapps.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

sealed interface DownloadState {
    data object Idle : DownloadState
    data class Downloading(val received: Long, val total: Long) : DownloadState {
        val progress: Float
            get() = if (total > 0) (received.toFloat() / total).coerceIn(0f, 1f) else 0f
    }

    data class Ready(val file: File, val version: String, val size: Long, val at: Long) : DownloadState
    data class Failed(val message: String) : DownloadState
}

enum class InstallResult { Started, NeedsPermission, NoInstaller, Failed }

/** Downloads release APKs into filesDir/apk and remembers them between launches. */
class ApkRepository(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val directory = File(context.filesDir, "apk").apply { mkdirs() }
    private val prefs = context.getSharedPreferences("otherapps_downloads", Context.MODE_PRIVATE)

    private val _states = MutableStateFlow(loadSaved())
    val states: StateFlow<Map<String, DownloadState>> = _states.asStateFlow()

    fun download(app: StoreApp) {
        val release = app.release ?: return
        val current = _states.value[app.repo]
        if (current is DownloadState.Downloading) return

        val safeTag = release.tag.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val target = File(directory, "${app.repo}-$safeTag.apk")
        val partial = File(directory, "${app.repo}-$safeTag.part")

        set(app.repo, DownloadState.Downloading(0, release.sizeBytes))
        scope.launch {
            try {
                partial.delete()
                val connection = (URL(release.apkUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 20_000
                    readTimeout = 30_000
                    instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "OtherApps/1.0 (Android)")
                    setRequestProperty("Accept", "application/octet-stream")
                }
                try {
                    val code = connection.responseCode
                    if (code !in 200..299) error("Сервер вернул $code")
                    val total = connection.contentLengthLong.takeIf { it > 0 } ?: release.sizeBytes
                    var received = 0L
                    var lastUpdate = 0L
                    connection.inputStream.use { input ->
                        partial.outputStream().use { output ->
                            val buffer = ByteArray(128 * 1024)
                            while (true) {
                                val read = input.read(buffer)
                                if (read < 0) break
                                output.write(buffer, 0, read)
                                received += read
                                val now = System.currentTimeMillis()
                                if (now - lastUpdate > 120) {
                                    lastUpdate = now
                                    set(app.repo, DownloadState.Downloading(received, total))
                                }
                            }
                            output.flush()
                        }
                    }
                    target.delete()
                    if (!partial.renameTo(target)) {
                        partial.copyTo(target, overwrite = true)
                        partial.delete()
                    }
                    val size = target.length().takeIf { it > 0 } ?: release.sizeBytes
                    set(
                        app.repo,
                        DownloadState.Ready(
                            file = target,
                            version = release.versionLabel,
                            size = size,
                            at = System.currentTimeMillis(),
                        ),
                    )
                    persist()
                } finally {
                    connection.disconnect()
                }
            } catch (t: Throwable) {
                partial.delete()
                set(app.repo, DownloadState.Failed("Не удалось скачать. Проверьте соединение."))
            }
        }
    }

    fun retry(app: StoreApp) {
        _states.value[app.repo]?.let {
            if (it is DownloadState.Failed) set(app.repo, DownloadState.Idle)
        }
        download(app)
    }

    fun delete(repo: String) {
        val state = _states.value[repo]
        if (state is DownloadState.Ready) {
            state.file.delete()
        }
        set(repo, DownloadState.Idle)
        persist()
    }

    fun install(file: File): InstallResult {
        if (!context.packageManager.canRequestPackageInstalls()) {
            runCatching {
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:${context.packageName}"),
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            return InstallResult.NeedsPermission
        }
        return try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            InstallResult.Started
        } catch (e: android.content.ActivityNotFoundException) {
            InstallResult.NoInstaller
        } catch (t: Throwable) {
            InstallResult.Failed
        }
    }

    private fun set(repo: String, state: DownloadState) {
        _states.value = _states.value.toMutableMap().apply { put(repo, state) }
    }

    private fun persist() {
        val array = JSONArray()
        _states.value.forEach { (repo, state) ->
            if (state is DownloadState.Ready) {
                array.put(
                    JSONObject().apply {
                        put("repo", repo)
                        put("path", state.file.absolutePath)
                        put("version", state.version)
                        put("size", state.size)
                        put("at", state.at)
                    }
                )
            }
        }
        prefs.edit().putString("index", array.toString()).apply()
    }

    private fun loadSaved(): Map<String, DownloadState> {
        val raw = prefs.getString("index", null) ?: return emptyMap()
        return runCatching {
            val array = JSONArray(raw)
            buildMap {
                for (i in 0 until array.length()) {
                    val json = array.getJSONObject(i)
                    val file = File(json.getString("path"))
                    if (!file.exists()) continue
                    put(
                        json.getString("repo"),
                        DownloadState.Ready(
                            file = file,
                            version = json.optString("version"),
                            size = json.optLong("size", file.length()),
                            at = json.optLong("at", file.lastModified()),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyMap())
    }

    @Suppress("unused")
    private suspend fun touch(file: File) = withContext(Dispatchers.IO) { file.lastModified() }
}
