package com.example.thecoffee.order.model

import java.io.Serializable

data class Cart (
    // item - product -> drink(size, topping di kem)
    // sluong
    // tong tien
    val totalPrice: Long? = null,
    val quantity: Long? = null,
    val drinkName: String? = null,
    val drinkSize: String? = null,
    val drinkTopping: List<String>? = emptyList(),
    val note: String? = null
){
    override fun equals(other: Any?): Boolean {
        if(this === other) return true

        if(javaClass != other?.javaClass) return false

        other as Cart

        return drinkName == other.drinkName
                && drinkSize == other.drinkSize
                && drinkTopping == other.drinkTopping
    }
}