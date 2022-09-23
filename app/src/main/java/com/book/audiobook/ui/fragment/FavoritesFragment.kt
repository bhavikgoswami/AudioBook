package com.book.audiobook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.book.audiobook.R
import com.book.audiobook.databinding.FavoritesFragmentBinding
import com.book.audiobook.model.AudioBook
import com.book.audiobook.viewmodel.AudioBookViewModel
import com.book.audiobook.ui.adapter.FavoritesBooksAdapter
import com.book.audiobook.utils.Constants


class FavoritesFragment : Fragment(), FavoritesBooksAdapter.OnItemClickListener {
    private lateinit var viewModel: AudioBookViewModel
    private lateinit var audioBookAdapter: FavoritesBooksAdapter
    private var audioBookList: ArrayList<AudioBook?>? = null
    private lateinit var navController: NavController
    lateinit var binding: FavoritesFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_favorite,
            container,
            false
        ) // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioBookList = ArrayList()

        // Instantiate the navController using the NavHostFragment
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val backStackEntry = navController.getBackStackEntry(R.id.nav_graph)
        viewModel = ViewModelProvider(
            backStackEntry,
            HiltViewModelFactory(requireContext(), backStackEntry)
        )[AudioBookViewModel::class.java]

        setBookRecyclerView()
        viewModel.init()
        observeData()

    }

    /**
     * Set Recyclerview with empty book list and managed item click listener
     */
    private fun setBookRecyclerView() {

        // click on book or read book navigate to bookDetails fragment
        audioBookAdapter = FavoritesBooksAdapter(
            requireContext(),
            audioBookList!!, this@FavoritesFragment
        )
        binding.apply {
            audioBooksRecyclerView.setHasFixedSize(true)
            audioBooksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            audioBooksRecyclerView.adapter = audioBookAdapter
        }
    }

    /**
     * This method observes the book data from the database
     */
    private fun observeData() {
        viewModel.bookFavoriteList!!.observe(
            viewLifecycleOwner
        ) { updatedBookList: List<AudioBook?>? ->
            if (updatedBookList != null && updatedBookList.isNotEmpty()) {
                audioBookList!!.clear()
                audioBookList!!.addAll(updatedBookList)

                audioBookAdapter.updateList(audioBookList!!)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoritesFragment().apply {}
    }

    override fun onItemClick(audioBook: java.util.ArrayList<AudioBook?>, position: Int) {
        val bundle = Bundle().apply {
            putSerializable(Constants.AUDIOBOOKList, audioBook)
            putInt(Constants.POSITION, position)
        }
        findNavController().navigate(R.id.action_libraryFragment_to_audioBookDetailFragment, bundle)

    }

    override fun onRemove(position: Int) {
        viewModel.setAsFavorite(false, audioBookList!![position!!]!!.bookId)
        audioBookList!!.remove(audioBookList!![position])
        audioBookAdapter.notifyDataSetChanged()

    }
}