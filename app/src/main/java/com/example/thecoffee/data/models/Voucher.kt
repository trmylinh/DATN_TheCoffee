package com.example.thecoffee.data.models

data class Voucher(
    val id: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val desc: String? = null,
    val title: String? = null,
    val min: Number? = null, // voucher giam toi thieu la bao nhieu
    val max: Number? = null, // voucher giam toi da la bao nhieu
    val condition: String? = null, // dieu kien ap dung voucher
)