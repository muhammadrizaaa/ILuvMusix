package org.riza.iluvmusix.ui.component.main.content.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import org.riza.iluvmusix.R

@Composable
fun PreviewPlayerButton(
    previewUrl: String,
    modifier: Modifier = Modifier,
    ringColor: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // 1. Initialize and manage the ExoPlayer lifecycle
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(previewUrl))
            prepare()
        }
    }

    // 2. Safely release the player when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // 3. Listen to ExoPlayer's state changes (Play, Pause, Finished)
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingState: Boolean) {
                isPlaying = isPlayingState
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    isPlaying = false
                    progress = 1f      // Fill the ring when done
                    exoPlayer.seekTo(0) // Reset to the beginning
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // 4. Update the progress ring smoothly while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val currentPosition = exoPlayer.currentPosition.toFloat()
            val duration = exoPlayer.duration.toFloat()
            if (duration > 0) {
                progress = currentPosition / duration
            }
            delay(50) // Poll 20 times per second for buttery smooth UI
        }
    }

    // 5. The UI Layer
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(64.dp) // Overall size of the widget
    ) {
        // The Circular Progress Ring
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = ringColor,
            trackColor = ringColor.copy(alpha = 0.2f), // Soft faded background track
            strokeWidth = 4.dp
        )

        // The Play/Pause Button
        IconButton(
            onClick = {
                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
            },
            modifier = Modifier
                .size(48.dp) // Slightly smaller than the ring to create a gap
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface) // Solid background behind icon
        ) {
            Icon(
                painter = painterResource(
                    // Replace these with your actual drawable resources
                    id = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}