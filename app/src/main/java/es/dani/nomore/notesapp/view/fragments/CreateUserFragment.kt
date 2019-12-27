package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import es.dani.nomore.notesapp.model.viewmodels.UserViewModel
import es.dani.nomore.notesapp.model.viewmodels.UserViewModelFactory
import es.dani.nomore.notesapp.view.adapters.UserRoleSpinnerAdapter


class CreateUserFragment : Fragment() {

    lateinit var binding: FragmentCreateUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_user, container, false)

        //(activity as AppCompatActivity).supportActionBar?.hide()
        setHasOptionsMenu(false)

        val application = requireNotNull(this.activity).application
        val userDao = NotesDatabase.getInstance(application).userDao()

        val isRegistering = CreateUserFragmentArgs.fromBundle(requireArguments()).isRegistration
        val userId = CreateUserFragmentArgs.fromBundle(requireArguments()).userId
        val adminUserId = CreateUserFragmentArgs.fromBundle(requireArguments()).adminUserId

        changeActionBarTitle(when {
            isRegistering -> application.getString(R.string.register_user_title_text)
            userId == -1L -> application.getString(R.string.new_user_title_text)
            else -> application.getString(R.string.edit_user_title_text)
        })

        val userViewModelFactory = UserViewModelFactory(userDao, application, userId)
        val userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this

        userViewModel.validationError.observe(this, Observer {
            if (it != null && it.trim().isNotEmpty())
                showToastMessage(it)
        })

        userViewModel.newUserId.observe(this, Observer {
            when {
                isRegistering -> goBackToLogin(it)
                adminUserId >= 0 -> goBackToUsers(adminUserId)
                else -> goBackToNotes(userViewModel.currentUser.value!!)
            }
        })

        //binding.userRoleSpinner.adapter = UserRoleSpinnerAdapter(application.applicationContext)
        binding.userRoleSpinner.visibility = if (adminUserId >= 0) View.VISIBLE else View.GONE

        return binding.root
    }

    private fun goBackToLogin(newUserId: Long) {
        showToastMessage("User created!")
        val action = CreateUserFragmentDirections.actionCreateUserFragmentToLoginFragment(newUserId)
        findNavController().navigate(action)
    }

    private fun goBackToNotes(user: User) {
        showToastMessage("User edited!")
        val action = CreateUserFragmentDirections.actionCreateUserFragmentToNotesFragment(user.userId, user.username, user.userRole.userRoleId)
        findNavController().navigate(action)
    }

    private fun goBackToUsers(adminUserId: Long) {
        showToastMessage("User saved!")
        val action = CreateUserFragmentDirections.actionCreateUserFragmentToUsersFragment(adminUserId)
        findNavController().navigate(action)
    }

    private fun showToastMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private fun changeActionBarTitle(newTitle: String) {
        (activity as AppCompatActivity).supportActionBar?.title = newTitle
    }
}
