package com.example.thecoffee.order.model

import java.io.Serializable

data class Drink (
    val drinkId: String? = null,
    val name: String? = null,
    val desc: String? = null,
    val image: String? = null,
    val price: Int? = null,
    val categoryId: String? = null,
    val isOutOfStock: Boolean? = null,
    val size: List<Size>? = emptyList(),
    val topping: List<Topping>? = emptyList()
): Serializable {

}