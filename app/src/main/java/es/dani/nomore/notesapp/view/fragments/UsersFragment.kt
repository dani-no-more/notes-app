package es.dani.nomore.notesapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import es.dani.nomore.notesapp.R
import es.dani.nomore.notesapp.databinding.FragmentUsersBinding
import es.dani.nomore.notesapp.model.database.NotesDatabase
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.entities.UserRole
import es.dani.nomore.notesapp.model.viewmodels.UserViewModel
import es.dani.nomore.notesapp.model.viewmodels.UserViewModelFactory
import es.dani.nomore.notesapp.view.adapters.UserItemViewHolder
import es.dani.nomore.notesapp.view.adapters.UsersAdapter


class UsersFragment : Fragment() {

    lateinit var binding: FragmentUsersBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)

        val application = requireNotNull(this.activity).application
        val adminUserId = UsersFragmentArgs.fromBundle(requireArguments()).adminUserId
        val userDao = NotesDatabase.getInstance(application).userDao()

        val actionBar: ActionBar? = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = application.getString(R.string.user_management_title)
        actionBar?.show()
        setHasOptionsMenu(true)

        val userViewModelFactory = UserViewModelFactory(userDao, application, adminUserId)
        val userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.lifecycleOwner = this
        binding.userViewModel = userViewModel

        val adapter = UsersAdapter(object: UserItemViewHolder.UserListItemClickListener {
            override fun onItemClick(user: User) {
                goToEditProfile(user.userId, adminUserId)
            }
        })
        userViewModel.usersList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            binding.executePendingBindings()
        })
        binding.usersList.adapter = adapter

        binding.addUserFab.setOnClickListener {
            goToEditProfile(adminUserId = adminUserId)
        }

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        menu.findItem(R.id.menu_item_users_management).isVisible = false
        menu.findItem(R.id.menu_item_notes_management).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val adminUserId = UsersFragmentArgs.fromBundle(requireArguments()).adminUserId
        when(item.itemId) {
            R.id.overflow_profile_action -> goToEditProfile(adminUserId)
            R.id.overflow_logout_action -> performLogout()
            R.id.menu_item_notes_management -> goToNotesManagement()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToNotesManagement() {
        val currentUser = binding.userViewModel?.currentUser?.value
        if (currentUser != null) {
            val action = UsersFragmentDirections.actionUsersFragmentToNotesFragment(currentUser.userId, currentUser.username, currentUser.userRole.userRoleId)
            findNavController().navigate(action)
        }
    }

    private fun performLogout() {
        val sharedPreferences = activity?.getSharedPreferences(getString(R.string.app_preferences_key), Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.let {
            it.putLong(getString(R.string.logged_user_id), -1L)
            it.putString(getString(R.string.logged_user_name), null)
            it.putInt(getString(R.string.logged_user_role), UserRole.TEAM_MEMBER.userRoleId)
            it.commit()
        }
        findNavController().navigate(UsersFragmentDirections.actionUsersFragmentToLoginFragment())
    }

    private fun goToEditProfile(userId: Long = -1L, adminUserId: Long = -1L) {
        val action = UsersFragmentDirections.actionUsersFragmentToCreateUserFragment(userId = userId, adminUserId = adminUserId)
        findNavController().navigate(action)
    }

}
