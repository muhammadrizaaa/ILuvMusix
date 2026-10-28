# I Luv Musix - Modern Music Discovery

I Luv Musix is a premium, beautifully designed music discovery application built entirely with **Jetpack Compose**. It focuses on delivering a high-end user experience similar to Spotify and Apple Music, featuring dynamic color theming, in-line audio previews, and rich artist metadata aggregated from multiple public APIs.

## Key Features

* **Dynamic UI Theming**: Extracts the dominant colors from album artwork using the Palette API and seamlessly animates the background gradients to match the music using `animateColorAsState`.
* **Comprehensive Catalog Search**: A powerful discovery engine leveraging the Spotify API, allowing users to instantly search and explore millions of artists, albums, and tracks.
* **Inline Audio Previews**: Uses the iTunes Search API and Media3 (ExoPlayer) to stream 30-second high-quality audio previews directly within the album screen.
* **Premium Typography**: Custom Material 3 typography implementation featuring **Space Grotesk** for punchy, editorial headers and a highly legible sans-serif for metadata.

## Tech Stack

**Core UI & Architecture**
* **Jetpack Compose**: 100% declarative UI toolkit.
* **Material Design 3**: Leveraging custom `ColorScheme` and `Typography`.
* **ViewModel & Coroutines**: For asynchronous data fetching and state management.

**Media & Image Loading**
* **Coil**: Fast, lightweight image loading with crossfade animations.
* **Palette API**: For on-the-fly dominant color extraction from Bitmaps.
* **Media3 / ExoPlayer**: Lifecycle-aware audio streaming for track previews with custom Compose UI controls.

**Networking & Data**
* **Retrofit 2 & OkHttp 3**: Type-safe REST client for multiple API integrations.
* **Gson / Kotlinx Serialization**: JSON parsing.

## API Integrations

This app creates a hybrid data experience by aggregating data from the best music databases:
1. **Spotify API**: Core catalog data (Artists, Albums, Tracklists, Release Dates).
2. **iTunes Search API**: Free, keyless retrieval of 30-second `.m4a` track previews.
3. **Last.fm API**: Crowdsourced artist biographies, tags, and playcount statistics. (TODO)
4. **MusicBrainz / Cover Art Archive**: Alternate artwork (vinyl discs, back covers) and deep metadata. (TODO)

### Prerequisites
* Android Studio (Giraffe or newer recommended)
* Minimum SDK: API 24 (Android 7.0)
* Target SDK: API 34 (Android 14)


## # V-1.1 Log
* Update UI
* add playlist getter from spotify (more platform is coming if possible)