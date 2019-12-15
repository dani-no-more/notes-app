package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentCreateEditNoteBinding
import es.dani.nomore.notesapp.model.database.NotesDatabase
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModel
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModelFactory


class CreateEditNoteFragment : Fragment() {

    lateinit var binding: FragmentCreateEditNoteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_edit_note, container, false)

        val args = CreateEditNoteFragmentArgs.fromBundle(requireArguments())
        val noteId = args.noteId
        val userId = args.userId
        val username = args.username
        val application = requireNotNull(this.activity).application
        val noteDao = NotesDatabase.getInstance(application).noteDao()
        val noteViewModelFactory = NoteViewModelFactory(noteDao, application, userId, noteId)
        val noteViewModel = ViewModelProviders.of(this, noteViewModelFactory).get(NoteViewModel::class.java)

        when(noteId) {
            -1L -> changeActionBarTitle(getString(R.string.new_note_title_text))
            else -> changeActionBarTitle(getString(R.string.edit_note_title_text))
        }

        binding.noteViewModel = noteViewModel
        binding.lifecycleOwner = this

        noteViewModel.validationError.observe(this, Observer { showToastValidationError(it) })
        noteViewModel.newSavedNoteId.observe(this, Observer {
            goToNoteList(userId, username)
            //fragmentManager?.popBackStack()
        })

        return binding.root
    }


    private fun goToNoteList(userId: Long, username: String) {
        val action = CreateEditNoteFragmentDirections.actionCreateEditNoteFragmentToNotesFragment(userId, username)
        findNavController().navigate(action)
    }

    private fun changeActionBarTitle(newTitle: String) {
        (activity as AppCompatActivity).supportActionBar?.title = newTitle
    }

    private fun showToastValidationError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}
