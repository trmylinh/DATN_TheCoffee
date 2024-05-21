package com.example.thecoffee.order.utils

import com.example.thecoffee.order.model.Drink

sealed class DrinksByCategory {
    data class TypeCategory(val categoryName: String): DrinksByCategory()
    data class TypeDrink(val drink: Drink): DrinksByCategory()
    data class TypeEmpty(val message: String): DrinksByCategory()

}