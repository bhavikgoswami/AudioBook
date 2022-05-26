package com.book.audiobook.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.book.audiobook.db.AppDatabase
import com.book.audiobook.model.AudioBook
import com.book.audiobook.model.AudioBookResponse
import com.book.audiobook.network.AudioBookAPIService
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is repository class to manage the database and API's operation
 */
@Singleton
class AudioBookRepository @Inject constructor() {
    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var apiService: AudioBookAPIService

    @Inject
    lateinit var applicationContext: Application

    // GET API to get List of Book Response
    fun getBooksResponse(): Observable<AudioBookResponse> {
        return apiService.getAudioBookList()
    }

    // Get list of Book from the database
    fun getBookList(): LiveData<List<AudioBook?>?> {
        return appDatabase.bookDao()!!.getBooks()
    }


    // Insert book in book details in database
    fun insertAllBooks(bookList: List<AudioBook?>?) {
        appDatabase.bookDao()!!.insertAllBooks(bookList)
    }

    // Get Book from the dabase for the selected book ID
    fun getSelectedBook(id: Int): AudioBook? {
        return appDatabase.bookDao()!!.getSelectedBook(id)
    }


}