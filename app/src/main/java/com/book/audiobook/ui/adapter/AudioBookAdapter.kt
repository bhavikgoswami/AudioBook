package com.witnovus.book.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import com.book.audiobook.R
import com.book.audiobook.databinding.AudioBookAdapterBinding
import com.book.audiobook.model.AudioBook

/**
 * This Adapter class is used to show list of Book with details
 */
class AudioBookAdapter(
    var context: Context,
    private var bookList: ArrayList<AudioBook?>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AudioBookAdapter.ViewHolder>() {
    private lateinit  var binding: AudioBookAdapterBinding
    private lateinit var authorName: String
    private lateinit var bookPublishInformation: String

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.audio_book_item, parent,
            false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            if (bookList[position] != null) {
                val book = bookList[position]

                bookAuthorNameTxtView.text = book!!.bookName

            /*  bookCardView.setOnClickListener { view: View? ->
                    onItemClickListener.onItemClick(view,
                        position)
                }
              readTxtView.setOnClickListener { view: View? ->
                    onItemClickListener.onItemClick(view,
                        position)
                }*/
            }
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class ViewHolder(val binding: AudioBookAdapterBinding) : RecyclerView.ViewHolder(
        binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updatedList: ArrayList<AudioBook?>) {
        bookList = updatedList
        notifyDataSetChanged()
    }
}