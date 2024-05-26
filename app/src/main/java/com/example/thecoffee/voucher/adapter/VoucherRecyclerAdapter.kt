package com.example.thecoffee.voucher.adapter

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.databinding.LayoutItemMyVoucherBinding
import com.example.thecoffee.order.utils.CategoryDiffCallBack
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.utils.VoucherDiffCallback
import com.example.thecoffee.voucher.view.VoucherFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

interface VoucherRecyclerInterface {
    fun onClickItemVoucher(position: Int)
}

class VoucherRecyclerAdapter(
    context: Context,
    val onClickItemVoucher: VoucherRecyclerInterface,
    val isAdmin: Boolean
) : ListAdapter<Voucher, VoucherRecyclerAdapter.VoucherViewHolder>(
    AsyncDifferConfig.Builder(VoucherDiffCallback())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    private lateinit var binding: LayoutItemMyVoucherBinding

    inner class VoucherViewHolder(binding: LayoutItemMyVoucherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(voucher: Voucher) {
            binding.titleVoucher.text = voucher.name
            binding.textDate.text = "Hết hạn ${voucher.end_date}"
//            binding.textAmount.text = "Số lượng còn lại: ${voucher.amount}"
        }


        init {
            binding.layoutItemVoucher.setOnClickListener {
                onClickItemVoucher.onClickItemVoucher(adapterPosition)
            }
        }

    }

    fun stringToLocalDate(dateString: String): Date? {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            sdf.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getDateOnly(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemMyVoucherBinding.inflate(view, parent, false)
        return VoucherViewHolder(binding)
    }


    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}