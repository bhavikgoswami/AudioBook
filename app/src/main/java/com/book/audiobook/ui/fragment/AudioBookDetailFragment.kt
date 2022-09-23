package com.book.audiobook.ui.fragment


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.book.audiobook.R
import com.book.audiobook.databinding.AudioBookDetailFragmentBinding
import com.book.audiobook.model.AudioBook
import com.book.audiobook.utils.Constants
import com.book.audiobook.viewmodel.AudioBookViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.net.URLConnection


class AudioBookDetailFragment : Fragment(), Player.Listener {

    private lateinit var audioBook: ArrayList<AudioBook?>
    private lateinit var binding: AudioBookDetailFragmentBinding
    private lateinit var player: ExoPlayer
    private var position: Int? = null
    private lateinit var mediaItems: MutableList<MediaSource>
    private lateinit var viewModel: AudioBookViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_audio_book_detail, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate the navController using the NavHostFragment
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val backStackEntry = navController.getBackStackEntry(R.id.nav_graph)
        viewModel = ViewModelProvider(
            backStackEntry,
            HiltViewModelFactory(requireContext(), backStackEntry)
        )[AudioBookViewModel::class.java]


        audioBook = arguments?.getSerializable(Constants.AUDIOBOOKList) as ArrayList<AudioBook?>
        position = arguments?.getInt(Constants.POSITION) as Int
        binding.audioBook = audioBook[position!!]
        binding.playerView.controllerShowTimeoutMs = 0
        updateUIComponents(position!!)

