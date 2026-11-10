package io.github.cleveroze.otherapps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.cleveroze.otherapps.data.ApkRepository
import io.github.cleveroze.otherapps.data.Catalog
import io.github.cleveroze.otherapps.data.DownloadState
import io.github.cleveroze.otherapps.data.InstallResult
import io.github.cleveroze.otherapps.data.StoreApp
import io.github.cleveroze.otherapps.data.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val storeRepository = StoreRepository(application)
    private val apkRepository = ApkRepository(application)

    private val cached = storeRepository.cachedApps()

    private val _apps = MutableStateFlow(Catalog.sort(cached ?: Catalog.bundled))
    val apps: StateFlow<List<StoreApp>> = _apps.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _lastUpdated = MutableStateFlow(storeRepository.lastUpdated())
    val lastUpdated: StateFlow<Long> = _lastUpdated.asStateFlow()

    val downloads: StateFlow<Map<String, DownloadState>> = apkRepository.states

    init {
        if (cached == null || !storeRepository.isCacheFresh()) {
            refresh(silent = true)
        }
    }

    fun refresh(silent: Boolean = false) {
        if (_refreshing.value) return
        viewModelScope.launch {
            _refreshing.value = true
            if (!silent) _error.value = null
            runCatching { storeRepository.fetchApps() }
                .onSuccess { fresh ->
                    if (fresh.isNotEmpty()) {
                        _apps.value = fresh
                        _lastUpdated.value = System.currentTimeMillis()
                        _error.value = null
                    }
                }
                .onFailure { throwable ->
                    _error.value = when {
                        throwable is java.net.UnknownHostException -> "Нет соединения с GitHub"
                        throwable is java.net.SocketTimeoutException -> "GitHub не отвечает"
                        else -> throwable.message ?: "Не удалось обновить каталог"
                    }
                }
            _refreshing.value = false
        }
    }

    fun download(app: StoreApp) = apkRepository.download(app)

    fun retry(app: StoreApp) = apkRepository.retry(app)

    fun delete(app: StoreApp) = apkRepository.delete(app.repo)

    fun install(file: File): InstallResult = apkRepository.install(file)
}
