package com.example.thecoffee.admin.manage.voucher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.databinding.LayoutItemChoosenVoucherBinding
import com.example.thecoffee.databinding.LayoutItemManageDrinkBinding
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import java.util.Locale

class SupportItemsVoucherAdapter(
    private var list: List<Any>,
    private val checkedStates: HashMap<Int, Boolean>,
    private val onCheckboxClicked: (Int, Boolean) -> Unit
): RecyclerView.Adapter<SupportItemsVoucherAdapter.SupportItemsVoucherViewHolder>() {
    private lateinit var binding: LayoutItemChoosenVoucherBinding
    private var filteredList: List<Any> = list
    inner class SupportItemsVoucherViewHolder(val binding: LayoutItemChoosenVoucherBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any){
            when(item){
                is Category -> {
                    Glide.with(itemView.context).load(item.image).into(binding.imgItem)
                    binding.checkBox.text = item.name

                    binding.checkBox.setOnCheckedChangeListener(null)
                    binding.checkBox.isChecked = checkedStates[adapterPosition] ?: false

                    binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                        checkedStates[adapterPosition] = isChecked
                        onCheckboxClicked(adapterPosition, isChecked)
                    }
                }
                is Drink -> {
                    Glide.with(itemView.context).load(item.image).into(binding.imgItem)
                    binding.checkBox.text = item.name

                    binding.checkBox.setOnCheckedChangeListener(null)
                    binding.checkBox.isChecked = checkedStates[adapterPosition] ?: false

                    binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                        checkedStates[adapterPosition] = isChecked
                        onCheckboxClicked(adapterPosition, isChecked)
                    }
                }
            }
        }
    }

    fun updateData(newList: List<Any>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SupportItemsVoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemChoosenVoucherBinding.inflate(view, parent, false)
        return SupportItemsVoucherViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SupportItemsVoucherViewHolder, position: Int) {
        holder.bind(list[position])
    }
}