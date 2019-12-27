package es.dani.nomore.notesapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.dani.nomore.notesapp.databinding.UserListItemViewBinding
import es.dani.nomore.notesapp.model.entities.User
import es.dani.nomore.notesapp.model.entities.UserRole


class UsersAdapter(private val listener: UserItemViewHolder.UserListItemClickListener): ListAdapter<User, UserItemViewHolder>(UserDiffCallback()) {
    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) = holder.bind(getItem(position), listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserItemViewHolder.from(parent)
}

class UserItemViewHolder private constructor(val binding: UserListItemViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, listener: UserListItemClickListener) {
        binding.user = user

        binding.userListItemAdminIcon.visibility = when (user.userRole) {
            UserRole.ADMIN -> View.VISIBLE
            else -> View.GONE
        }

        binding.userListItemName.setOnClickListener {
            listener.onItemClick(user)
        }

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): UserItemViewHolder {
            val binding = UserListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return UserItemViewHolder(binding)
        }
    }

    interface UserListItemClickListener {
        fun onItemClick(user: User)
    }
}


class UserDiffCallback: DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.userId == newItem.userId

    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
}