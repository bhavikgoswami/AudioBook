package com.book.audiobook.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.book.audiobook.model.AudioBook

/**
 * Database management class
 */
@Database(entities = [AudioBook::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): AudioBookDao?
}