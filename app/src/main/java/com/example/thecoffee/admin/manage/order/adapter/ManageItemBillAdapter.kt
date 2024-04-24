package com.example.thecoffee.admin.manage.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.databinding.LayoutItemBillBinding
import com.example.thecoffee.databinding.LayoutItemDrinkHomeBinding
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Category

interface ManageItemBillAdapterInterface {
    fun onClickItem(position: Bill)
}

class ManageItemBillAdapter(
    val list: List<Bill>,
    val onClickItem: ManageItemBillAdapterInterface
): RecyclerView.Adapter<ManageItemBillAdapter.ManageItemBillViewModel>(){
    private lateinit var binding: LayoutItemBillBinding
    inner class ManageItemBillViewModel(binding: LayoutItemBillBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(bill: Bill){
//            binding.idOrderBill.text = "Đơn hàng ${bill.id}"
            binding.idOrderBill.text = bill.drinks!!.joinToString(", ") {"${it.drinkName}"}
            binding.statusBill.text = when(bill.status){
                (-1).toLong() -> "${R.string.status_cancel}"
                0.toLong() -> "${R.string.status_pre_confirm}"
                1.toLong() -> "${R.string.status_confirm}"
                2.toLong() -> "${R.string.delivery}"
                3.toLong() -> "${R.string.status_done_delivery}"
                else -> "-----"
            }
            binding.dateBill.text = bill.time

            // total price
            var sum: Long = 0
            for (item in bill.drinks!!){
                sum += item.totalPrice!!
            }
            binding.priceBill.text = "${String.format("%,d", sum)}đ"
        }

        init {
            binding.itemBill.setOnClickListener {
                onClickItem.onClickItem(list[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageItemBillViewModel {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemBillBinding.inflate(view, parent, false)
        return ManageItemBillViewModel(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ManageItemBillViewModel, position: Int) {
        holder.bind(list[position])
    }
}