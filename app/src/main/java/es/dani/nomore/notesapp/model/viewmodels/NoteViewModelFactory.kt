package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.dani.nomore.notesapp.model.dao.NoteDao
import java.lang.IllegalArgumentException

class NoteViewModelFactory(private val noteDao: NoteDao, private val application: Application,
                           private val userId: Long, private val noteId: Long? = null): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(noteDao, application, userId, noteId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}