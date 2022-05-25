package  com.book.audiobook.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.book.audiobook.model.AudioBook


/**
 * This interface contains all the database related operation methods.
 */
@Dao
interface AudioBookDao {
    // Get list of Book from the database
    /**
     * get bookList from book_table
     */
    @Query("SELECT * FROM audio_book_table")
    fun getBooks(): LiveData<List<AudioBook?>?>

    /**
     * get selected book from book_table
     */
    @Query("SELECT * FROM audio_book_table WHERE bookId = :id")
    fun getSelectedBook(id: Int): AudioBook?


    /**
     * insert selected book in book_table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBooks(book: List<AudioBook?>?)

}