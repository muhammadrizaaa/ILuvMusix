package org.riza.iluvmusix.ui.component.main.content.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import org.riza.iluvmusix.R
import org.riza.iluvmusix.dataclass.response.main.content.album.AlbumTrackDto
import org.riza.iluvmusix.ui.component.main.content.util.ExplicitBox

@Composable
fun AlbumListTrackPart(
    modifier: Modifier = Modifier,
    data: List<AlbumTrackDto>
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        data.forEachIndexed { index, dto ->
            val artistName = dto.artists?.joinToString(",") { it.name }
            AlbumTrackItem(
                modifier = Modifier.fillMaxWidth(),
                title = dto.name,
                artists = artistName?:"",
                index = index+1,
                trackNumber = dto.trackNumber,
                isExplicit = dto.explicit
            )
            if(index != data.size-1){
                HorizontalDivider(
                    color = colorResource(R.color.main),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun AlbumTrackItem(
    modifier: Modifier = Modifier,
    title: String,
    artists: String,
    index: Int,
    trackNumber: Int?,
    isExplicit: Boolean?
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${(trackNumber?:index)}.",
            style = MaterialTheme.typography.titleMedium
        )
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = artists,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colorResource(R.color.black).copy(alpha = 0.75f)
                )
            )
        }
        if(isExplicit == true){
            ExplicitBox()
        }
    }
}