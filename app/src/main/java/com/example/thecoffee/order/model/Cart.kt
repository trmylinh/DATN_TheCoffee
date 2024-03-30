package com.example.thecoffee.order.model

import java.io.Serializable

data class Cart (
    // item - product -> drink(size, topping di kem)
    // sluong
    // tong tien
    val totalPrice: Long?,
    val quantity: Int?,
    val drinkName: String?,
    val drinkSize: String?,
    val drinkTopping: List<String>?,
    val note: String?
)