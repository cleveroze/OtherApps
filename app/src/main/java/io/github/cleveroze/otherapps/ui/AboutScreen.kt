package io.github.cleveroze.otherapps.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.cleveroze.otherapps.data.Catalog
import io.github.cleveroze.otherapps.data.StoreApp

@Composable
fun AboutScreen(
    apps: List<StoreApp>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "aboutLogo")
    val tilt by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "tilt",
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "title") {
            Column {
                Text("О проекте", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "маленький магазин приложений",
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.onSurfaceVariant,
                )
            }
        }

        item(key = "hero") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(34.dp),
                color = Color.Transparent,
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(scheme.primaryContainer, scheme.secondaryContainer, scheme.tertiaryContainer)
                            )
                        )
                        .padding(22.dp)
                        .fillMaxWidth(),
                ) {
                    Canvas(Modifier.matchParentSize()) {
                        drawCircle(
                            color = scheme.onPrimaryContainer.copy(alpha = 0.05f),
                            radius = size.minDimension * 0.8f,
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, 0f),
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.rotate(tilt)) {
                            Surface(
                                shape = RoundedCornerShape(30.dp),
                                color = LogoSurface,
                            ) {
                                CloudLogo(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(92.dp),
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "OtherApps",
                            style = MaterialTheme.typography.displaySmall,
                            color = scheme.onPrimaryContainer,
                        )
                        Text(
                            text = "версия 1.0.1",
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatusPill(
                                text = "${apps.size} приложений",
                                color = scheme.onPrimaryContainer,
                                container = scheme.onPrimaryContainer.copy(alpha = 0.10f),
                            )
                            StatusPill(
                                text = "${apps.count { it.hasApk }} с APK",
                                color = scheme.onPrimaryContainer,
                                container = scheme.onPrimaryContainer.copy(alpha = 0.10f),
                            )
                        }
                    }
                }
            }
        }

        item(key = "text") {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = scheme.surfaceContainerLow,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    scheme.outlineVariant.copy(alpha = 0.5f),
                ),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "OtherApps — витрина Android-проектов cleveroze. Каталог собирается " +
                            "автоматически из GitHub: репозитории, описания, иконки и APK из Releases. " +
                            "Никаких ПК-программ вроде Stride и Potable — только то, что ставится на телефон.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
        }

        item(key = "how") {
            SectionHeader(title = "Как это работает")
        }

        item(key = "steps") {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = scheme.surfaceContainerLow,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    scheme.outlineVariant.copy(alpha = 0.5f),
                ),
            ) {
                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    StepRow("01", "Каталог", "Приложение читает репозитории cleveroze и оставляет только те, где есть APK.")
                    StepRow("02", "Релизы", "Версия, размер и changelog подтягиваются из последнего GitHub Release.")
                    StepRow("03", "Установка", "APK качается напрямую с GitHub, сохраняется внутри и открывается установщиком.")
                }
            }
        }

        item(key = "links") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LinkCard(
                    title = "GitHub · cleveroze",
                    subtitle = "все репозитории и исходники",
                    icon = Icons.Rounded.Code,
                    onClick = { openUrl(context, Catalog.PROFILE_URL) },
                )
                LinkCard(
                    title = "Telegram-канал",
                    subtitle = "анонсы и свежие сборки",
                    icon = Icons.Rounded.Send,
                    onClick = { openUrl(context, Catalog.TELEGRAM_URL) },
                )
            }
        }

        item(key = "footer") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BrandMark(size = 30.dp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "сделано с любовью к бежевому",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun StepRow(number: String, title: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 10.dp)) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(34.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LinkCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(42.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(17.dp),
            )
        }
    }
}
