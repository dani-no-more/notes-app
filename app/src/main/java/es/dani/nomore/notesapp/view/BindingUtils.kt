package es.dani.nomore.notesapp.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import es.dani.nomore.notesapp.model.entities.Note
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("lastTimeModificationString")
fun TextView.lastTimeModificationFormatted(note: Note?) {
    note?.let {
        text = getLastModificationString(note.lastModificationTime)
    }
}


@SuppressLint("SimpleDateFormat")
fun getLastModificationString(time: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
    try {
        return sdf.format(Date(time))
    } catch (e: Exception) {
        Log.e("Note", "Couldn't get the last modification time string", e)
    }
    return ""
}
