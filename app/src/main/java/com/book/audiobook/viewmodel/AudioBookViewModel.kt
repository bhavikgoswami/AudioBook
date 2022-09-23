package com.book.audiobook.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.book.audiobook.model.AudioBook
import com.book.audiobook.repository.AudioBookRepository
import com.book.audiobook.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.paperdb.Paper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

/**
 * This View Model class manages the API and Database operation
 */
@HiltViewModel
class AudioBookViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: AudioBookRepository

    @Inject
    lateinit var applicationContext: Application
    var bookList = MutableLiveData<ArrayList<AudioBook>>()
    var getSelectedBook = MutableLiveData<AudioBook?>()

    /**
     * This method returns Book List
     *
     * @return bookLiveList
     */
    var bookLiveList: LiveData<List<AudioBook?>?>? = null
    var bookFavoriteList: LiveData<List<AudioBook?>?>? = null


    /**
     * This method get Book List from the database
     */
    fun init() {
        bookLiveList = repository.getBookList()
        bookFavoriteList = repository.getFavoriteBooks(isFavorite = true)
    }

    //Start worker to sync book record with server and update database
    /**
     * This method calls API to get list Of Book from the server
     */
    fun getBookListAPI() {
            repository.getBooksResponse()
                .subscribeOn(Schedulers.io())
                .map { bookResponse ->
                    val bookList: ArrayList<AudioBook> = bookResponse.audioBookList
                    repository.insertAllBooks(bookList)
                    bookList
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: ArrayList<AudioBook> ->
                    bookList.value = result
                    //Set shared preference value
                    Paper.book().write(Constants.IS_BOOK_FETCHED_FROM_API, true)

                }
                ) { error: Throwable ->
                    Log.e(TAG,
                        "getBook API Error: " + error.message)
                }
        }

    /**
     * This method gets Book detail for the selected Book Id
     *
     * @param id : Book Id
     */
    fun getSelectedBook(id: Int) {
        getSelectedBook.value = repository.getSelectedBook(id)
    }

    // Set favorite Book
    fun setAsFavorite(isFavorite: Boolean,id: Int) {
        return repository.setFavoriteBook(isFavorite ,id)
    }


    fun getFavoriteBooks(isFavorite: Boolean): LiveData<List<AudioBook?>?>{
        return repository.getFavoriteBooks(isFavorite)
    }

    companion object {
        private const val TAG = "BookViewModel"
    }
}