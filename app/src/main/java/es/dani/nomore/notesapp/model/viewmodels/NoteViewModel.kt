package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
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
    val deleteNoteTitle = MutableLiveData<String>()

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
                    val newNote = currentValue.copy(lastModificationTime = System.currentTimeMillis())
                    val insertedNoteId = when(isEdition()) {
                        true -> {
                            Log.i("NoteViewModel", "Editing note")
                            updateNote(newNote)
                        }
                        false -> {
                            Log.i("NoteViewModel", "Creating note")
                            insertNote(newNote)
                        }
                    }

                    if (insertedNoteId >= 0)
                        newSavedNoteId.value = insertedNoteId
                    else
                        validationError.value = getApplication<Application>().getString(R.string.note_already_exists)
                }
            }
        }
    }

    fun deleteNote() {
        if (isEdition()) {
            uiScope.launch {
                removeNote(currentNote.value!!)
                deleteNoteTitle.value = currentNote.value?.title
            }
        }
    }

    fun isEdition() = noteId != null && noteId >= 0

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

    private suspend fun insertNote(note: Note): Long {
        Log.i("NoteViewModel", "Insert note: $note")
        return withContext(Dispatchers.IO) {
            var insertedNoteId = -1L
            try {
                insertedNoteId = noteDao.insert(note)
            } catch (e: SQLiteConstraintException) {
                Log.e("NoteViewModel", "Unique constraint violation: User already has a note with that title")
            }
            insertedNoteId
        }
    }

    private suspend fun updateNote(note: Note): Long {
        Log.i("NoteViewModel", "Update note: $note")
        return withContext(Dispatchers.IO) {
            var updatedNoteId = note.noteId
            try {
                noteDao.update(note)
            } catch (e: SQLiteConstraintException) {
                Log.e("NoteViewModel", "Unique constraint violation: User already has a note with that title")
                updatedNoteId = -1L
            }
            updatedNoteId
        }
    }

    private suspend fun removeNote(note: Note) {
        Log.i("NoteViewModel", "Deleting note: ${note.title}")
        withContext(Dispatchers.IO) {
            noteDao.delete(note)
        }
    }

    private fun getEmptyNote(): Note {
        return Note(title = "", content = "", owner = userId)
    }

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