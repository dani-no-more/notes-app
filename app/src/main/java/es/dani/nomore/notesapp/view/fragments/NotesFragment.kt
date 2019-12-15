package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentNotesBinding
import es.dani.nomore.notesapp.model.database.NotesDatabase
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModel
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModelFactory
import es.dani.nomore.notesapp.view.adapters.NotesAdapter


class NotesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentNotesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)

        val application = requireNotNull(this.activity).application
        val noteDao = NotesDatabase.getInstance(application).noteDao()
        val userId = NotesFragmentArgs.fromBundle(requireArguments()).userId
        val username = NotesFragmentArgs.fromBundle(requireArguments()).username

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.username_title_text, username)

        val noteViewModelFactory = NoteViewModelFactory(noteDao, application, userId)
        val noteViewModel = ViewModelProviders.of(this, noteViewModelFactory).get(NoteViewModel::class.java)

        binding.lifecycleOwner = this
        binding.noteViewModel = noteViewModel

        val adapter = NotesAdapter()
        noteViewModel.noteList.observe(viewLifecycleOwner, Observer {
            Log.i("NotesFragment", "The list of notes has changed")
            adapter.submitList(it)
            binding.executePendingBindings()
        })
        binding.noteList.adapter = adapter

        binding.addNoteFab.setOnClickListener { goToNewNote(userId) }
        return binding.root
    }

    private fun goToNewNote(userId: Long) {
        val action = NotesFragmentDirections.actionNotesFragmentToCreateEditNoteFragment(userId = userId)
        findNavController().navigate(action)
    }

}
