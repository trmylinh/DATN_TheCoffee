package com.example.thecoffee.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.databinding.LayoutItemToppingBinding
import com.example.thecoffee.order.model.Topping

interface ItemToppingRecyclerInterface {
    fun onTotalChanged(total: Int?)
}

class ItemToppingRecyclerAdapter(
    val list: List<Topping>,
    val onTotalChanged: ItemToppingRecyclerInterface
) : RecyclerView.Adapter<ItemToppingRecyclerAdapter.ItemToppingViewHolder>() {
    private lateinit var binding: LayoutItemToppingBinding
    private var selectedCheckbox: Int = 0
    private var totalPriceTopping: Int = 0

    inner class ItemToppingViewHolder(val binding: LayoutItemToppingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topping: Topping) {
            binding.checkBox.text = topping.name
            binding.priceTopping.text = "${String.format("%,d", topping.price)}đ"

            binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (selectedCheckbox == 2) {
                    if (!isChecked) {
                        selectedCheckbox--
                        totalPriceTopping -= topping.price!!
                        onTotalChanged.onTotalChanged(totalPriceTopping)
                    } else {
                        buttonView.isChecked = false
                    }
                } else {
                    if (isChecked) {
                        selectedCheckbox++
                        totalPriceTopping += topping.price!!
                        onTotalChanged.onTotalChanged(totalPriceTopping)
                    } else {
                        selectedCheckbox--
                        totalPriceTopping -= topping.price!!
                        onTotalChanged.onTotalChanged(totalPriceTopping)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemToppingViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemToppingBinding.inflate(view, parent, false)
        return ItemToppingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemToppingViewHolder, position: Int) {
        holder.bind(list[position])
    }


}