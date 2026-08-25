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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.riza.iluvmusix.dataclass.response.main.content.track.TrackAlbumDto
import org.riza.iluvmusix.helper.function.formatDuration
import org.riza.iluvmusix.ui.component.main.content.util.ExplicitBox

@Composable
fun TrackDetailPart(
    modifier: Modifier = Modifier,
    album: TrackAlbumDto?,
    title: String?,
    isExplicit: Boolean?,
    durationMs: Long?,
    artists: String?,
    onArtistClick: () -> Unit,
    onAlbumClick: (id: String, title: String) -> Unit,
){
    val cover = album?.images?.firstOrNull()?.url
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
                    model = cover,
                    contentDescription = null,
                    modifier = Modifier.size(300.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = {onAlbumClick(album?.id?:"", album?.name?:"")}),
                        text = title?: "Unknown",
                        style = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Start
                        )
                    )
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = {onArtistClick()}),
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
                    ExplicitBox()
                }
            }
        }
    }
}