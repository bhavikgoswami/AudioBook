package com.book.audiobook.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import com.book.audiobook.R
import com.book.audiobook.databinding.FavoriteBookAdapterBinding
import com.book.audiobook.model.AudioBook
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * This Adapter class is used to show list of Book with details
 */
class FavoritesBooksAdapter(
    var context: Context,
    private var bookList: ArrayList<AudioBook?>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FavoritesBooksAdapter.ViewHolder>() {
    private lateinit var binding: FavoriteBookAdapterBinding

    interface OnItemClickListener {
        fun onItemClick(audioBook: ArrayList<AudioBook?>,position: Int)
        fun onRemove(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.favorite_book_item, parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if (bookList[position] != null) {
                val book = bookList[position]
                bookAuthorNameTxtView.text = book!!.bookAuthorName
                bookTitleTxtView.text = book.bookName

                Glide.with(context)
                    .load(book.bookImageUrl)
                    .transform(CenterCrop())
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.splash)
                    .into(bookImageView)

                root.setOnClickListener { view: View? ->
                    onItemClickListener.onItemClick(bookList,position)
                }
                /*  readTxtView.setOnClickListener { view: View? ->
                       onItemClickListener.onItemClick(view,
                           position)
                   }*/
            }
        }
        holder.binding.removeButton.setOnClickListener {
            onItemClickListener.onRemove(position)

        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class ViewHolder(val binding: FavoriteBookAdapterBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updatedList: ArrayList<AudioBook?>) {
        bookList = updatedList
        notifyDataSetChanged()
    }
}