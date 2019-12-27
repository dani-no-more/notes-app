package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.model.dao.UserDao
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.entities.UserRole
import kotlinx.coroutines.*


class UserViewModel(private val userDao: UserDao, application: Application, private val userId: Long? = null): AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val currentUser = MutableLiveData<User>()
    val loginUser = MutableLiveData<User>()
    val validationError = MutableLiveData<String>()
    val newUserId = MutableLiveData<Long>()
    val usersList = userDao.getAllUsers()
    val userRoles = MutableLiveData<List<UserRole>>()

    init {
        initializeUser()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun checkLogin() {
        if (validateUser() && validatePassword()) {
            Log.i("UserViewModel", "Valid user and password")
            uiScope.launch {
                val user = getUserByEmail(currentUser.value!!.email)
                if (user != null) {
                    Log.i("UserViewModel", "User exists")
                    if (user.password == currentUser.value?.password) {
                        Log.i("UserViewModel", "Password correct")
                        loginUser.value = user
                    } else {
                        validationError.value = getApplication<Application>().getString(R.string.credential_fail)
                    }
                } else {
                    validationError.value = getApplication<Application>().getString(R.string.user_not_found)
                }
            }
        }

    }

    fun createUser() {
        if (validateUsername() && validateUser() && validatePassword())
            uiScope.launch {
                newUserId.value = upsertUser(currentUser.value!!)
            }
    }

    private fun initializeUser() {
        uiScope.launch {
            currentUser.value = getUserById(userId) ?: getEmptyUser()
            userRoles.value = UserRole.values().asList()
        }
    }

    private suspend fun getUserById(id: Long?): User? {
        return withContext(Dispatchers.IO) {
            userDao.get(id ?: -1)
        }
    }

    private suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUser(email)
        }
    }

    private suspend fun upsertUser(user: User): Long {
        return withContext(Dispatchers.IO) {
            if (userId != null && userId >= 0) { // Update
                userDao.update(user)
                userId
            }
            else
                userDao.insert(user)
        }
    }

    private suspend fun addUser(user: User): Long {
        return withContext(Dispatchers.IO) {
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
            if (password.length >= 8)
                true
            else
                false.also { validationError.value = getApplication<Application>().getString(R.string.invalid_password) }
        else
            false.also { validationError.value = getApplication<Application>().getString(R.string.password_not_blank) }
    }
}