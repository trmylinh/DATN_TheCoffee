package com.example.thecoffee.order.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.databinding.LayoutItemCategoryBinding
import com.example.thecoffee.order.utils.CategoryDiffCallBack
import java.util.concurrent.Executor
import java.util.concurrent.Executors

interface ItemCategoryRecyclerInterface {
    fun onClickItemCategory(category: Category)
}
class ItemCategoryRecyclerAdapter(
    context: Context,
    val onClickItemCategory: ItemCategoryRecyclerInterface
): ListAdapter<Category, ItemCategoryRecyclerAdapter.ItemCategoryViewHolder>(
    // chỉ định là CategoryDiffCallBack được chạy dưới BackgroundThread để tránh gây giật lag View
    AsyncDifferConfig.Builder(CategoryDiffCallBack())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    private lateinit var binding: LayoutItemCategoryBinding
    inner class ItemCategoryViewHolder(val binding: LayoutItemCategoryBinding) : RecyclerView.ViewHolder (binding.root){
        fun bind(category: Category){
            Glide.with(itemView.context).load(category.image).into(binding.imageCategory)
            binding.nameCategory.text = category.name
        }
        init {
            binding.layoutCategory.setOnClickListener {
                onClickItemCategory.onClickItemCategory(category = getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemCategoryBinding.inflate(view, parent, false)
        return ItemCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<Category>?) {
        super.submitList(ArrayList<Category>(list ?: listOf()))
    }
}