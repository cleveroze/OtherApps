package io.github.cleveroze.otherapps.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.cleveroze.otherapps.MainViewModel
import io.github.cleveroze.otherapps.data.DownloadState
import io.github.cleveroze.otherapps.data.InstallResult
import io.github.cleveroze.otherapps.data.StoreApp

private const val TAB_CATALOG = 0
private const val TAB_DOWNLOADS = 1
private const val TAB_ABOUT = 2

@Composable
fun OtherAppsApp(viewModel: MainViewModel) {
    val apps by viewModel.apps.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val lastUpdated by viewModel.lastUpdated.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()

    var tab by rememberSaveable { mutableStateOf(TAB_CATALOG) }
    var selectedRepo by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    BackHandler(enabled = selectedRepo != null) { selectedRepo = null }

    fun install(app: StoreApp) {
        val state = downloads[app.repo] as? DownloadState.Ready ?: return
        when (viewModel.install(state.file)) {
            InstallResult.NeedsPermission -> Toast.makeText(
                context,
                "Разрешите установку из OtherApps и нажмите «Установить» снова",
                Toast.LENGTH_LONG,
            ).show()

            InstallResult.NoInstaller -> Toast.makeText(
                context,
                "На устройстве нет установщика APK",
                Toast.LENGTH_SHORT,
            ).show()

            InstallResult.Failed -> Toast.makeText(
                context,
                "Не удалось открыть установщик",
                Toast.LENGTH_SHORT,
            ).show()

            InstallResult.Started -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AmbientBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (selectedRepo == null) {
                    AppBottomBar(
                        tab = tab,
                        downloadedCount = downloads.values.count { it is DownloadState.Ready },
                        onSelect = { tab = it },
                    )
                }
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = selectedRepo,
                transitionSpec = {
                    if (targetState != null) {
                        (slideInHorizontally(tween(340)) { it / 3 } + fadeIn(tween(260))) togetherWith
                            (slideOutHorizontally(tween(340)) { -it / 6 } + fadeOut(tween(180)))
                    } else {
                        (slideInHorizontally(tween(340)) { -it / 6 } + fadeIn(tween(260))) togetherWith
                            (slideOutHorizontally(tween(340)) { it / 3 } + fadeOut(tween(180)))
                    }
                },
                label = "screen",
            ) { repo ->
                if (repo != null) {
                    val app = apps.firstOrNull { it.repo == repo }
                    if (app != null) {
                        DetailScreen(
                            app = app,
                            download = downloads[app.repo],
                            onBack = { selectedRepo = null },
                            onDownload = { viewModel.download(app) },
                            onRetry = { viewModel.retry(app) },
                            onInstall = { install(app) },
                            onDelete = { viewModel.delete(app) },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                } else {
                    AnimatedContent(
                        targetState = tab,
                        transitionSpec = {
                            val direction = if (targetState > initialState) 1 else -1
                            (slideInHorizontally(tween(280)) { direction * it / 8 } + fadeIn(tween(240))) togetherWith
                                (slideOutHorizontally(tween(240)) { -direction * it / 10 } + fadeOut(tween(160)))
                        },
                        label = "tab",
                    ) { current ->
                        when (current) {
                            TAB_DOWNLOADS -> DownloadsScreen(
                                apps = apps,
                                downloads = downloads,
                                onInstall = { install(it) },
                                onDelete = { viewModel.delete(it) },
                                onOpenCatalog = { tab = TAB_CATALOG },
                                modifier = Modifier.padding(innerPadding),
                            )

                            TAB_ABOUT -> AboutScreen(
                                apps = apps,
                                modifier = Modifier.padding(innerPadding),
                            )

                            else -> HomeScreen(
                                apps = apps,
                                isRefreshing = refreshing,
                                error = error,
                                lastUpdated = lastUpdated,
                                downloads = downloads,
                                onRefresh = { viewModel.refresh() },
                                onOpenApp = { selectedRepo = it.repo },
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    tab: Int,
    downloadedCount: Int,
    onSelect: (Int) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.97f),
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = tab == TAB_CATALOG,
            onClick = { onSelect(TAB_CATALOG) },
            icon = { Icon(Icons.Rounded.Apps, contentDescription = null) },
            label = { Text("Каталог") },
            colors = navColors(),
        )
        NavigationBarItem(
            selected = tab == TAB_DOWNLOADS,
            onClick = { onSelect(TAB_DOWNLOADS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (downloadedCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ) {
                                Text("$downloadedCount")
                            }
                        }
                    },
                ) {
                    Icon(Icons.Rounded.Download, contentDescription = null)
                }
            },
            label = { Text("Загрузки") },
            colors = navColors(),
        )
        NavigationBarItem(
            selected = tab == TAB_ABOUT,
            onClick = { onSelect(TAB_ABOUT) },
            icon = { Icon(Icons.Rounded.Info, contentDescription = null) },
            label = { Text("О проекте") },
            colors = navColors(),
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedTextColor = MaterialTheme.colorScheme.onSurface,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
