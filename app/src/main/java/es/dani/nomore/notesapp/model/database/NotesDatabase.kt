package es.dani.nomore.notesapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.dani.nomore.notesapp.model.dao.NoteDao
import es.dani.nomore.notesapp.model.dao.UserDao
import es.dani.nomore.notesapp.model.entities.Note
import es.dani.nomore.notesapp.model.entities.User


@Database(entities = [User::class, Note::class], version = 3, exportSchema = false)
abstract class NotesDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao

    companion object {
        private const val NOTES_DATABASE = "notes_db"
        @Volatile private var instance: NotesDatabase? = null

        fun getInstance(context: Context): NotesDatabase {
            synchronized(this) {
                return instance
                    ?: Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java,
                        NOTES_DATABASE
                    )
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}
