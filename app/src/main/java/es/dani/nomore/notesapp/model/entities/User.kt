package es.dani.nomore.notesapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "user_table", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0L,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "password")
    var password: String,

    @ColumnInfo(name = "user_role")
    var userRole: Int = UserRole.TEAM_MEMBER.userRoleId
)
