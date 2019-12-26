package es.dani.nomore.notesapp.model.entities

import androidx.room.TypeConverter


enum class UserRole(val userRoleId: Int) {
    ADMIN(0), TEAM_MEMBER(1)
}


class UserRoleConverter {
    @TypeConverter
    fun fromUserRole(userRoleId: Int): UserRole = UserRole.values()[userRoleId]

    @TypeConverter
    fun toUserRole(userRole: UserRole): Int = userRole.ordinal
}