package es.dani.nomore.notesapp.view.fragments

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentCreateUserBinding
import es.dani.nomore.notesapp.model.NotesDatabase
import es.dani.nomore.notesapp.model.dao.UserDao
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.viewmodels.UserViewModel
import es.dani.nomore.notesapp.model.viewmodels.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CreateUserFragment : Fragment() {

    private lateinit var application: Application
    lateinit var binding: FragmentCreateUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_user, container, false)

        application = requireNotNull(this.activity).application
        val userDao = NotesDatabase.getInstance(application).UserDao()

        val userViewModelFactory = UserViewModelFactory(userDao, application, 1L)
        val userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.userViewModel = userViewModel

        binding.lifecycleOwner = this

        userViewModel.validationError.observe(this, Observer {
            if (it != null && !it.trim().isEmpty())
                showToastValidationError(it)
        })

        return binding.root
    }

    private fun showToastValidationError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
