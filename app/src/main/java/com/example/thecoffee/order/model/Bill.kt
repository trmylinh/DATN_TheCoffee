package com.example.thecoffee.order.model

import java.io.Serializable

data class Bill(
    val id: String? = null,
    val userId: String? = null,
    val address: String? = null,
    val drinks: List<Cart>? = emptyList(), // cac spham trong don hang
    val status: String? = null,  // trang thai don hang
    val shipFee: Long? = null,
    val time: String? = null,
//    val voucher: Int? = null,
): Serializable {

}