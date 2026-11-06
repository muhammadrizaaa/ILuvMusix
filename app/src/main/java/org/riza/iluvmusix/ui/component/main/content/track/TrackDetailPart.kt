package org.riza.iluvmusix.ui.component.main.content.track

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.riza.iluvmusix.R
import org.riza.iluvmusix.dataclass.response.main.content.track.TrackAlbumDto
import org.riza.iluvmusix.helper.formatDuration
import org.riza.iluvmusix.ui.component.main.content.util.ExplicitBox
import org.riza.iluvmusix.ui.component.main.content.util.PreviewPlayerButton
import org.riza.iluvmusix.ui.component.state.loading.LoadingBox

@Composable
fun TrackDetailPart(
    modifier: Modifier = Modifier,
    album: TrackAlbumDto?,
    title: String?,
    bgColor: Color,
    isLightColor: Boolean,
    imageRequest: ImageRequest,
    isExplicit: Boolean?,
    durationMs: Long?,
    artists: String?,
    urlPreview: String?,
    previewIsLoading: Boolean,
    onArtistClick: () -> Unit,
    onAlbumClick: (id: String, title: String) -> Unit,
){
    LocalContext.current
    Box(
        modifier = modifier
    ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = {
                                onAlbumClick(
                                    album?.id ?: "",
                                    album?.name ?: ""
                                )
                            }),
                        text = title?: "Unknown",
                        style = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Start
                        )
                    )
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = { onArtistClick() }),
                        text = artists ?: "Unknown",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start
                        )
                    )
                    Text(
                        text = formatDuration(durationMs = durationMs),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start
                        )
                    )
                }
                if(isExplicit == true){
                    ExplicitBox(
                        bgColor = bgColor,
                        contentColor = if(isLightColor) colorResource(R.color.black) else colorResource(R.color.black)
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                if(previewIsLoading){
                    LoadingBox(
                        size = 64.dp,
                        color = bgColor,
                        trackColor = colorResource(R.color.black)
                    )
                }
                else{
                    urlPreview?.let {
                        PreviewPlayerButton(
                            previewUrl = it,
                            ringColor = bgColor
                        )
                    }
                }
            }
        }
    }
}