package io.github.cleveroze.otherapps.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

/**
 * Reads the public GitHub profile and turns every repository that ships an APK
 * into a store entry. No token needed; responses are cached for offline use.
 */
class StoreRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("otherapps_cache", Context.MODE_PRIVATE)

    fun cachedApps(): List<StoreApp>? {
        val raw = prefs.getString(KEY_APPS, null) ?: return null
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { array.getJSONObject(it).toApp() }
        }.getOrNull()?.takeIf { it.isNotEmpty() }
    }

    fun lastUpdated(): Long = prefs.getLong(KEY_TIME, 0L)

    fun isCacheFresh(maxAgeMs: Long = 45 * 60 * 1000L): Boolean =
        System.currentTimeMillis() - lastUpdated() < maxAgeMs

    suspend fun fetchApps(): List<StoreApp> = withContext(Dispatchers.IO) {
        val reposJson = getJson("https://api.github.com/users/${Catalog.USER}/repos?per_page=100&sort=updated")
        val reposArray = JSONArray(reposJson)
        val repos = (0 until reposArray.length())
            .map { reposArray.getJSONObject(it) }
            .filter { repo ->
                !repo.optBoolean("fork", false) &&
                    !repo.optBoolean("archived", false) &&
                    !Catalog.blockedRepos.contains(repo.getString("name").lowercase())
            }

        val fetched = coroutineScope {
            repos.map { repo ->
                async {
                    val name = repo.getString("name")
                    val release = runCatching { latestApkRelease(name) }.getOrNull()
                    Triple(repo, name, release)
                }
            }.awaitAll()
        }

        val apps = fetched.mapNotNull { (repo, name, release) ->
            val meta = Catalog.byRepo[name.lowercase()]
            when {
                release != null -> {
                    val base = meta ?: StoreApp(
                        repo = name,
                        name = name,
                        tagline = repo.optString("description").ifBlank { "Проект cleveroze" },
                        description = repo.optString("description").ifBlank {
                            "Приложение из GitHub cleveroze. Описание появится в следующем обновлении каталога."
                        },
                        category = "Другое",
                        features = emptyList(),
                        iconKey = "monogram:$name",
                        accent = 0xFFB08968,
                    )
                    base.copy(
                        release = release,
                        stars = repo.optInt("stargazers_count", 0),
                        language = repo.optString("language"),
                        comingSoon = false,
                    )
                }

                meta != null && meta.comingSoon -> meta.copy(
                    stars = repo.optInt("stargazers_count", 0),
                    language = repo.optString("language").ifBlank { meta.language },
                )

                meta != null && meta.hasApk -> meta.copy(release = null, comingSoon = true)

                else -> null
            }
        }

        val merged = LinkedHashMap<String, StoreApp>()
        Catalog.bundled.forEach { merged[it.repo] = it }
        apps.forEach { merged[it.repo] = it }

        val result = Catalog.sort(merged.values.toList())
        saveCache(result)
        result
    }

    private fun latestApkRelease(repo: String): Release? {
        val raw = getJson("https://api.github.com/repos/${Catalog.USER}/$repo/releases?per_page=15")
        val releases = JSONArray(raw)
        data class Candidate(val json: JSONObject, val asset: JSONObject)

        val candidates = buildList {
            for (i in 0 until releases.length()) {
                val rel = releases.getJSONObject(i)
                val assets = rel.optJSONArray("assets") ?: continue
                val apks = (0 until assets.length())
                    .map { assets.getJSONObject(it) }
                    .filter { it.optString("name").endsWith(".apk", ignoreCase = true) }
                if (apks.isEmpty()) continue
                val asset = apks.sortedWith(
                    compareByDescending<JSONObject> {
                        val n = it.optString("name").lowercase()
                        !(n.contains("arm64") || n.contains("aarch64") || n.contains("armeabi") ||
                            n.contains("x86") || n.contains("v7a") || n.contains("v8a"))
                    }.thenByDescending { it.optLong("size", 0) }
                ).first()
                add(Candidate(rel, asset))
            }
        }
        if (candidates.isEmpty()) return null
        val chosen = candidates.filter { !it.json.optBoolean("prerelease", false) }
            .maxByOrNull { parseInstant(it.json.optString("published_at")) }
            ?: candidates.maxByOrNull { parseInstant(it.json.optString("published_at")) }
            ?: return null

        val tag = chosen.json.optString("tag_name")
        val version = tag.removePrefix("v").ifBlank { tag }
        return Release(
            tag = tag,
            version = version,
            name = chosen.json.optString("name").ifBlank { "$repo $version" },
            publishedAt = parseInstant(chosen.json.optString("published_at")),
            apkUrl = chosen.asset.optString("browser_download_url"),
            apkName = chosen.asset.optString("name"),
            sizeBytes = chosen.asset.optLong("size", 0L),
            downloads = chosen.asset.optInt("download_count", 0),
            notes = chosen.json.optString("body").trim().take(700),
        )
    }

    private fun parseInstant(value: String): Long =
        runCatching { Instant.parse(value).toEpochMilli() }.getOrDefault(0L)

    private fun getJson(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 20_000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "OtherApps/1.0 (Android)")
            setRequestProperty("Accept", "application/vnd.github+json")
        }
        return try {
            val code = connection.responseCode
            if (code == 403) {
                throw IllegalStateException("GitHub временно ограничил запросы")
            }
            if (code !in 200..299) {
                throw IllegalStateException("GitHub ответил $code")
            }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun saveCache(apps: List<StoreApp>) {
        val array = JSONArray()
        apps.forEach { array.put(it.toJson()) }
        prefs.edit()
            .putString(KEY_APPS, array.toString())
            .putLong(KEY_TIME, System.currentTimeMillis())
            .apply()
    }

    private fun StoreApp.toJson(): JSONObject = JSONObject().apply {
        put("repo", repo)
        put("name", name)
        put("tagline", tagline)
        put("description", description)
        put("category", category)
        put("features", JSONArray(features))
        put("iconKey", iconKey)
        put("accent", accent)
        put("featured", featured)
        put("comingSoon", comingSoon)
        put("stars", stars)
        put("language", language)
        release?.let { r ->
            put(
                "release",
                JSONObject().apply {
                    put("tag", r.tag)
                    put("version", r.version)
                    put("name", r.name)
                    put("publishedAt", r.publishedAt)
                    put("apkUrl", r.apkUrl)
                    put("apkName", r.apkName)
                    put("sizeBytes", r.sizeBytes)
                    put("downloads", r.downloads)
                    put("notes", r.notes)
                },
            )
        }
    }

    private fun JSONObject.toApp(): StoreApp {
        val releaseJson = optJSONObject("release")
        val featuresJson = optJSONArray("features")
        return StoreApp(
            repo = getString("repo"),
            name = getString("name"),
            tagline = optString("tagline"),
            description = optString("description"),
            category = optString("category", "Другое"),
            features = buildList {
                if (featuresJson != null) {
                    for (i in 0 until featuresJson.length()) add(featuresJson.getString(i))
                }
            },
            iconKey = optString("iconKey", "monogram:${getString("repo")}"),
            accent = optLong("accent", 0xFFB08968).toLong(),
            featured = optBoolean("featured", false),
            comingSoon = optBoolean("comingSoon", false),
            release = releaseJson?.let {
                Release(
                    tag = it.optString("tag"),
                    version = it.optString("version"),
                    name = it.optString("name"),
                    publishedAt = it.optLong("publishedAt", 0L),
                    apkUrl = it.optString("apkUrl"),
                    apkName = it.optString("apkName"),
                    sizeBytes = it.optLong("sizeBytes", 0L),
                    downloads = it.optInt("downloads", 0),
                    notes = it.optString("notes"),
                )
            },
            stars = optInt("stars", 0),
            language = optString("language"),
        )
    }

    private companion object {
        const val KEY_APPS = "apps_json"
        const val KEY_TIME = "time"
    }
}
