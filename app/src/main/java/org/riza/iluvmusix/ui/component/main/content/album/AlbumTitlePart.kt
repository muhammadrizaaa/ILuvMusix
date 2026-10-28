package org.riza.iluvmusix.ui.component.main.content.album

import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import org.riza.iluvmusix.R
import org.riza.iluvmusix.helper.rememberPaletteImageRequest
import org.riza.iluvmusix.ui.component.main.content.util.BackIconButton

@Composable
fun AlbumTitlePart(
    title: String,
    imageUrl: String?,
    artistsName: String,
    isSaved: Boolean,
    onGlowColor: (Color) -> Unit,
    onArtistClick: () -> Unit,
    onSave: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val fallbackColor = MaterialTheme.colorScheme.primary
    var glowColor by remember { mutableStateOf(fallbackColor) }
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_pulse")

// Pulses smoothly back and forth between 0.7f and 1.0f over 3 seconds
    val firstAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse // Plays forward, then backwards
        ),
        label = "firstAlpha"
    )

// Pulses between 0.3f and 0.6f over a slightly different duration (4 seconds)
// to create an organic, unsynchronized feel
    val secondAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "secondAlpha"
    )

    val isLightColor = glowColor.luminance() > 0.5f
    val view = LocalView.current

    // Dynamically flip the status bar icons to Dark/Light based on the album color
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Ensure the status bar background itself is totally transparent
                @Suppress("DEPRECATION")
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                // Flip icons to dark if the color is bright, and light if the color is dark
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightColor
            }
        }
    }

    // The massive block is now just one clean line!
    val imageRequest = rememberPaletteImageRequest(
        imageUrl = imageUrl,
        context = context,
        onColorExtracted = { customColor -> glowColor = customColor }
    )
    LaunchedEffect(glowColor) {
        onGlowColor(glowColor)
    }
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            // The gradient now draws all the way to the top bezel of the phone
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = firstAlpha),
                        glowColor.copy(alpha = secondAlpha),
                        Color.Transparent
                    )
                )
            )
            .padding(bottom = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                // Added statusBarsPadding() so the album cover gets pushed down BELOW the battery icons
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 32.dp) // Adjusted padding since statusBarsPadding handles the system gap now
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = fallbackColor)
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(start = 8.dp).weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = {onArtistClick()}),
                        text = artistsName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                }
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable(onClick = { onSave(!isSaved) }),
                    painter = painterResource(
                        if (isSaved) R.drawable.round_bookmark_24
                        else R.drawable.outline_bookmark_24
                    ),
                    contentDescription = null,
                    tint = glowColor

                )
            }
        }

        BackIconButton(
            tint = colorResource(if(isLightColor) R.color.black else R.color.white),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp),
            onCLick = { onBack() }
        )
    }
}