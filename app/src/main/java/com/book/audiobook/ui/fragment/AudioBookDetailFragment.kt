package com.book.audiobook.ui.fragment


import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.book.audiobook.R
import com.book.audiobook.databinding.AudioBookDetailFragmentBinding
import com.book.audiobook.model.AudioBook
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter


class AudioBookDetailFragment : Fragment() {

    private lateinit var audioBook: AudioBook
    private lateinit var binding: AudioBookDetailFragmentBinding
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_audio_book_detail, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioBook = arguments?.getSerializable("audioBook") as AudioBook
        binding.audioBook = audioBook
        Glide.with(this@AudioBookDetailFragment)
            .load(audioBook.bookImageUrl)
            .transform(CenterCrop())
            .thumbnail(0.05f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.splash)
            .into(binding.audioBookCover)

        playSong(false,audioBook.bookAudioUrl)
    }

    private fun playSong(isAlreadyPlaying: Boolean, Url: String) {
        var filePath = Url.substring(Url.lastIndexOf('/') + 1, Url.length)
        val tempName: String = filePath.replace(".", "_")
        val name = tempName.split("_").toTypedArray()
        if (!isAlreadyPlaying) {
            player = if (player != null && player.playWhenReady) {
                player.stop()
                player.release()
                ExoPlayerFactory.newSimpleInstance(
                    this,
                    DefaultTrackSelector(DefaultBandwidthMeter.Builder().build())
                )
            } else {
                ExoPlayer. .newSimpleInstance(
                    this,
                    DefaultTrackSelector(DefaultBandwidthMeter.Builder().build())
                )
            }
            val dialog = ProgressDialog(requireContext())
            dialog.setMessage(getString(R.string.buffering))
            dialog.setCancelable(true)
            dialog.show()
            val dataSourceFactory: DataSource.Factory = CacheDataSourceFactory(
                VideoCache.getInstance(
                    name[0]
                ), DefaultDataSourceFactory(this, "brahmakumaris")
            )
            val mediaSource: MediaSource =
                Factory(dataSourceFactory).createMediaSource(Uri.parse(Url))
            player.prepare(mediaSource)
            player.playWhenReady = true
            player.seekToDefaultPosition()
            player.addListener(object : EventListener() {
                fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}
                fun onTracksChanged(
                    trackGroups: TrackGroupArray?,
                    trackSelections: TrackSelectionArray?
                ) {
                }

                fun onLoadingChanged(isLoading: Boolean) {}
                fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                        dialog.cancel()
                    }
                }

                fun onRepeatModeChanged(repeatMode: Int) {}
                fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
                fun onPlayerError(error: ExoPlaybackException?) {}
                fun onPositionDiscontinuity(reason: Int) {
                    //THIS METHOD GETS CALLED FOR EVERY NEW SOURCE THAT IS PLAYED
                    dialog.cancel()
                }

                fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
                fun onSeekProcessed() {}
            })
            //            imageview.setImageResource(R.drawable.ic_pause);
//            imageview.setTag(true);
        } else {
//            imageview.setTag(false);
            player.playWhenReady = false
            player.stop()
            stopMediaPlayer()

//            imageview.setImageResource(R.drawable.ic_play);
        }
    }
}

