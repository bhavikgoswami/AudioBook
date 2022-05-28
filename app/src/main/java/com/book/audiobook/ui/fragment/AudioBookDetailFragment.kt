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

    }

}

