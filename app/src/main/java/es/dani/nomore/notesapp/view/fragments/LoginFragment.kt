package es.dani.nomore.notesapp.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.LoginFragmentBinding
import es.dani.nomore.notesapp.model.database.NotesDatabase
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.entities.UserRole
import es.dani.nomore.notesapp.model.viewmodels.UserViewModel
import es.dani.nomore.notesapp.model.viewmodels.UserViewModelFactory


class LoginFragment : Fragment() {

    lateinit var binding: LoginFragmentBinding
    var doLogin = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()

        val sharedPreferences = activity?.getSharedPreferences(getString(R.string.app_preferences_key), Context.MODE_PRIVATE)
        sharedPreferences?.let {
            val loggedUserId = it.getLong(getString(R.string.logged_user_id), -1L)
            val loggedUsername = it.getString(getString(R.string.logged_user_name), "User")
            val loggedUserRoleId = it.getInt(getString(R.string.logged_user_role), UserRole.TEAM_MEMBER.userRoleId)

            if (loggedUserId >= 0 && loggedUsername != null) {
                loginSuccess(User(userId = loggedUserId, username = loggedUsername, userRole = UserRole.values()[loggedUserRoleId], email = "", password = ""))
            }
        }

        val application = requireNotNull(this.activity).application
        val userDao = NotesDatabase.getInstance(application).userDao()
        val userId = LoginFragmentArgs.fromBundle(requireArguments()).userId
        val userViewModelFactory = UserViewModelFactory(userDao, application, userId)
        val userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this

        userViewModel.validationError.observe(this, Observer { showSnackbarMessage(it) })
        userViewModel.loginUser.observe(this, Observer { loginSuccess(it) })
        binding.registerButton.setOnClickListener { goToUserRegister() }

        return binding.root
    }

    private fun loginSuccess(loginUser: User?) {
        hideKeyboard()
        if (loginUser != null && !doLogin) {
            Log.i("LoginFragment", "Login success")

            // For some reason this is being called twice after using the back button from the register user Fragment
            doLogin = true

            // Saved logged user Id
            val sharedPreferences = activity?.getSharedPreferences(getString(R.string.app_preferences_key), Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.let {
                it.putLong(getString(R.string.logged_user_id), loginUser.userId)
                it.putString(getString(R.string.logged_user_name), loginUser.username)
                it.putInt(getString(R.string.logged_user_role), loginUser.userRole.userRoleId)
                it.commit()
            }

            val action = LoginFragmentDirections.actionLoginFragmentToNotesFragment(loginUser.userId, loginUser.username, loginUser.userRole.userRoleId)
            findNavController().navigate(action)
        }
    }

    private fun goToUserRegister() {
        val action = LoginFragmentDirections.actionLoginFragmentToCreateUserFragment(true)
        findNavController().navigate(action)
    }

    private fun showToastValidationError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private fun showSnackbarMessage(msg: String) {
        Snackbar.make(activity!!.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val view = (activity as AppCompatActivity).currentFocus
        view?.let {
            val imm: InputMethodManager? =
                context?.let { it1 -> getSystemService(it1, InputMethodManager::class.java) }
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
