package io.github.cleveroze.otherapps.data

/** A published GitHub release that contains a downloadable APK. */
data class Release(
    val tag: String,
    val version: String,
    val name: String,
    val publishedAt: Long,
    val apkUrl: String,
    val apkName: String,
    val sizeBytes: Long,
    val downloads: Int,
    val notes: String,
) {
    val versionLabel: String
        get() = when {
            version.isEmpty() -> tag
            version.firstOrNull()?.isDigit() == true -> "v$version"
            else -> version
        }
}

/**
 * One app in the store. Everything except [release] can come from the bundled
 * catalogue; the release part is refreshed from the GitHub API at runtime.
 */
data class StoreApp(
    val repo: String,
    val name: String,
    val tagline: String,
    val description: String,
    val category: String,
    val features: List<String>,
    val iconKey: String,
    val accent: Long,
    val featured: Boolean = false,
    val comingSoon: Boolean = false,
    val release: Release? = null,
    val stars: Int = 0,
    val language: String = "",
) {
    val hasApk: Boolean get() = release != null
    val repoUrl: String get() = "https://github.com/cleveroze/$repo"
    val accentColor: androidx.compose.ui.graphics.Color
        get() = androidx.compose.ui.graphics.Color(accent)
}
