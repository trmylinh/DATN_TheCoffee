package com.example.thecoffee.data.models

import android.net.Uri
import java.io.Serializable

data class Drink (
    val id: String,
    val name: String,
    val desc: String?,
    val images: Int,
    val price: Int,
    val discount: Int,
//    val toppings: MutableList<String>?,
    val categoryId: String
) {

}