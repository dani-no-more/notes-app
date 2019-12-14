package es.dani.nomore.notesapp.model

import androidx.room.Database
import androidx.room.RoomDatabase
import es.dani.nomore.notesapp.model.dao.UserDao
import es.dani.nomore.notesapp.model.entities.User


@Database(entities = arrayOf(User::class), version = 1)
abstract class NotesDatabase: RoomDatabase() {
    abstract fun UserDao(): UserDao
}
