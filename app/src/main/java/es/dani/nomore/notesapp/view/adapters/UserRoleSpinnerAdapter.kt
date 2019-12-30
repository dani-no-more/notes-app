package es.dani.nomore.notesapp.view.adapters

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import es.dani.nomore.notesapp.model.entities.UserRole


@BindingAdapter(value = ["userRoles", "selectedRole", "selectedRoleAttrChanged"], requireAll = false)
fun setUserRoles(spinner: Spinner, userRoles: List<UserRole>?, selectedRole: UserRole?, listener: InverseBindingListener) {
    if (userRoles != null) {
        spinner.adapter = ArrayAdapter<UserRole>(spinner.context, android.R.layout.simple_spinner_item, userRoles)
        setCurrentSelection(spinner, selectedRole)
        setSpinnerListener(spinner, listener)
    }
}

@InverseBindingAdapter(attribute = "selectedRole")
fun getSelectedUserRole(spinner: Spinner): UserRole {
    return spinner.selectedItem as UserRole
}


private fun setSpinnerListener(spinner: Spinner, listener: InverseBindingListener) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = listener.onChange()
        override fun onNothingSelected(adapterView: AdapterView<*>) = listener.onChange()
    }
}

private fun setCurrentSelection(spinner: Spinner, selectedRole: UserRole?): Boolean {
    if (selectedRole != null) {
        for (index in 0 until spinner.count) {
            if (spinner.getItemAtPosition(index) as UserRole == selectedRole) {
                spinner.setSelection(index)
                return true
            }
        }
    }
    return false
}
