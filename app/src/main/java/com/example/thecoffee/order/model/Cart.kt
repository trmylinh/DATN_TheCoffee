package com.example.thecoffee.order.model

import java.io.Serializable

data class Cart (
    // item - product -> drink(size, topping di kem)
    // sluong
    // tong tien
    val totalPrice: Long? = null,
    val quantity: Int? = null,
    val drinkName: String? = null,
    val drinkSize: String? = null,
    val drinkTopping: List<String>? = emptyList(),
    val note: String? = null
)