        // create list of mediaSources
        mediaItems = ArrayList()
        // added mediaSource items
        for (i in 0 until audioBook.size) {
            mediaItems.add(buildMediaSource(audioBook[i]!!.bookAudioUrl))
        }

    }

    private fun updateUIComponents(position: Int) {

        binding.bookTitleTxtView.text = audioBook[position]?.bookName
        binding.bookAuthorNameTxtView.text = audioBook[position]?.bookAuthorName

        if (!isDetached)
            Glide.with(this@AudioBookDetailFragment)
                .load(audioBook[position]?.bookImageUrl)
                .transform(CenterCrop())
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.splash)
                .into(binding.audioBookCover)
    }

    private fun initPlayer() {

        // Create a player instance.
        player = ExoPlayer.Builder(requireContext()).build()

        // Bind the player to the view.
        binding.playerView.player = player
        binding.playerView.controllerAutoShow = true

        //setting exoplayer when it is ready.
        player.playWhenReady = true


        // Set the media source to be played.
        player.setMediaSources(mediaItems, false)

        // Prepare the player.
        if (!player.isPlaying)
            player.prepare()

        init()
        initListeners()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            // Active playback.
        } else {
            position = position?.plus(1)
            updateUIComponents(position!!)
        }
    }

    private fun initListeners() {
        binding.arrowDownImageView.setOnClickListener {
            player.stop()
            findNavController().navigate(R.id.action_audioBookDetailFragment_to_libraryFragment)
        }

    }
    private fun init() {
// handles next button Listener
        val nextBtn = binding.playerView.findViewById<AppCompatImageView>(R.id.exo_next)
        nextBtn.setOnClickListener {
            player.stop()
            if (position == audioBook.size) {
                position = 0
            } else {
                position = position?.plus(1)
            }
            updateUIComponents(position!!)
            player.setMediaSources(mediaItems, position!!, 1)
            player.prepare()
        }
//handels previous button Listener
        val prevButton = binding.playerView.findViewById<AppCompatImageView>(R.id.exo_prev)
        prevButton.setOnClickListener {
            player.stop()
            if (position == 0) {
                position = audioBook.size
            } else {
                position = position?.minus(1)
            }
            updateUIComponents(position!!)
            player.setMediaSources(mediaItems, position!!, 1)
            player.prepare()
        }

        /// handles mute unmute media
        val soundBtn = binding.playerView.findViewById<AppCompatImageView>(R.id.exo_sound)
        soundBtn.setOnClickListener {
            val currentVolume: Float = player.volume
            if (currentVolume != 0f) {
                soundBtn.setImageResource(R.drawable.ic_mute)
                player.volume = 0f
            } else {
                soundBtn.setImageResource(R.drawable.ic_volume_up)
                player.volume = 1f
            }
        }
        val shareBtn = binding.playerView.findViewById<AppCompatImageView>(R.id.exo_share)
        shareBtn.setOnClickListener {
            val uri = Uri.parse(audioBook[position!!]!!.bookAudioUrl)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, audioBook[position!!]!!.bookAudioUrl);
     //       shareIntent.putExtra(Intent.EXTRA_STREAM,uri )
        //    shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            shareIntent.type = "text/plain"

            startActivity(Intent.createChooser(shareIntent, "share.."))
        }


        /// handles speed  of mediaItem
        val speedDecreaseBtn =
            binding.playerView.findViewById<AppCompatImageView>(R.id.exo_speed_decrease)
        speedDecreaseBtn.setOnClickListener {
            if (player.playbackParameters.speed == (Constants.SPEED_5F.toFloat())) {
                Toast.makeText(
                    requireContext(),
                    "Speed decreased " + Constants.SPEED_4F,
                    Toast.LENGTH_SHORT
                ).show()
                player.playbackParameters = PlaybackParameters(4f)
            } else if (player.playbackParameters.speed == (Constants.SPEED_4F.toFloat())) {
                Toast.makeText(
                    requireContext(),
                    "Speed decreased " + Constants.SPEED_3F,
                    Toast.LENGTH_SHORT
                ).show()
                player.playbackParameters = PlaybackParameters(3f)
            } else if (player.playbackParameters.speed == (Constants.SPEED_3F.toFloat())) {
                Toast.makeText(
                    requireContext(),
                    "Speed decreased " + Constants.SPEED_2F,
                    Toast.LENGTH_SHORT
                ).show()
                player.playbackParameters = PlaybackParameters(2f)
            } else if (player.playbackParameters.speed == Constants.SPEED_2F.toFloat()) {
                Toast.makeText(
                    requireContext(),
                    "Speed decreased " + Constants.SPEED_1F,
                    Toast.LENGTH_SHORT
                ).show()
                player.playbackParameters = PlaybackParameters(1f)
            } else {


                Toast.makeText(
                    requireContext(),
                    "Minimum 1f speed required. " + Constants.SPEED_1F,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val speedBtn = binding.playerView.findViewById<AppCompatImageView>(R.id.exo_playback_speed)
        speedBtn.setOnClickListener {

            if (player.playbackParameters.speed == (Constants.SPEED_1F.toFloat())) {
                Toast.makeText(
                    requireContext(),
                    "Speed Increased " + Constants.SPEED_2F,
                    Toast.LENGTH_SHORT
                ).show()
                player.playbackParameters = PlaybackParameters(2f)
            } else if (player.playbackParameters.speed == (Constants.SPEED_2F.toFloat())) {
                Toast.makeText(
                    requireContext(),
                    "Speed Increased " + Constants.SPEED_3F,
                    Toast.LENGTH_LONG
                ).show()
                player.playbackParameters = PlaybackParameters(3f)
            } else if (player.playbackParameters.speed == (Constants.SPEED_3F.toFloat())) {
                Toast.makeText(
                    requireContext(),
                    "Speed Increased " + Constants.SPEED_4F,
                    Toast.LENGTH_SHORT
                ).show()
                player.playbackParameters = PlaybackParameters(4f)
            } else if (player.playbackParameters.speed == Constants.SPEED_4F.toFloat()) {
                Toast.makeText(
                    requireContext(),
                    "Speed Increased " + Constants.SPEED_5F,
                    Toast.LENGTH_SHORT
                ).show()
                player.playbackParameters = PlaybackParameters(5f)
            } else if (player.playbackParameters.speed == Constants.SPEED_5F.toFloat()) {

                var playbackParameters = PlaybackParameters(1f)
                player.playbackParameters = playbackParameters
                Toast.makeText(
                    requireContext(),
                    "Speed Increased " + Constants.SPEED_1F,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // download current playing song
        val downloadBtn =
            binding.playerView.findViewById<AppCompatImageView>(R.id.exo_playback_download)
        downloadBtn.setOnClickListener {
            checkPermission()
        }

        // added bookmark
        val bookMarkBtn =
            binding.playerView.findViewById<AppCompatImageView>(R.id.exo_playback_bookmark)
        bookMarkBtn.setOnClickListener {
            viewModel.setAsFavorite(true, audioBook[position!!]!!.bookId)
            Toast.makeText(requireContext(), "Added to Bookmarks", Toast.LENGTH_LONG).show()
            // addToFavorites()
        }
    }

    private fun downloadFile(fileName: String, urlPath: String) {
        Toast.makeText(requireContext(), getString(R.string.lbl_downmoading), Toast.LENGTH_LONG)
            .show()
        val thread = Thread {
            try {
                var count: Int
                val file: File

                val url = URL(urlPath)
                val connection: URLConnection = url.openConnection()
                connection.connect()
                val lenghtOfFile: Int = connection.getContentLength()
                val input: InputStream = connection.getInputStream()
                val SDCardRoot = Environment.getExternalStorageDirectory()
                val folder = File(SDCardRoot, Constants.AUDIOBOOK)
                if (!folder.exists())
                    folder.mkdir()
                file = File(folder, Constants.AUDIOBOOK + "_" + "$fileName" + Constants.MP3)
                val output: OutputStream = FileOutputStream(file)
                val data = ByteArray(1024)
                var total: Long = 0
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    //    showToast("" + (total * 100 / lenghtOfFile).toInt())
                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                input.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Exception", e.message.toString())
            }
        }
        thread.start()
    }

    private fun showToast(s: String) {
        Toast.makeText(requireContext(), "Downloading Progress.." + s, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        player.stop()
    }

    override fun onStop() {
        super.onStop()
        player.stop()
        /*if (Util.SDK_INT >= 24) {
            releasePlayer()
        }*/
    }


    private fun releasePlayer() {
        if (player == null) {
            return
        }
        //release player when done
        player.release()
    }

    //creating mediaSource
    private fun buildMediaSource(audioURL: String): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        // Create a progressive media source pointing to a stream uri.
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(audioURL))

        return mediaSource
    }

    // check permissions for download media file
    private fun checkPermission() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // on permission check download file
                    downloadFile(
                        audioBook[position!!]!!.bookName,
                        audioBook[position!!]!!.bookAudioUrl
                    )
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Toast.makeText(requireActivity(), it.name, Toast.LENGTH_SHORT)
                    .show()
            }
            .check()
    }
}

