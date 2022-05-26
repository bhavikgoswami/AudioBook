package com.book.audiobook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.book.audiobook.R
import com.book.audiobook.databinding.LibraryFragmentBinding
import com.book.audiobook.model.AudioBook
import com.book.audiobook.utils.Constants
import com.book.audiobook.utils.Utils
import com.book.audiobook.viewmodel.AudioBookViewModel
import com.witnovus.book.ui.adapters.AudioBookAdapter
import io.paperdb.Paper

class LibraryFragment : Fragment() ,AudioBookAdapter.OnItemClickListener {

    private lateinit var viewModel: AudioBookViewModel
    private lateinit var binding: LibraryFragmentBinding
    private lateinit var audioBookAdapter: AudioBookAdapter
    private var audioBookList: ArrayList<AudioBook?>? = null
    private lateinit var navController: NavController

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LibraryFragment.
         */
        @JvmStatic
        fun newInstance() = LibraryFragment().apply {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false) // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioBookList = ArrayList()

        // Instantiate the navController using the NavHostFragment
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val backStackEntry = navController.getBackStackEntry(R.id.audio_nav_graph)
        viewModel = ViewModelProvider(backStackEntry,
            HiltViewModelFactory(requireContext(), backStackEntry)
        )[AudioBookViewModel::class.java]

        // set RecyclerView
        setBookRecyclerView()
        viewModel.init()
        observeData()
        if (!Paper.book().contains(Constants.IS_BOOK_FETCHED_FROM_API)) {
            getBookListAPI()
        }
    }


    /**
     * This method calls API and observers the response.
     */
    private fun getBookListAPI() {
        // checks internet is connected or not
        if (!Utils.checkInternetConnection(requireContext())) {
            Toast.makeText(requireContext(),
                getString(R.string.msg_please_check_your_connection),
                Toast.LENGTH_LONG).show()
        }
        // set observer for BookList which gets from API
        if (!viewModel.bookList.hasObservers()) {
            viewModel.bookList.observe(viewLifecycleOwner
            ) {
                Utils.hideProgressDialog()
            }
        }
        // show Progress dialog

        Utils.showProgressDialog(requireContext())
        //Call GetBook API
        viewModel.getBookListAPI()
    }

    /**
     * Set Recyclerview with empty book list and managed item click listener
     */
    private fun setBookRecyclerView() {

        // click on book or read book navigate to bookDetails fragment
        audioBookAdapter = AudioBookAdapter(requireContext(),
            audioBookList!!, this@LibraryFragment)
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
        viewModel.bookLiveList!!.observe(viewLifecycleOwner
        ) { updatedBookList: List<AudioBook?>? ->
            if (updatedBookList != null && updatedBookList.isNotEmpty()) {
                audioBookList!!.clear()
                audioBookList!!.addAll(updatedBookList)

                audioBookAdapter.updateList(audioBookList!!)

            }
        }
    }

    override fun onItemClick(view: View?, position: Int) {

    }


}