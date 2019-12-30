package es.dani.nomore.notesapp.model.viewmodels

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
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
    val deleteUsername = MutableLiveData<String>()
    val usersList = userDao.getAllOtherUsers(userId ?: -1L)
    val userRoles = MutableLiveData<List<UserRole>>()

    init {
        initializeUser()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun checkLogin() {
        if (validateLogin()) {
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
        if (validateUserForm()) {
            Log.i("UserViewModel", "User form validated successfully")
            uiScope.launch {
                val savedUserId = if (isEditing())
                    updateUser(currentUser.value!!)
                else
                    addUser(currentUser.value!!)

                if (savedUserId >= 0)
                    newUserId.value = savedUserId
                else
                    validationError.value = getApplication<Application>().getString(R.string.user_exists)
            }
        }

    }

    fun deleteUser() {
        if (isEditing())
            uiScope.launch {
                removeUser(currentUser.value!!)
                deleteUsername.value = currentUser.value?.username
            }
    }

    private fun initializeUser() {
        uiScope.launch {
            currentUser.value = getUserById(userId) ?: getEmptyUser()
            Log.d("UserViewModel", "Initialized user: ${currentUser.value}")
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

    private suspend fun addUser(user: User): Long {
        return withContext(Dispatchers.IO) {
            var insertedId = -1L
            try {
                insertedId = userDao.insert(user)
            } catch (e: SQLiteConstraintException) {
                Log.e("UserViewModel", "User's unique constraint violated: ${user.email} repeated")
            }
            insertedId
        }
    }

    private suspend fun updateUser(user: User): Long {
        return withContext(Dispatchers.IO) {
            var updatedId = user.userId
            try {
                userDao.update(user)
            } catch (e: SQLiteConstraintException) {
                Log.e("UserViewModel", "User's unique constraint violated: ${user.email} repeated")
                updatedId = -1L
            }
            updatedId
        }
    }

    private suspend fun removeUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.delete(user)
        }
    }

    private fun isEditing(): Boolean = userId != null && userId >= 0

    private fun getEmptyUser() = User(username = "", email = "", password = "")

    private fun validateLogin() = isUserNotBlank() && isPasswordNotBlank()

    private fun validateUserForm() = usernameNotBlank() && isUserNotBlank() && isPasswordNotBlank() && isUserValid() && isPasswordValid()

    private fun usernameNotBlank(): Boolean {
        val username = currentUser.value?.username

        return if (username != null && username.trim().isNotBlank())
            true
        else
            false.also { validationError.value = getApplication<Application>().getString(R.string.username_not_blank) }
    }

    private fun isUserNotBlank(): Boolean {
        val email = currentUser.value?.email
        return if (email != null && email.trim().isNotBlank())
                true
            else {
                false.also { validationError.value = getApplication<Application>().getString(R.string.invalid_user) }
            }
    }

    private fun isUserValid(): Boolean {
        val email = currentUser.value?.email
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            true
        else
            false.also { validationError.value =  getApplication<Application>().getString(R.string.invalid_email)}
    }

    private fun isPasswordNotBlank(): Boolean {
        val password = currentUser.value?.password
        return if (password != null && password.trim().isNotBlank())
                true
            else
                false.also { validationError.value = getApplication<Application>().getString(R.string.password_not_blank) }
    }

    private fun isPasswordValid(): Boolean {
        val password = currentUser.value?.password
        return if (password != null && password.trim().length >= 8)
                true
            else
                false.also { validationError.value = getApplication<Application>().getString(R.string.invalid_password) }
    }

}