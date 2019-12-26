package es.dani.nomore.notesapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import es.dani.nomore.notesapp.model.dao.NoteDao
import es.dani.nomore.notesapp.model.dao.UserDao
import es.dani.nomore.notesapp.model.entities.Note
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.entities.UserRole
import es.dani.nomore.notesapp.model.entities.UserRoleConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Database(entities = [User::class, Note::class], version = 6, exportSchema = false)
@TypeConverters(UserRoleConverter::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao

    companion object {
        private const val NOTES_DATABASE = "notes_db"
        @Volatile
        private var instance: NotesDatabase? = null

        fun getInstance(context: Context): NotesDatabase {
            synchronized(this) {

                val roomDbCallBack = object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.Main).launch {
                            getInstance(context).userDao().insert(getRootAdminUser())
                        }
                    }
                }

                return instance ?: Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, NOTES_DATABASE)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomDbCallBack)
                    .build().also { instance = it }
            }
        }

        // The app will have a single initial admin user, which credentials are encourage to be changed first time after login
        private fun getRootAdminUser() = User(
            username = "admin",
            email = "admin",
            password = "admin",
            userRole = UserRole.ADMIN.userRoleId
        )
    }
}
