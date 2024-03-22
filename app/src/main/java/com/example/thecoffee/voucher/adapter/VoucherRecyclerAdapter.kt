package com.example.thecoffee.voucher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.databinding.LayoutItemMyVoucherBinding

interface VoucherRecyclerInterface {
    fun onClickItemVoucher(position: Int)
}
class VoucherRecyclerAdapter(
    val list: List<Voucher>,
    val onClickItemVoucher: VoucherRecyclerInterface
) : RecyclerView.Adapter<VoucherRecyclerAdapter.VoucherViewHolder>()
{
    private lateinit var binding: LayoutItemMyVoucherBinding
    inner class VoucherViewHolder(binding: LayoutItemMyVoucherBinding): RecyclerView.ViewHolder (binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemMyVoucherBinding.inflate(view, parent, false)
        return VoucherViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.itemView.apply {
            run {
                binding.titleVoucher.text = list[position].title
                binding.textDate.text = "Hết hạn ${list[position].endDate}"
            }

            holder.itemView.setOnClickListener {
                onClickItemVoucher.onClickItemVoucher(position)
            }
        }
    }
}