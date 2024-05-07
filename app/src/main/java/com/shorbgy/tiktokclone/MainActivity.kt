package com.shorbgy.tiktokclone

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import com.shorbgy.tiktokclone.databinding.ActivityMainBinding
import com.shorbgy.tiktokclone.utils.PlayerViewAdapter
import com.shorbgy.tiktokclone.utils.RecyclerViewScrollListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter: VideosAdapter by lazy { VideosAdapter() }

    // for handle scroll and get first visible item index
    private lateinit var scrollListener: RecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.videosRv.adapter = adapter

        PagerSnapHelper().attachToRecyclerView(binding.videosRv)

        scrollListener = object : RecyclerViewScrollListener() {
            override fun onItemIsFirstVisibleItem(index: Int) {
                Log.d("visible item index", index.toString())
                // play just visible item
                if (index != -1)
                    PlayerViewAdapter.playIndexThenPausePreviousPlayer(index)
            }

        }
        binding.videosRv.addOnScrollListener(scrollListener)
        getVideos()
    }

    private fun getVideos(){
        getAllVideos().addSnapshotListener { snapshot, _ ->
            snapshot?.let { s ->
                val allVideos = s.toObjects(Video::class.java)
                adapter.submitList(allVideos)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        PlayerViewAdapter.pauseAllPlayers()
    }

    override fun onResume() {
        super.onResume()
        PlayerViewAdapter.resumeCurrentPlayer()
    }
}