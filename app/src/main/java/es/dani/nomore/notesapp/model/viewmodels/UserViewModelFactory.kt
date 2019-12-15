package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.dani.nomore.notesapp.model.dao.UserDao
import java.lang.IllegalArgumentException

class UserViewModelFactory(private val userDao: UserDao, private val application: Application, private val userId: Long? = null): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userDao, application, userId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}