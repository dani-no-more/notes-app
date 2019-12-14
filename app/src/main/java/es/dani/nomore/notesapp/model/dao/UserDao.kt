package es.dani.nomore.notesapp.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.dani.nomore.notesapp.model.entities.User

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM user_table WHERE userId = :key")
    fun get(key: Long): User?

    @Query("SELECT * FROM user_table ORDER BY username ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("DELETE FROM user_table")
    fun clear()
}
