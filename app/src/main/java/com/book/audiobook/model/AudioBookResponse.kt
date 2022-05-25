package com.book.audiobook.model


import com.google.gson.annotations.SerializedName

data class AudioBookResponse(
    @SerializedName("AudioBookList")
    val audioBookList: List<AudioBook>
)