package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentCreateEditNoteBinding
import es.dani.nomore.notesapp.model.database.NotesDatabase
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModel
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModelFactory


class CreateEditNoteFragment : Fragment() {

    lateinit var binding: FragmentCreateEditNoteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_edit_note, container, false)

        val noteId = CreateEditNoteFragmentArgs.fromBundle(requireArguments()).noteId
        val userId = CreateEditNoteFragmentArgs.fromBundle(requireArguments()).userId
        val application = requireNotNull(this.activity).application
        val noteDao = NotesDatabase.getInstance(application).noteDao()
        val noteViewModelFactory = NoteViewModelFactory(noteDao, application, userId, noteId)
        val noteViewModel = ViewModelProviders.of(this, noteViewModelFactory).get(NoteViewModel::class.java)

        binding.noteViewModel = noteViewModel
        binding.lifecycleOwner = this

        noteViewModel.validationError.observe(this, Observer { showToastValidationError(it) })
        noteViewModel.newSavedNoteId.observe(this, Observer {
            Log.i("CreateEditNoteFragment", "New created note with id %s".format(it))
            fragmentManager?.popBackStack()
        })

        return binding.root
    }

    private fun showToastValidationError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}
