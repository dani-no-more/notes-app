package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentNotesBinding
import es.dani.nomore.notesapp.model.database.NotesDatabase
import es.dani.nomore.notesapp.model.entities.Note
import es.dani.nomore.notesapp.model.entities.UserRole
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModel
import es.dani.nomore.notesapp.model.viewmodels.NoteViewModelFactory
import es.dani.nomore.notesapp.view.adapters.NoteItemViewHolder
import es.dani.nomore.notesapp.view.adapters.NotesAdapter


class NotesFragment : Fragment() {

    lateinit var userRole: UserRole

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentNotesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)

        val application = requireNotNull(this.activity).application
        val noteDao = NotesDatabase.getInstance(application).noteDao()
        val userId = NotesFragmentArgs.fromBundle(requireArguments()).userId
        val username = NotesFragmentArgs.fromBundle(requireArguments()).username
        userRole = UserRole.values()[NotesFragmentArgs.fromBundle(requireArguments()).userRole]

        val actionBar: ActionBar? = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.username_title_text, username)
        actionBar?.show()
        setHasOptionsMenu(true)

        val noteViewModelFactory = NoteViewModelFactory(noteDao, application, userId)
        val noteViewModel = ViewModelProviders.of(this, noteViewModelFactory).get(NoteViewModel::class.java)

        binding.lifecycleOwner = this
        binding.noteViewModel = noteViewModel

        val adapter = NotesAdapter(object: NoteItemViewHolder.NoteItemClickListener {
            override fun onItemClick(note: Note) {
                goToEditNote(note.noteId, userId, username)
            }
        })
        noteViewModel.noteList.observe(viewLifecycleOwner, Observer {
            Log.i("NotesFragment", "The list of notes has changed")
            adapter.submitList(it)
            binding.executePendingBindings()
        })
        binding.noteList.adapter = adapter

        binding.addNoteFab.setOnClickListener { goToNewNote(userId, username) }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        menu.findItem(R.id.menu_item_users_management).isVisible = userRole == UserRole.ADMIN
        menu.findItem(R.id.menu_item_notes_management).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.overflow_profile_action -> goToEditProfile()
            R.id.overflow_logout_action -> performLogout()
            R.id.menu_item_users_management -> goToUserManagement()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToNewNote(userId: Long, username: String) {
        val action = NotesFragmentDirections.actionNotesFragmentToCreateEditNoteFragment(userId = userId, username = username)
        findNavController().navigate(action)
    }

    private fun goToEditNote(noteId: Long, userId: Long, username: String) {
        val action = NotesFragmentDirections.actionNotesFragmentToCreateEditNoteFragment(noteId, userId, username)
        findNavController().navigate(action)
    }

    private fun goToEditProfile() {
        val userId = NotesFragmentArgs.fromBundle(requireArguments()).userId
        val action = NotesFragmentDirections.actionNotesFragmentToCreateUserFragment(false, userId)
        findNavController().navigate(action)
    }

    private fun performLogout() {
        findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToLoginFragment())
    }

    private fun goToUserManagement() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
