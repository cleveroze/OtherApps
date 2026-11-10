package io.github.cleveroze.otherapps.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.cleveroze.otherapps.data.Catalog
import io.github.cleveroze.otherapps.data.DownloadState
import io.github.cleveroze.otherapps.data.StoreApp
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun HomeScreen(
    apps: List<StoreApp>,
    isRefreshing: Boolean,
    error: String?,
    lastUpdated: Long,
    downloads: Map<String, DownloadState>,
    onRefresh: () -> Unit,
    onOpenApp: (StoreApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<String?>(null) }

    val featured = apps.firstOrNull { it.featured } ?: apps.firstOrNull()
    val categories = remember(apps) { apps.map { it.category }.distinct() }
    val filtered = remember(apps, query, category) {
        apps.filter { app ->
            (category == null || app.category == category) &&
                (query.isBlank() || app.name.contains(query, true) ||
                    app.tagline.contains(query, true) ||
                    app.description.contains(query, true))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(key = "header") {
            HomeHeader(
                isRefreshing = isRefreshing,
                lastUpdated = lastUpdated,
                onRefresh = onRefresh,
            )
        }

        if (error != null) {
            item(key = "error") {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "Повторить",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = onRefresh)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }

        if (featured != null && query.isBlank() && category == null) {
            item(key = "hero") {
                HeroCard(app = featured, onOpen = { onOpenApp(featured) })
            }
        }

        item(key = "search") {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = CircleShape,
                placeholder = {
                    Text(
                        "Найти приложение",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Очистить",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            )
        }

        item(key = "chips") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TagChip(
                    text = "Все",
                    selected = category == null,
                    onClick = { category = null },
                )
                categories.forEach { item ->
                    TagChip(
                        text = item,
                        selected = category == item,
                        onClick = { category = if (category == item) null else item },
                    )
                }
            }
        }

        item(key = "section") {
            SectionHeader(
                title = if (query.isBlank() && category == null) "Все приложения" else "Найдено",
                trailing = {
                    Text(
                        text = "${filtered.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }

        if (filtered.isEmpty()) {
            item(key = "empty") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EmptyArt(modifier = Modifier.size(120.dp))
                    Spacer(Modifier.height(14.dp))
                    EmptyStateText(
                        title = "Ничего не нашлось",
                        subtitle = "Попробуйте другое название или сбросьте фильтр.",
                    )
                }
            }
        } else {
            itemsIndexed(filtered, key = { _, app -> app.repo }) { index, app ->
                AppCard(
                    app = app,
                    index = index,
                    download = downloads[app.repo],
                    onClick = { onOpenApp(app) },
                )
            }
        }

        item(key = "footer") {
            CatalogFooter(lastUpdated = lastUpdated, isRefreshing = isRefreshing)
        }
    }
}

@Composable
private fun HomeHeader(
    isRefreshing: Boolean,
    lastUpdated: Long,
    onRefresh: () -> Unit,
) {
    val spin = rememberInfiniteTransition(label = "refresh")
    val angle by spin.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(950, easing = LinearEasing)),
        label = "angle",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandMark()
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "OtherApps",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "каталог cleveroze",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onRefresh, enabled = !isRefreshing) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Обновить каталог",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(22.dp)
                    .rotate(if (isRefreshing) angle else 0f),
            )
        }
    }
}

@Composable
private fun HeroCard(app: StoreApp, onOpen: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val float = rememberInfiniteTransition(label = "hero")
    val drift by float.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "drift",
    )
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "heroScale",
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(34.dp),
        color = Color.Transparent,
        onClick = onOpen,
        interactionSource = interaction,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(scheme.primaryContainer, scheme.secondaryContainer, scheme.tertiaryContainer),
                    )
                )
                .padding(22.dp),
        ) {
            Canvas(Modifier.matchParentSize()) {
                drawCircle(
                    color = scheme.onPrimaryContainer.copy(alpha = 0.05f),
                    radius = size.minDimension * 0.7f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.94f, size.height * 0.1f),
                )
                drawCircle(
                    color = scheme.onPrimaryContainer.copy(alpha = 0.04f),
                    radius = size.minDimension * 0.42f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.62f, size.height * 1.02f),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = scheme.onPrimaryContainer.copy(alpha = 0.08f),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = scheme.onPrimaryContainer,
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "Выбор редакции",
                                style = MaterialTheme.typography.labelSmall,
                                color = scheme.onPrimaryContainer,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = scheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = app.tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onPrimaryContainer.copy(alpha = 0.78f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = scheme.onPrimaryContainer,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Смотреть",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = scheme.primaryContainer,
                                )
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.ArrowForward,
                                    contentDescription = null,
                                    tint = scheme.primaryContainer,
                                    modifier = Modifier.size(15.dp),
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = app.release?.let { formatBytes(it.sizeBytes) } ?: "скоро",
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .offset(y = drift.dp)
                        .rotate(-5f)
                ) {
                    AppIconView(app = app, size = 92.dp)
                }
            }
        }
    }
}

@Composable
private fun AppCard(
    app: StoreApp,
    index: Int,
    download: DownloadState?,
    onClick: () -> Unit,
) {
    var shown by remember(app.repo) { mutableStateOf(false) }
    LaunchedEffect(app.repo) {
        delay((index.coerceAtMost(7) * 45).toLong())
        shown = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(380),
        label = "alpha",
    )
    val offset by animateFloatAsState(
        targetValue = if (shown) 0f else 20f,
        animationSpec = tween(380),
        label = "offset",
    )
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardScale",
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = offset.dp.toPx()
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
        ),
        onClick = onClick,
        interactionSource = interaction,
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIconView(app = app, size = 58.dp)
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (app.featured) {
                        Spacer(Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(13.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = app.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (app.comingSoon || app.release == null) {
                        StatusPill(
                            text = if (app.comingSoon) "Скоро" else app.category,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            container = MaterialTheme.colorScheme.surfaceContainerHigh,
                        )
                    } else if (download is DownloadState.Downloading) {
                        StatusPill(
                            text = "Загрузка ${(download.progress * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.primary,
                            container = MaterialTheme.colorScheme.primaryContainer,
                        )
                    } else if (download is DownloadState.Ready) {
                        StatusPill(
                            text = "Скачано · установить",
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            container = MaterialTheme.colorScheme.tertiaryContainer,
                        )
                    } else {
                        Text(
                            text = app.release?.versionLabel ?: app.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        MetaDot()
                        Text(
                            text = formatBytes(app.release?.sizeBytes ?: 0L),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun CatalogFooter(lastUpdated: Long, isRefreshing: Boolean) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { openUrl(context, Catalog.PROFILE_URL) },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandMark(size = 26.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Каталог собирается из GitHub",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = when {
                        isRefreshing -> "Обновляем релизы…"
                        lastUpdated <= 0L -> "каталог из приложения"
                        else -> "обновлено ${formatRelative(lastUpdated)}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Suppress("unused")
private fun wave(value: Float): Float = sin(value)
