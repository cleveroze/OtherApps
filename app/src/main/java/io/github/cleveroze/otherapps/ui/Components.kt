package io.github.cleveroze.otherapps.ui

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.cleveroze.otherapps.R
import io.github.cleveroze.otherapps.data.Release
import io.github.cleveroze.otherapps.data.StoreApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.sin

val LogoSurface = Color(0xFFF3E4CD)

@Composable
fun BrandMark(size: Dp = 42.dp, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size * 0.32f),
        color = LogoSurface,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.logo_mark),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.66f),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun CloudLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_mark),
        contentDescription = "OtherApps",
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@DrawableRes
private fun vectorIcon(key: String): Int = when (key) {
    "res:notesyou" -> R.drawable.app_notesyou
    "res:weatheryou" -> R.drawable.app_weatheryou
    "res:keyboardyou" -> R.drawable.app_keyboardyou
    "res:claudeland" -> R.drawable.app_claudeland
    else -> R.drawable.logo_mark
}

@Composable
fun AppIconView(
    app: StoreApp,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(size * 0.27f)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(app.accentColor.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            app.iconKey.startsWith("res:") -> Image(
                painter = painterResource(vectorIcon(app.iconKey)),
                contentDescription = app.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            app.iconKey.startsWith("asset:") -> {
                val path = app.iconKey.removePrefix("asset:")
                val bitmap = remember(path) {
                    runCatching {
                        context.assets.open(path).use { BitmapFactory.decodeStream(it) }
                    }.getOrNull()
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = app.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Monogram(app.name, app.accentColor, size)
                }
            }

            else -> Monogram(app.name, app.accentColor, size)
        }
    }
}

@Composable
private fun Monogram(name: String, accent: Color, size: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.85f), accent.copy(alpha = 0.55f))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.take(2).uppercase(Locale.forLanguageTag("ru")),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.34f).sp,
        )
    }
}

@Composable
fun AmbientBackground(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "ambient")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 26_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "phase",
    )
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val drift = sin(phase * Math.PI.toFloat()) * w * 0.06f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(scheme.primary.copy(alpha = 0.13f), Color.Transparent),
                center = Offset(w * 0.08f + drift, h * 0.06f),
                radius = w * 0.62f,
            ),
            radius = w * 0.62f,
            center = Offset(w * 0.08f + drift, h * 0.06f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(scheme.tertiary.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(w * 0.96f - drift, h * 0.42f),
                radius = w * 0.55f,
            ),
            radius = w * 0.55f,
            center = Offset(w * 0.96f - drift, h * 0.42f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(scheme.secondary.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(w * 0.2f, h * 0.92f),
                radius = w * 0.6f,
            ),
            radius = w * 0.6f,
            center = Offset(w * 0.2f, h * 0.92f),
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) trailing()
    }
}

@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    container: Color = MaterialTheme.colorScheme.primaryContainer,
    icon: ImageVector? = null,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = container,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun MetaDot() {
    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(3.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f))
    )
}

@Composable
fun TagChip(text: String, selected: Boolean = false, onClick: (() -> Unit)? = null) {
    val container = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val content = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        shape = CircleShape,
        color = container,
        onClick = onClick ?: {},
        enabled = onClick != null,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            maxLines = 1,
        )
    }
}

@Composable
fun EmptyArt(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    val arrowColor = MaterialTheme.colorScheme.background
    val transition = rememberInfiniteTransition(label = "empty")
    val bob by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "bob",
    )
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f + bob
            val r = size.minDimension * 0.3f
            drawCircle(tint.copy(alpha = 0.10f), r * 1.7f, Offset(cx, cy))
            drawCircle(tint.copy(alpha = 0.18f), r * 1.2f, Offset(cx, cy))
            drawCircle(tint, r, Offset(cx, cy))
            drawLine(
                color = arrowColor,
                start = Offset(cx, cy - r * 0.45f),
                end = Offset(cx, cy + r * 0.25f),
                strokeWidth = r * 0.18f,
            )
            drawLine(
                color = arrowColor,
                start = Offset(cx - r * 0.35f, cy - r * 0.02f),
                end = Offset(cx, cy + r * 0.38f),
                strokeWidth = r * 0.18f,
            )
            drawLine(
                color = arrowColor,
                start = Offset(cx + r * 0.35f, cy - r * 0.02f),
                end = Offset(cx, cy + r * 0.38f),
                strokeWidth = r * 0.18f,
            )
        }
    }
}

private val russianMonths = arrayOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "—"
    val mb = bytes / 1_048_576.0
    return if (mb >= 100) {
        "${mb.roundToInt()} МБ"
    } else {
        String.format(Locale.forLanguageTag("ru"), "%.1f МБ", mb)
    }
}

fun formatDate(timestamp: Long): String {
    if (timestamp <= 0) return "—"
    val calendar = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
    return "${calendar.get(java.util.Calendar.DAY_OF_MONTH)} " +
        "${russianMonths[calendar.get(java.util.Calendar.MONTH)]} " +
        "${calendar.get(java.util.Calendar.YEAR)}"
}

fun formatRelative(timestamp: Long): String {
    if (timestamp <= 0) return "ещё не обновлялся"
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60_000
    return when {
        minutes < 1 -> "только что"
        minutes < 60 -> "$minutes мин назад"
        minutes < 24 * 60 -> "${minutes / 60} ч назад"
        minutes < 48 * 60 -> "вчера"
        else -> formatDate(timestamp)
    }
}

fun shortVersion(release: Release?): String = release?.versionLabel ?: "—"

fun openUrl(context: Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

@Composable
fun CloudHeroArt(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier) {
        drawCircle(tint.copy(alpha = 0.10f), size.minDimension * 0.46f, center)
        drawCircle(tint.copy(alpha = 0.08f), size.minDimension * 0.34f, center)
        drawCircle(tint.copy(alpha = 0.06f), size.minDimension * 0.24f, center)
    }
}

@Composable
fun IconBadge(icon: ImageVector, contentDescription: String?, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(34.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun EmptyStateText(title: String, subtitle: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

fun Modifier.rotated(degrees: Float): Modifier = this.rotate(degrees)

@Composable
fun TinyLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_mark),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun MetaText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
fun DotLine() {
    Spacer(
        Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}
