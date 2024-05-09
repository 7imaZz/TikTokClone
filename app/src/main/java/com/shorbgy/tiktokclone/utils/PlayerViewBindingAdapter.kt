package com.shorbgy.tiktokclone.utils

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.shorbgy.tiktokclone.MyApp

class PlayerViewAdapter {


    @SuppressLint("StaticFieldLeak")
    companion object{
        private lateinit var httpDataSourceFactory: HttpDataSource.Factory
        private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
        private lateinit var cacheDataSourceFactory: DataSource.Factory
        private val simpleCache: SimpleCache = MyApp.simpleCache

        // for hold all players generated
        private var playersMap: MutableMap<Int, ExoPlayer>  = mutableMapOf()
        // for hold current player
        private var currentPlayingVideo: Pair<Int, ExoPlayer>? = null
        fun releaseAllPlayers(){
            playersMap.map {
                it.value.release()
            }
        }

        fun pauseAllPlayers(){
            playersMap.map {
                it.value.pause()
            }
        }

        fun resumeCurrentPlayer(){
            currentPlayingVideo?.second?.playWhenReady = true
        }

        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: Int){
            playersMap[index]?.release()
        }

        // call when scroll to pause any playing player
        private fun pauseCurrentPlayingVideo(){
            if (currentPlayingVideo != null){
                currentPlayingVideo?.second?.apply {
                    playWhenReady = false
                }
            }
        }

        fun playIndexThenPausePreviousPlayer(index: Int){
            if (playersMap[index]?.playWhenReady == false) {
                pauseCurrentPlayingVideo()
                playersMap[index]?.playWhenReady = true
                currentPlayingVideo = Pair(index, playersMap[index]!!)
            }

        }

        /*
        *  url is a url of stream video
        *  progressbar for show when start buffering stream
        * thumbnail for show before video start
        * */
        fun StyledPlayerView.loadVideo(url: String, callback: PlayerStateCallback, progressbar: ProgressBar, item_index: Int? = null, autoPlay: Boolean = false) {
            httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
            defaultDataSourceFactory = DefaultDataSourceFactory(
                this.context, httpDataSourceFactory
            )

            cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(simpleCache)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            val player = ExoPlayer.Builder(context).setMediaSourceFactory(DefaultMediaSourceFactory(
                cacheDataSourceFactory)
            ).build()

            player.playWhenReady = autoPlay
            player.repeatMode = Player.REPEAT_MODE_ALL
            this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            // We'll show the controller, change to true if want controllers as pause and start
            this.useController = false
            // Provide url to load the video from here
//            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(Uri.parse(url))
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)
            player.setMediaSource(mediaSource, true)
            this.player = player

            player.prepare()




            // add player with its index to map
            if (playersMap.containsKey(item_index))
                playersMap.remove(item_index)
            if (item_index != null)
                playersMap[item_index] = player

            this.player!!.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == ExoPlayer.STATE_BUFFERING) {
                        callback.onVideoBuffering(player)
                        // Buffering..
                        // set progress bar visible here
                        // set thumbnail visible
                        progressbar.visibility = View.VISIBLE
                    }
                    if (playbackState == ExoPlayer.STATE_READY) {
                        // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                        progressbar.visibility = View.GONE
                        // set thumbnail gone
                        callback.onVideoDurationRetrieved(this@loadVideo.player!!.duration, player)
                    }
                    if (playbackState == Player.STATE_READY && player.playWhenReady){
                        // [PlayerView] has started playing/resumed the video
                        callback.onStartedPlaying(player)
                    }
                }
            })
        }
    }
}