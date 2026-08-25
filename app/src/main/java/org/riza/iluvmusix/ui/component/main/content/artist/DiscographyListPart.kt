package org.riza.iluvmusix.ui.component.main.content.artist

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.riza.iluvmusix.R
import org.riza.iluvmusix.dataclass.response.main.content.album.SimpleAlbumDto

@Composable
fun DiscographyListPart(
    modifier: Modifier,
    data: List<SimpleAlbumDto>,
    onClick: (String) -> Unit
){
    Box(
        modifier = modifier
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.discography),
                style = MaterialTheme.typography.titleMedium
            )
            data.forEach {
                DiscographyItem(
                    modifier = Modifier.fillMaxWidth(),
                    data = it,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun DiscographyItem(
    modifier: Modifier = Modifier,
    data: SimpleAlbumDto,
    onClick: (String) -> Unit
){
    val cover = data.images?.firstOrNull()?.url
    val releaseDateText = "${data.releaseDate}"
    val albumTypeText = data.albumType?.replaceFirstChar { it.uppercase() } ?: ""
    val totalTrackText = if(data.albumType == "single") "" else " - ${data.totalTracks} Tracks"
    val underText = "$albumTypeText$totalTrackText - $releaseDateText"
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = {onClick(data.id)})
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
    ){
        Column(
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    model = cover,
                    contentDescription = null
                )
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = data.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W900
                        )
                    )
                    Text(
                        text = underText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

}