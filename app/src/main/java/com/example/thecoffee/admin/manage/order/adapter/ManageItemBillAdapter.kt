package com.example.thecoffee.admin.manage.order.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
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
    var list: List<Bill>,
    val onClickItem: ManageItemBillAdapterInterface?
): RecyclerView.Adapter<ManageItemBillAdapter.ManageItemBillViewModel>(){
    private lateinit var binding: LayoutItemBillBinding
    inner class ManageItemBillViewModel(binding: LayoutItemBillBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(bill: Bill, context: Context){
//            binding.idOrderBill.text = "Đơn hàng ${bill.id}"
            binding.idOrderBill.text = bill.drinks!!.joinToString(", ") {"${it.drinkName}"}
            when(bill.status){
                -1L -> {
                    // huy
                    binding.statusBill.text =  context.getString(R.string.status_cancel)
                }
                0L -> {
                    // dang cho xac nhan - xac nhan
                    binding.statusBill.text =  context.getString(R.string.status_pre_confirm)
                }
                1L -> {
                    // da xac nhan - giao hang
                    binding.statusBill.text =  context.getString(R.string.status_confirm)
                    binding.statusBill.setTextColor(context.resources.getColor(R.color.orange_900,null))
                }
                2L -> {
                    // dang giao hang - hoan thanh
                    binding.statusBill.text = context.getString(R.string.status_delivery)
                    binding.statusBill.setTextColor(context.resources.getColor(R.color.orange_900,null))
                }
                3L -> {
                    // giao hang thanh cong
                    binding.statusBill.text = context.getString(R.string.status_done_delivery)
                    binding.statusBill.setTextColor(context.resources.getColor(R.color.green_900, null))
                }
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
                onClickItem?.onClickItem(list[adapterPosition])
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
        val context = holder.itemView.context
        holder.bind(list[position], context)
    }
}