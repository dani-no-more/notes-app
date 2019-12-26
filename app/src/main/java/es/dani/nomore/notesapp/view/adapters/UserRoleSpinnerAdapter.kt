package es.dani.nomore.notesapp.view.adapters

import android.content.Context
import android.widget.ArrayAdapter
import es.dani.nomore.notesapp.model.entities.UserRole

class UserRoleSpinnerAdapter(context: Context): ArrayAdapter<UserRole>(context, android.R.layout.simple_spinner_item) {
    init {
        this.addAll(UserRole.TEAM_MEMBER, UserRole.ADMIN)
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }
}