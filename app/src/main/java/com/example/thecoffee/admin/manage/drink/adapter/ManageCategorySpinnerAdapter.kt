package com.example.thecoffee.admin.manage.drink.adapter

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.R
import com.example.thecoffee.databinding.CategorySpinnerBinding
import com.example.thecoffee.databinding.LayoutItemManageDrinkBinding
import com.example.thecoffee.order.model.Category
import java.util.Locale

interface ManageCategorySpinnerInterface {
    fun onCategorySelected(category: Category)
}

class ManageCategorySpinnerAdapter(
    val list: List<Category>,
    val onCategorySelected: ManageCategorySpinnerInterface,
): RecyclerView.Adapter<ManageCategorySpinnerAdapter.ManageCategorySpinnerViewHolder>(){
    private lateinit var binding: CategorySpinnerBinding
    private var filteredList: List<Category> = list
    inner class ManageCategorySpinnerViewHolder(val binding: CategorySpinnerBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.textView.text =  category.name
            Glide.with(itemView.context)
                .load(category.image)
                .into(binding.imageView)
        }

        init{
            binding.viewItem.setOnClickListener {
                onCategorySelected.onCategorySelected(list[adapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageCategorySpinnerViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = CategorySpinnerBinding.inflate(view, parent, false)
        return ManageCategorySpinnerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ManageCategorySpinnerViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    fun filter(query: String?) {
        filteredList = if (query.isNullOrEmpty()) {
            list
        } else {
            val lowercaseQuery = query.lowercase(Locale.getDefault())
            list.filter {
                // Thay "myAttribute" bằng thuộc tính cần lọc
                it.name!!.lowercase(Locale.getDefault()).contains(lowercaseQuery)
            }
        }
        notifyDataSetChanged()
    }


}