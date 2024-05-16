package com.example.thecoffee.order.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.thecoffee.order.model.Category

class CategoryDiffCallBack: DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}