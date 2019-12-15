package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentNotesBinding


class NotesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentNotesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)

        val userId = NotesFragmentArgs.fromBundle(requireArguments()).userId
        val username = NotesFragmentArgs.fromBundle(requireArguments()).username

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.username_title_text, username)
        return binding.root
    }

}
