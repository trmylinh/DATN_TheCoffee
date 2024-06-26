package com.example.thecoffee.order.model

import java.io.Serializable

data class Drink (
    val drinkId: String? = null,
    val name: String? = null,
    val desc: String? = null,
    val image: String? = null,
    val price: Int? = null,
    var discount: Int? = null,
    val categoryId: String? = null,
    val outOfStock: Boolean? = null,
    val size: List<Size>? = emptyList(),
    val topping: List<Topping>? = emptyList()
): Serializable {

}