package es.dani.nomore.notesapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.model.entities.Note

class NotesAdapter: ListAdapter<Note, NoteItemViewHolder>(NoteDiffCallback()) {

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder =
        NoteItemViewHolder.from(parent)

}


class NoteItemViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val noteTitleTextView: TextView = itemView.findViewById(R.id.note_title_text)
    private val noteLastModificationTextView: TextView = itemView.findViewById(R.id.note_last_modification_text)

    fun bind(note: Note) {
        noteTitleTextView.text = note.title
        noteLastModificationTextView.text = note.lastModificationTime.toString()
    }

    companion object {
        fun from(parent: ViewGroup) = NoteItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_list_item_view, parent, false))
    }
}


class NoteDiffCallback: DiffUtil.ItemCallback<Note>() {

    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.noteId == newItem.noteId

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem

}