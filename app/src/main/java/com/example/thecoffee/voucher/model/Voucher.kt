package com.example.thecoffee.voucher.model

import java.io.Serializable

data class Voucher(
    val voucherId: String? = null,
    val unit: String? = null, // don vi tien te cua voucher -> dong hay %
    val type: String? = null, // ap dung cho spham hay la van chuyen -> Drink or Ship
    val supportIdItems: List<String>? = null, // luu categoryId -> ap dung do voi cac spham cung loai
    val start_date: String? = null,
    val end_date: String? = null,
    val name: String? = null,
    val discount: Int? = null,
    val amount: Int? = null,  // SOS -> khong xử lý được case giới hạn số lượng voucher -> cần có BE
    val expired: Boolean? = null,  // SOS: khong dung den
): Serializable {
}