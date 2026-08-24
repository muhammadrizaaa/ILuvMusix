package org.riza.iluvmusix.ui.component.main.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.riza.iluvmusix.R
import org.riza.iluvmusix.dataclass.response.main.AlbumDto
import org.riza.iluvmusix.dataclass.response.main.ArtistDto
import org.riza.iluvmusix.dataclass.response.main.SearchResponse
import org.riza.iluvmusix.dataclass.response.main.TrackDto

@Composable
fun SearchResult(
    modifier: Modifier = Modifier,
    data: SearchResponse?,
    onAlbumCLick: (id: String) -> Unit,
    onTrackClick: (id: String) -> Unit,
    onArtistClick: (id: String) -> Unit
){
    if(data != null){
        val artists = data.artists
        val albums = data.albums
        val tracks = data.tracks
        Box(
            modifier = modifier
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
            ) {
                artists?.items?.let {
                    if(it.isNotEmpty()){
                        items(it){ data->
                            ArtistBox(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = {onArtistClick(data.id)}),
                                data = data
                            )
                        }
                    }
                }
                albums?.items?.let {
                    if(it.isNotEmpty()){
                        items(it){ data->
                            AlbumBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = {onAlbumCLick(data.id)})
                                ,
                                data = data
                            )
                        }
                    }
                }
                tracks?.items?.let {
                    if(it.isNotEmpty()){
                        items(it){ data->
                            TrackBox(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = {onTrackClick(data.id)}),
                                data = data
                            )
                        }
                    }
                }
            }
        }
    }
    else{
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Search Somethin'",
                fontWeight = FontWeight.W700,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.black)
            )
        }
    }
}

@Composable
fun TrackBox(
    modifier: Modifier = Modifier,
    data: TrackDto,
    bgColor: Color = colorResource(R.color.main).copy(alpha = 0.8f),
    contentColor: Color = colorResource(R.color.white)
){
    val artist = data.artists?.joinToString(", ") { it.name }
    Box(
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            )
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_music_note_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    lineHeight = 4.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor
                )
                data.album?.let { album ->
                    Text(
                        text = album.name,
                        fontWeight = FontWeight.W500,
                        fontSize = 8.sp,
                        lineHeight = 4.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )
                }
                artist?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.W500,
                        fontSize = 8.sp,
                        lineHeight = 4.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )
                }
            }
            if(data.explicit == true){
                Box(
                    modifier = Modifier.wrapContentSize()
                        .background(
                            color = contentColor,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
                        text = "E",
                        fontWeight = FontWeight.W900,
                        fontSize = 12.sp,
                        color = bgColor
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumBox(
    modifier: Modifier = Modifier,
    data: AlbumDto,
    bgColor: Color = colorResource(R.color.main).copy(alpha = 0.8f),
    contentColor: Color = colorResource(R.color.white)
){
    val artist = data.artists?.joinToString(", ") { it.name }
    val image = data.images?.firstOrNull()
    Box(
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            )
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(data.images?.isNotEmpty() == true){
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
                ){
                    AsyncImage(
                        model = image?.url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            else{
                Icon(
                    painter = painterResource(R.drawable.baseline_music_note_24),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = contentColor
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    lineHeight = 4.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor
                )
                artist?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.W500,
                        fontSize = 8.sp,
                        lineHeight = 4.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistBox(
    modifier: Modifier = Modifier,
    data: ArtistDto,
    bgColor: Color = colorResource(R.color.main).copy(alpha = 0.8f),
    contentColor: Color = colorResource(R.color.white)
){
    val image = data.images?.firstOrNull()
    Box(
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            )
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(data.images?.isNotEmpty() == true){
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                ){
                    AsyncImage(
                        model = image?.url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            else{
                Icon(
                    painter = painterResource(R.drawable.baseline_music_note_24),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = contentColor
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor
                )
            }
        }
    }
}