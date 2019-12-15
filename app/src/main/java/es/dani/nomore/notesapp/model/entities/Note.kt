package es.dani.nomore.notesapp.model.entities

import android.annotation.SuppressLint
import android.util.Log
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*


@Entity(tableName = "note_table",
    foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["owner"], onDelete = CASCADE)],
    indices = [Index(value = ["title", "owner"], unique = true), Index(value = ["owner"])]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "content")
    var content: String,

    @ColumnInfo(name = "last_modification_time")
    var lastModificationTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "owner")
    var owner: Long
)
