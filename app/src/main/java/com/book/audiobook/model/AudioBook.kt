package com.book.audiobook.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "audio_book_table")
data class AudioBook(
    @SerializedName("book_id")
    @PrimaryKey val bookId: Int,
    @SerializedName("book_audio_url")
    val bookAudioUrl: String,
    @SerializedName("book_author_name")
    val bookAuthorName: String,
    @SerializedName("book_description")
    val bookDescription: String,
    @SerializedName("book_image_url")
    val bookImageUrl: String,
    @SerializedName("book_language")
    val bookLanguage: String,
    @SerializedName("book_name")
    val bookName: String
) : Serializable