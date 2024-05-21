package com.example.thecoffee.order.utils

import androidx.recyclerview.widget.DiffUtil

class DrinksByCategoryDiffCallBack: DiffUtil.ItemCallback<DrinksByCategory>() {
    override fun areItemsTheSame(oldItem: DrinksByCategory, newItem: DrinksByCategory): Boolean {
        return when{
            oldItem is DrinksByCategory.TypeCategory && newItem is DrinksByCategory.TypeCategory -> {
                // So sánh theo categoryName
                oldItem.categoryName == newItem.categoryName
            }
            oldItem is DrinksByCategory.TypeDrink && newItem is DrinksByCategory.TypeDrink -> {
                oldItem.drink.drinkId == newItem.drink.drinkId
            }
            else -> false
        }
    }


    override fun areContentsTheSame(oldItem: DrinksByCategory, newItem: DrinksByCategory): Boolean {
        return when {
            oldItem is DrinksByCategory.TypeCategory && newItem is DrinksByCategory.TypeCategory -> {
                oldItem == newItem
            }
            oldItem is DrinksByCategory.TypeDrink && newItem is DrinksByCategory.TypeDrink -> {
                oldItem.drink == newItem.drink
            }
            else -> false
        }
    }
}