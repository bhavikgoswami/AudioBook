package com.book.audiobook.network

import com.book.audiobook.model.AudioBookResponse
import com.book.audiobook.utils.Constants
import retrofit2.http.GET
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * This is interface class contains all the API interface methods
 */
interface AudioBookAPIService {
    /**
     * Get the list of Audio Book
     * @return AudioBookResponse class which contains list of  Audio Book
     */
    @GET(Constants.END_POINT_GET_BOOKS)
    fun getAudioBookList(): Observable<AudioBookResponse>

}