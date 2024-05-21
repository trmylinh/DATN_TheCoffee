package com.example.thecoffee.order.model

import android.net.Uri
import java.io.Serializable

data class Drink (
    val drinkId: String? = null,
    val name: String? = null,
    val desc: String? = null,
    val image: String? = null,
    val price: Int? = null,
    val discount: Int? = null,
    val categoryId: String? = null,
    val outOfStock: Boolean? = false,
    val size: List<Size>? = emptyList(),
    val topping: List<Topping>? = emptyList()
): Serializable {

}