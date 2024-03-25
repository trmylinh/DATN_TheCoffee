package com.example.thecoffee.order.model

data class Cart (
    // item - product -> drink, topping
    // sluong
    val totalPrice: Int?,
    val quantity: Int?,
    val drinkName: String?,
    val topping: List<String>?
){

}