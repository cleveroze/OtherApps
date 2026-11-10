package io.github.cleveroze.otherapps.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.cleveroze.otherapps.data.DownloadState
import io.github.cleveroze.otherapps.data.StoreApp
import kotlin.math.sin

@Composable
fun DetailScreen(
    app: StoreApp,
    download: DownloadState?,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onRetry: () -> Unit,
    onInstall: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val entry = remember { Animatable(0f) }
    LaunchedEffect(app.repo) { entry.animateTo(1f, spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)) }
    val float = rememberInfiniteTransition(label = "detailFloat")
    val drift by float.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(4200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift",
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "bar") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(
                    icon = Icons.Rounded.ArrowBack,
                    contentDescription = "Назад",
                    modifier = Modifier
                        .clip(CircleShape)
                        .then(Modifier)
                        .clickableIcon(onBack),
                )
                Spacer(Modifier.weight(1f))
                IconBadge(
                    icon = Icons.Rounded.Share,
                    contentDescription = "Поделиться",
                    modifier = Modifier.clickableIcon {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, "${app.name} — ${app.repoUrl}")
                        }
                        context.startActivity(
                            android.content.Intent.createChooser(intent, "Поделиться приложением")
                        )
                    },
                )
                Spacer(Modifier.width(8.dp))
                IconBadge(
                    icon = Icons.Rounded.Code,
                    contentDescription = "Открыть GitHub",
                    modifier = Modifier.clickableIcon { openUrl(context, app.repoUrl) },
                )
            }
        }

        item(key = "hero") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = entry.value
                        scaleX = 0.92f + 0.08f * entry.value
                        scaleY = 0.92f + 0.08f * entry.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(178.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(app.accentColor.copy(alpha = 0.22f), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(cx, cy),
                                radius = size.minDimension * 0.7f,
                            ),
                            radius = size.minDimension * 0.7f,
                            center = androidx.compose.ui.geometry.Offset(cx, cy),
                        )
                        drawCircle(
                            color = app.accentColor.copy(alpha = 0.08f),
                            radius = size.minDimension * 0.28f,
                            center = androidx.compose.ui.geometry.Offset(cx + size.width * 0.22f, cy + sin(drift) * 10f),
                        )
                        drawCircle(
                            color = scheme.tertiary.copy(alpha = 0.10f),
                            radius = size.minDimension * 0.2f,
                            center = androidx.compose.ui.geometry.Offset(cx - size.width * 0.24f, cy - 22f),
                        )
                    }
                    Box(modifier = Modifier.graphicsLayer { translationY = drift }) {
                        AppIconView(app = app, size = 116.dp)
                    }
                }
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = app.tagline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusPill(text = app.category)
                    if (app.release != null) {
                        StatusPill(
                            text = app.release.versionLabel,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            container = MaterialTheme.colorScheme.secondaryContainer,
                        )
                    }
                    if (app.release != null) {
                        StatusPill(
                            text = formatBytes(app.release.sizeBytes),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            container = MaterialTheme.colorScheme.tertiaryContainer,
                        )
                    }
                }
            }
        }

        item(key = "actions") {
            DownloadAction(
                app = app,
                download = download,
                onDownload = onDownload,
                onRetry = onRetry,
                onInstall = onInstall,
                onDelete = onDelete,
            )
        }

        item(key = "about") {
            DetailSection(title = "Об приложении") {
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (app.features.isNotEmpty()) {
            item(key = "features") {
                DetailSection(title = "Что внутри") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        app.features.forEach { feature ->
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier
                                        .padding(top = 1.dp)
                                        .size(19.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (app.release != null && app.release.notes.isNotBlank()) {
            item(key = "notes") {
                DetailSection(
                    title = "Что нового",
                    subtitle = "${app.release.versionLabel} · ${formatDate(app.release.publishedAt)}",
                ) {
                    Text(
                        text = app.release.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item(key = "info") {
            DetailSection(title = "Информация") {
                Column {
                    InfoRow("Разработчик", "cleveroze")
                    InfoRow("Категория", app.category)
                    InfoRow("Версия", app.release?.versionLabel ?: "готовится")
                    InfoRow("Размер APK", app.release?.let { formatBytes(it.sizeBytes) } ?: "—")
                    InfoRow("Обновлено", app.release?.let { formatDate(it.publishedAt) } ?: "—")
                    InfoRow("Загрузок", app.release?.let { it.downloads.toString() } ?: "—")
                    if (app.language.isNotBlank()) InfoRow("Язык кода", app.language)
                    InfoRow("Исходный код", "github.com/cleveroze/${app.repo}", isLink = true) {
                        openUrl(context, app.repoUrl)
                    }
                }
            }
        }

        item(key = "dev") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                onClick = { openUrl(context, "https://github.com/cleveroze") },
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrandMark(size = 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "cleveroze",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "остальные проекты на GitHub",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        item(key = "legal") {
            Text(
                text = "APK скачивается напрямую из GitHub Releases. OtherApps не меняет файлы " +
                    "и не хранит их на сервере — только передаёт ссылку.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp),
            )
        }
    }
}

private fun Modifier.clickableIcon(onClick: () -> Unit): Modifier =
    this
        .clip(CircleShape)
        .clickable(onClick = onClick)

@Composable
private fun DownloadAction(
    app: StoreApp,
    download: DownloadState?,
    onDownload: () -> Unit,
    onRetry: () -> Unit,
    onInstall: () -> Unit,
    onDelete: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        shape = RoundedCornerShape(28.dp),
        color = scheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AnimatedContent(
                targetState = actionState(app, download),
                transitionSpec = {
                    (fadeIn(tween(220)) + androidx.compose.animation.scaleIn(initialScale = 0.96f))
                        .togetherWith(fadeOut(tween(160)))
                },
                label = "downloadAction",
            ) { state ->
                when (state) {
                    ActionState.ComingSoon -> DisabledAction(
                        text = "Готовится к релизу",
                        hint = "Следите за обновлениями на GitHub",
                    )

                    ActionState.NoRelease -> DisabledAction(
                        text = "Сборки пока нет",
                        hint = "APK появится в GitHub Releases",
                    )

                    ActionState.Downloading -> {
                        val progress = (download as? DownloadState.Downloading)
                        val animated by animateFloatAsState(
                            targetValue = progress?.progress ?: 0f,
                            animationSpec = tween(240),
                            label = "progress",
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Download,
                                    contentDescription = null,
                                    tint = scheme.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Скачиваем APK",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "${(animated * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = scheme.primary,
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { animated },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = scheme.primary,
                                trackColor = scheme.surfaceContainerHighest,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = progress?.let {
                                    "${formatBytes(it.received)} из ${formatBytes(it.total)}"
                                } ?: "Подключаемся к GitHub…",
                                style = MaterialTheme.typography.labelSmall,
                                color = scheme.onSurfaceVariant,
                            )
                        }
                    }

                    ActionState.Ready -> Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = onInstall,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = scheme.primary,
                                contentColor = scheme.onPrimary,
                            ),
                        ) {
                            Icon(Icons.Rounded.Android, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Установить", style = MaterialTheme.typography.labelLarge)
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(scheme.surfaceContainerHigh),
                        ) {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = "Удалить APK",
                                tint = scheme.onSurfaceVariant,
                            )
                        }
                    }

                    ActionState.Failed -> Column {
                        Text(
                            text = (download as? DownloadState.Failed)?.message ?: "Ошибка загрузки",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.error,
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = onRetry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = CircleShape,
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Повторить", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    ActionState.Download -> Column {
                        Button(
                            onClick = onDownload,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = scheme.primary,
                                contentColor = scheme.onPrimary,
                            ),
                        ) {
                            Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(19.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Скачать APK" + (app.release?.let { " · ${formatBytes(it.sizeBytes)}" } ?: ""),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Прямая ссылка с GitHub Releases · Android 8+",
                            style = MaterialTheme.typography.labelSmall,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private enum class ActionState { ComingSoon, NoRelease, Download, Downloading, Ready, Failed }

private fun actionState(app: StoreApp, download: DownloadState?): ActionState = when {
    app.comingSoon || app.release == null -> ActionState.ComingSoon
    download is DownloadState.Downloading -> ActionState.Downloading
    download is DownloadState.Ready -> ActionState.Ready
    download is DownloadState.Failed -> ActionState.Failed
    else -> ActionState.Download
}

@Composable
private fun DisabledAction(text: String, hint: String) {
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = hint,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DetailSection(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, isLink: Boolean = false, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .androidxClickable(onClick)
                } else Modifier
            )
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isLink) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isLink) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        if (isLink) {
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

private fun Modifier.androidxClickable(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

@Suppress("unused")
private fun unusedStar() = Icons.Rounded.Star

