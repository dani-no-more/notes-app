package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.model.dao.NoteDao
import es.dani.nomore.notesapp.model.entities.Note
import kotlinx.coroutines.*

class NoteViewModel(private val noteDao: NoteDao, application: Application, private val userId: Long, private val noteId: Long? = null): AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val validationError = MutableLiveData<String>()
    val currentNote = MutableLiveData<Note>()
    val noteList = noteDao.getNotesByUser(userId)
    val newSavedNoteId = MutableLiveData<Long>()

    init {
        initializeNote()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun saveNote() {
        if (validateTitle() && validateContent()) {
            Log.i("NoteViewModel", "Note validated")
            uiScope.launch {
                val currentValue = currentNote.value
                if (currentValue != null) {
                    val newNote = Note(title = currentValue.title, content = currentValue.content, owner = userId, lastModificationTime = System.currentTimeMillis())
                    Log.i("NoteViewModel", "Note ready to be saved")
                    newSavedNoteId.value = upsertNote(newNote)
                    Log.i("NoteViewModel", "Note saved")
                }
            }
        }
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
        Log.i("NoteViewModel", "Upsert note: %s".format(note.toString()))
        return withContext(Dispatchers.IO) {
            when(isEdition()) {
                true -> {
                    Log.i("NoteViewModel", "Editing note")
                    noteDao.update(note.copy(noteId = noteId!!))
                    noteId!!
                }
                false -> {
                    Log.i("NoteViewModel", "Creating note")
                    noteDao.insert(note)
                }
            }
        }
    }

    private fun getEmptyNote(): Note {
        return Note(title = "", content = "", owner = -1L)
    }

    private fun isEdition() = noteId != null && noteId >= 0

    private fun validateTitle(): Boolean {
        val title = currentNote.value?.title
        return if (title != null && title.trim().isNotBlank())
            true
        else
            false.also { validationError.value = getApplication<Application>().getString(R.string.note_title_not_blank) }
    }

    private fun validateContent(): Boolean {
        val content = currentNote.value?.content
        return if (content != null && content.trim().isNotBlank())
            true
        else
            false.also { validationError.value = getApplication<Application>().getString(R.string.note_content_not_blank) }
    }
}