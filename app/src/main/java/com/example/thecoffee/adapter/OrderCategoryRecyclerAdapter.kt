package com.example.thecoffee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.databinding.LayoutItemCategoryBinding

interface ItemCategoryRecyclerInterface {
    fun onClickItemDrink(position: Int)
}
class ItemCategoryRecyclerAdapter(
    val list: List<Category>,
    val onClickItemDrink: ItemCategoryRecyclerInterface
): RecyclerView.Adapter<ItemCategoryRecyclerAdapter.ItemCategoryViewHolder>() {
    private lateinit var binding: LayoutItemCategoryBinding
    inner class ItemCategoryViewHolder(binding: LayoutItemCategoryBinding) : RecyclerView.ViewHolder (binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemCategoryBinding.inflate(view, parent, false)
        return ItemCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemCategoryViewHolder, position: Int) {
        holder.itemView.apply {
            run {
                binding.nameCategory.text = list[position].name
                binding.imageCategory.setImageResource(list[position].image)
            }

            holder.itemView.setOnClickListener {
                onClickItemDrink.onClickItemDrink(position)
            }
        }
    }
}