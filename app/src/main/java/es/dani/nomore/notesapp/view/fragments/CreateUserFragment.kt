package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentCreateUserBinding
import es.dani.nomore.notesapp.model.database.NotesDatabase
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.entities.UserRole
import es.dani.nomore.notesapp.model.viewmodels.UserViewModel
import es.dani.nomore.notesapp.model.viewmodels.UserViewModelFactory
import es.dani.nomore.notesapp.view.adapters.UserRoleSpinnerAdapter


class CreateUserFragment : Fragment() {

    lateinit var binding: FragmentCreateUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_user, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()

        val application = requireNotNull(this.activity).application
        val userDao = NotesDatabase.getInstance(application).userDao()

        val isRegistering = CreateUserFragmentArgs.fromBundle(requireArguments()).isRegistration
        val userId = CreateUserFragmentArgs.fromBundle(requireArguments()).userId

        val userViewModelFactory = UserViewModelFactory(userDao, application, userId)
        val userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.userViewModel = userViewModel

        binding.lifecycleOwner = this

        userViewModel.validationError.observe(this, Observer {
            if (it != null && !it.trim().isEmpty())
                showToastValidationError(it)
        })

        userViewModel.newUserId.observe(this, Observer {
            if (isRegistering) {
                goBackToLogin(it)
            } else {
                goBackToNotes(userViewModel.currentUser.value!!)
            }
        })

        binding.userRoleSpinner.adapter = UserRoleSpinnerAdapter(application.applicationContext)

        return binding.root
    }

    private fun goBackToLogin(newUserId: Long) {
        showToastValidationError("User created!")
        val action = CreateUserFragmentDirections.actionCreateUserFragmentToLoginFragment(newUserId)
        findNavController().navigate(action)
    }

    private fun goBackToNotes(user: User) {
        showToastValidationError("User edited!")
        val action = CreateUserFragmentDirections.actionCreateUserFragmentToNotesFragment(user.userId, user.username, user.userRole)
        findNavController().navigate(action)
    }

    private fun showToastValidationError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
