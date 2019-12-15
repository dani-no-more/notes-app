package es.dani.nomore.notesapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.dani.nomore.notesapp.databinding.NoteListItemViewBinding
import es.dani.nomore.notesapp.model.entities.Note

class NotesAdapter: ListAdapter<Note, NoteItemViewHolder>(NoteDiffCallback()) {

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder =
        NoteItemViewHolder.from(parent)

}


class NoteItemViewHolder private constructor(val binding: NoteListItemViewBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note) {
        binding.note = note
        binding.executePendingBindings()
        //binding.itemNoteTitleText.text = note.title
        //binding.itemNoteLastModificationText.text = note.lastModificationTime.toString()
    }

    companion object {
        fun from(parent: ViewGroup): NoteItemViewHolder {
            val binding =
                NoteListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoteItemViewHolder(binding)
        }
    }
}


class NoteDiffCallback: DiffUtil.ItemCallback<Note>() {

    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.noteId == newItem.noteId

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem

}