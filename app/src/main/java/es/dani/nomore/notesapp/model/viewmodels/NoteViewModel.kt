package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import es.dani.nomore.notesapp.model.dao.NoteDao
import es.dani.nomore.notesapp.model.entities.Note
import kotlinx.coroutines.*

class NoteViewModel(private val noteDao: NoteDao, application: Application, private val userId: Long, private val noteId: Long? = null): AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val currentNote = MutableLiveData<Note>()
    val noteList = noteDao.getNotesByUser(userId)

    init {
        initializeNote()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun initializeNote() {
        uiScope.launch {
            currentNote.value = getNoteById(noteId) ?: getEmptyNote()
        }
    }

    private suspend fun getNoteById(noteId: Long?): Note? {
        return withContext(Dispatchers.IO) {
            noteDao.get(noteId ?: -1)
        }
    }

    private suspend fun upsertNote(note: Note): Long {
        return withContext(Dispatchers.IO) {
            if (noteId != null) {
                noteDao.update(note)
                noteId
            } else {
                noteDao.insert(note)
            }
        }
    }

    private fun getEmptyNote(): Note {
        return Note(title = "", content = "", owner = -1L)
    }
}