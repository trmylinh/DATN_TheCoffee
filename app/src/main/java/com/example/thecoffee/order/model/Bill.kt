package com.example.thecoffee.order.model

import java.io.Serializable

data class Bill(
    val id: String? = null,
    val userId: String? = null,
    val address: String? = null,
    val drinks: List<Cart>? = emptyList(), // cac spham trong don hang
    var status: Long? = null,  // trang thai don hang
    /*
    -1 - huy -> do user huy (trong vong 1 khoang tgian nhat dinh)
    0 - dang cho xac nhan
    1 - da xac nhan
    2 - dang giao hang
    3 - giao hang thanh cong
    -> 0 -> 3 do admin chuyen status
    */
    val shipFee: Long? = null,
    val time: String? = null,
//    val voucher: Int? = null,
): Serializable {

}