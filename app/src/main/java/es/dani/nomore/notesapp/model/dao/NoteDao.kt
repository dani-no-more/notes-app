package es.dani.nomore.notesapp.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.dani.nomore.notesapp.model.entities.Note


@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM note_table WHERE noteId = :key")
    fun get(key: Long): Note?

    @Query("SELECT * FROM note_table WHERE owner = :userId ORDER BY last_modification_time DESC")
    fun getNotesByUser(userId: Long): LiveData<List<Note>>

    @Query("DELETE FROM user_table")
    fun clear()
}
