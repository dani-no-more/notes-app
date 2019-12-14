package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.model.dao.UserDao
import es.dani.nomore.notesapp.model.entities.User
import kotlinx.coroutines.*


class UserViewModel(private val userDao: UserDao, application: Application, var userId: Long? = null): AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val currentUser = MutableLiveData<User>()
    val validationError = MutableLiveData<String>()

    init {
        initializeUser()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun createUser() {
        if (validateUsername() && validateUser() && validatePassword())
            uiScope.launch {
                addUser(currentUser.value!!)
            }
    }

    private fun initializeUser() {
        uiScope.launch {
            if (userId != null) {
                currentUser.value = getUserById(userId)
            }
            if (currentUser.value == null)
                currentUser.value = getEmptyUser()
        }
    }

    private suspend fun getUserById(id: Long?): User? {
        return withContext(Dispatchers.IO) {
            userDao.get(id ?: -1)
        }
    }

    private suspend fun addUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insert(user)
        }
    }

    private suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.update(user)
        }
    }

    private suspend fun removeUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.delete(user)
        }
    }

    private fun getEmptyUser() = User(username = "", email = "", password = "")

    private fun validateUsername(): Boolean {
        val username = currentUser.value?.username

        return if (username != null && username.trim().isNotBlank())
            true
        else
            false.also { validationError.value = getApplication<Application>().getString(R.string.username_not_blank) }
    }

    private fun validateUser(): Boolean {
        val email = currentUser.value?.email
        return if (email != null && email.trim().isNotBlank())
            true
        else
            false.also { validationError.value = getApplication<Application>().getString(R.string.invalid_user) }
    }

    private fun validatePassword(): Boolean {
        val password = currentUser.value?.password
        return if (password != null && password.trim().isNotBlank())
            true
        else
            false.also { validationError.value = getApplication<Application>().getString(R.string.invalid_password) }
    }
}