package com.example.thecoffee.admin.manage.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.admin.manage.drink.adapter.ManageCategorySpinnerAdapter
import com.example.thecoffee.admin.manage.drink.adapter.ManageCategorySpinnerInterface
import com.example.thecoffee.databinding.CategorySpinnerBinding
import com.example.thecoffee.databinding.LayoutItemManageUserBinding
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.other.user.model.User
import java.util.Locale

class ManageItemUserAdapter(
    val list: List<User>
): RecyclerView.Adapter<ManageItemUserAdapter.ManageItemUserViewHolder>(){
    private lateinit var binding: LayoutItemManageUserBinding
    inner class ManageItemUserViewHolder(val binding: LayoutItemManageUserBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            Glide.with(itemView.context).load(user.avt).into(binding.imgAvt)
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageItemUserViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemManageUserBinding.inflate(view, parent, false)
        return ManageItemUserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ManageItemUserViewHolder, position: Int) {
        holder.bind(list[position])
    }
}