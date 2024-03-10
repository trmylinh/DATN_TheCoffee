package com.example.thecoffee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.models.Category
import com.example.thecoffee.databinding.LayoutItemCategoryBinding

interface ItemCategoryRecyclerInterface {
    fun onClickItemDrink(position: Category)
}
class ItemCategoryRecyclerAdapter(
    val list: List<Category>,
    val onClickItemDrink: ItemCategoryRecyclerInterface
): RecyclerView.Adapter<ItemCategoryRecyclerAdapter.ItemCategoryViewHolder>() {
    private lateinit var binding: LayoutItemCategoryBinding
    inner class ItemCategoryViewHolder(val binding: LayoutItemCategoryBinding) : RecyclerView.ViewHolder (binding.root){
        fun bind(category: Category){
            Glide.with(itemView.context).load(category.image).into(binding.imageCategory)
            binding.nameCategory.text = category.name
        }
        init {
            binding.layoutCategory.setOnClickListener {
                onClickItemDrink.onClickItemDrink(list[adapterPosition])
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemCategoryBinding.inflate(view, parent, false)
        return ItemCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemCategoryViewHolder, position: Int) {
        holder.bind(list[position])
    }
}