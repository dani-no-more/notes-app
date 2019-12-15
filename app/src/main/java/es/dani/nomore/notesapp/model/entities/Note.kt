package es.dani.nomore.notesapp.model.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity(tableName = "note_table",
    foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["owner"], onDelete = CASCADE)],
    indices = [Index(value = ["title"], unique = true)]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "content")
    var content: String,

    @ColumnInfo(name = "creation_time")
    var creationTime: Long,

    @ColumnInfo(name = "owner")
    var owner: Long


)
