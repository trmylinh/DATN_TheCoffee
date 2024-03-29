package com.example.thecoffee.order.model

import android.net.Uri
import java.io.Serializable

data class Drink (
    val id: String? = null,
    val name: String? = null,
    val desc: String? = null,
    val image: String? = null,
    val price: Int? = null,
    val discount: Int? = null,
    val categoryId: String? = null,
    var countTopping: Int = 0
): Serializable {

}