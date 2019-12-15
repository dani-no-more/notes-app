package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.LoginFragmentBinding
import es.dani.nomore.notesapp.model.NotesDatabase
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.viewmodels.UserViewModel
import es.dani.nomore.notesapp.model.viewmodels.UserViewModelFactory


class LoginFragment : Fragment() {

    lateinit var binding: LoginFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val userDao = NotesDatabase.getInstance(application).userDao()

        val userId = LoginFragmentArgs.fromBundle(requireArguments()).userId
        val userViewModelFactory = UserViewModelFactory(userDao, application, userId)
        val userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this

        userViewModel.validationError.observe(this, Observer { showToastValidationError(it) })
        userViewModel.loginUser.observe(this, Observer { loginSuccess(it) })
        binding.registerButton.setOnClickListener { findNavController().navigate(R.id.action_login_fragment_to_createUserFragment) }

        return binding.root
    }

    private fun loginSuccess(loginUser: User?) {
        if (loginUser != null) {
            Log.i("LoginFragment", "Login success")
            val action = LoginFragmentDirections.actionLoginFragmentToNotesFragment(loginUser.userId, loginUser.username)
            findNavController().navigate(action)
        }
    }

    private fun showToastValidationError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}
