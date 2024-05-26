package com.example.thecoffee.voucher.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.voucher.model.Voucher

class VoucherDiffCallback : DiffUtil.ItemCallback<Voucher>() {
    override fun areItemsTheSame(oldItem: Voucher, newItem: Voucher): Boolean {
        return oldItem.voucherId == newItem.voucherId
    }

    override fun areContentsTheSame(oldItem: Voucher, newItem: Voucher): Boolean {
        return oldItem == newItem
    }
}