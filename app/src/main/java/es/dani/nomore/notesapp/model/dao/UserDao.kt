package es.dani.nomore.notesapp.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.dani.nomore.notesapp.model.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user_table WHERE userId = :key")
    suspend fun get(key: Long): User?

    @Query("SELECT * FROM user_table WHERE email = :user")
    suspend fun getUser(user: String): User?

    @Query("SELECT * FROM user_table ORDER BY username ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("DELETE FROM user_table")
    suspend fun clear()
}
