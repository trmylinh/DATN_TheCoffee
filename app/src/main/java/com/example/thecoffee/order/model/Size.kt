package com.example.thecoffee.order.model

import java.io.Serializable

data class Size(
    val name: String? = null,
    val price: Long? = null
): Serializable {
}