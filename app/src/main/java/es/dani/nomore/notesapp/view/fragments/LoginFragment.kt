package es.dani.nomore.notesapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.LoginFragmentBinding


class LoginFragment : Fragment() {

    lateinit var binding: LoginFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        binding.loginButton.setOnClickListener { doLogin() }
        binding.registerButton.setOnClickListener { findNavController().navigate(R.id.action_login_fragment_to_createUserFragment) }


        return binding.root
    }

    private fun doLogin() {
        loginSuccess()
    }

    private fun loginSuccess() {
        findNavController().navigate(R.id.action_login_fragment_to_notes_fragment)
    }

    private fun showError(errorMsg: String) {
        binding.loginErrorText.text = errorMsg
        binding.loginErrorText.visibility = View.VISIBLE
    }

}
