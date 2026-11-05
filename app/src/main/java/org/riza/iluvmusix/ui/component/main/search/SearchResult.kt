package org.riza.iluvmusix.ui.component.main.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.riza.iluvmusix.R
import org.riza.iluvmusix.dataclass.response.main.AlbumDto
import org.riza.iluvmusix.dataclass.response.main.ArtistDto
import org.riza.iluvmusix.dataclass.response.main.SearchResponse
import org.riza.iluvmusix.dataclass.response.main.TrackDto
import kotlin.math.max
import kotlin.math.min

// --- Sealed Class to Flatten Spotify's Categorized Search Data ---
sealed class SearchResultItem {
    abstract val name: String
    abstract val relevanceScore: Double

    data class Artist(val data: ArtistDto, override val relevanceScore: Double) : SearchResultItem() {
        override val name: String get() = data.name
    }
    data class Album(val data: AlbumDto, override val relevanceScore: Double) : SearchResultItem() {
        override val name: String get() = data.name
    }
    data class Track(val data: TrackDto, override val relevanceScore: Double) : SearchResultItem() {
        override val name: String get() = data.name
    }
}

// --- Levenshtein Distance & Normalized Relevance Calculator ---
private fun calculateRelevance(target: String, query: String): Double {
    val cleanTarget = target.lowercase().trim()
    val cleanQuery = query.lowercase().trim()

    if (cleanTarget.isEmpty() || cleanQuery.isEmpty()) return 0.0
    if (cleanTarget == cleanQuery) return 1.0
    if (cleanTarget.startsWith(cleanQuery)) return 0.9
    if (cleanTarget.contains(cleanQuery)) return 0.85

    val distance = levenshteinDistance(cleanTarget, cleanQuery)
    val maxLength = max(cleanTarget.length, cleanQuery.length)
    return 1.0 - (distance.toDouble() / maxLength)
}

private fun levenshteinDistance(s1: String, s2: String): Int {
    val dp = IntArray(s2.length + 1) { it }
    for (i in 1..s1.length) {
        var prev = dp[0]
        dp[0] = i
        for (j in 1..s2.length) {
            val temp = dp[j]
            dp[j] = if (s1[i - 1] == s2[j - 1]) prev else min(prev, min(dp[j - 1], dp[j])) + 1
            prev = temp
        }
    }
    return dp[s2.length]
}

// --- Main Search Result Composable ---
@Composable
fun SearchResult(
    modifier: Modifier = Modifier,
    data: SearchResponse?,
    searchQuery: String = "",
    onAlbumClick: (id: String) -> Unit,
    onTrackClick: (id: String) -> Unit,
    onArtistClick: (id: String) -> Unit
) {
    if (data == null) {
        // State 1: Prompt user to type something
        EmptyState(
            modifier = modifier,
            title = "Search Somethin'",
            subtitle = "Find your favorite tracks, artists, or albums"
        )
        return
    }

    // Rank and flatten all items dynamically using the query
    val sortedItems = remember(data, searchQuery) {
        val list = mutableListOf<SearchResultItem>()

        data.artists?.items?.forEach { artist ->
            list.add(SearchResultItem.Artist(artist, calculateRelevance(artist.name, searchQuery)))
        }
        data.albums?.items?.forEach { album ->
            list.add(SearchResultItem.Album(album, calculateRelevance(album.name, searchQuery)))
        }
        data.tracks?.items?.forEach { track ->
            list.add(SearchResultItem.Track(track, calculateRelevance(track.name, searchQuery)))
        }

        // Sort descending by calculated relevance score
        list.sortedByDescending { it.relevanceScore }
    }

    if (sortedItems.isEmpty()) {
        // State 2: Query executed, but zero results returned
        EmptyState(
            modifier = modifier,
            title = "No results found",
            subtitle = "Try checking your spelling or searching for another term"
        )
    } else {
        // State 3: Render ranked results list
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
        ) {
            items(
                items = sortedItems,
                key = { item ->
                    when (item) {
                        is SearchResultItem.Artist -> "artist_${item.data.id}"
                        is SearchResultItem.Album -> "album_${item.data.id}"
                        is SearchResultItem.Track -> "track_${item.data.id}"
                    }
                }
            ) { item ->
                when (item) {
                    is SearchResultItem.Artist -> {
                        ArtistBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onArtistClick(item.data.id) },
                            data = item.data
                        )
                    }
                    is SearchResultItem.Album -> {
                        AlbumBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAlbumClick(item.data.id) },
                            data = item.data
                        )
                    }
                    is SearchResultItem.Track -> {
                        TrackBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onTrackClick(item.data.id) },
                            data = item.data
                        )
                    }
                }
            }
        }
    }
}

// --- Empty State Component ---
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

// --- Track Item Box ---
@Composable
fun TrackBox(
    modifier: Modifier = Modifier,
    data: TrackDto,
    bgColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    val artist = data.artists?.joinToString(", ") { it.name }
    val image = data.album?.images?.firstOrNull()

    Box(
        modifier = modifier.background(color = bgColor, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Track Thumbnail
            if (image != null) {
                AsyncImage(
                    model = image.url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_music_note_24),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = contentColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor
                )
                artist?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.W400,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }

            if (data.explicit == true) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                        .background(
                            color = contentColor,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .padding(horizontal = 3.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        lineHeight = 9.sp, // Explicit line height prevents vertical clipping inside small boxes
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
    bgColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    val artist = data.artists?.joinToString(", ") { it.name }
    val image = data.images?.firstOrNull()

    Box(
        modifier = modifier.background(color = bgColor, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (data.images?.isNotEmpty() == true) {
                AsyncImage(
                    model = image?.url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_music_note_24),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = contentColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category badge for Albums
                    Box(
                        modifier = Modifier
                            .background(
                                color = contentColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "ALBUM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.W700,
                            color = contentColor
                        )
                    }
                    artist?.let {
                        Text(
                            text = "•  $it",
                            fontWeight = FontWeight.W500,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistBox(
    modifier: Modifier = Modifier,
    data: ArtistDto,
    bgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val image = data.images?.firstOrNull()
    Box(
        modifier = modifier.background(color = bgColor, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (data.images?.isNotEmpty() == true) {
                AsyncImage(
                    model = image?.url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_music_note_24),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = contentColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor
                )
                Text(
                    text = "Artist",
                    fontWeight = FontWeight.W400,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}