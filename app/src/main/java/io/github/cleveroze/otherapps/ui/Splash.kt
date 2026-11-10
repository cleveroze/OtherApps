package io.github.cleveroze.otherapps.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val scale = remember { Animatable(0.72f) }
    val alpha = remember { Animatable(0f) }
    val ring = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
        scale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
        ring.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        delay(420)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(scheme.surfaceContainerLow, scheme.background),
                    radius = 1100f,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = scheme.primary.copy(alpha = 0.08f * ring.value),
                radius = size.minDimension * (0.26f + 0.24f * ring.value),
                center = center,
            )
            drawCircle(
                color = scheme.primary.copy(alpha = 0.05f * (1f - ring.value)),
                radius = size.minDimension * (0.3f + 0.4f * ring.value),
                center = center,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    },
                shape = RoundedCornerShape(38.dp),
                color = LogoSurface,
            ) {
                CloudLogo(
                    modifier = Modifier
                        .padding(22.dp)
                        .size(112.dp),
                )
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = "OtherApps",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.2).sp,
                color = scheme.onBackground,
                modifier = Modifier.graphicsLayer { this.alpha = alpha.value },
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "каталог приложений cleveroze",
                style = MaterialTheme.typography.labelMedium,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { this.alpha = alpha.value },
            )
        }
        Text(
            text = "github.com/cleveroze",
            style = MaterialTheme.typography.labelSmall,
            color = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
                .graphicsLayer { this.alpha = alpha.value },
        )
    }
}
