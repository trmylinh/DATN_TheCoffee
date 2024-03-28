package com.example.thecoffee.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.databinding.LayoutItemToppingBinding
import com.example.thecoffee.order.model.Topping

interface ItemToppingRecyclerInterface {
    fun onTotalChanged(total: Long?, list: List<String>)
}

class ItemToppingRecyclerAdapter(
    val list: List<Topping>,
    val onTotalChanged: ItemToppingRecyclerInterface
) : RecyclerView.Adapter<ItemToppingRecyclerAdapter.ItemToppingViewHolder>() {
    private lateinit var binding: LayoutItemToppingBinding
    private var selectedCheckbox: Int = 0
    private var totalPriceTopping: Long = 0
    private var selectedListTopping = mutableListOf<String>()

    inner class ItemToppingViewHolder(val binding: LayoutItemToppingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topping: Topping) {
            binding.checkBox.text = topping.name
            binding.priceTopping.text = "${String.format("%,d", topping.price)}đ"

            binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (selectedCheckbox == 2) {
                    if (!isChecked) {
                        selectedCheckbox--
                        selectedListTopping.remove(buttonView.text.toString())
                        totalPriceTopping -= topping.price!!
                        onTotalChanged.onTotalChanged(totalPriceTopping, selectedListTopping)
                    } else {
                        buttonView.isChecked = false
                    }
                } else {
                    if (isChecked) {
                        selectedCheckbox++
                        selectedListTopping.add(buttonView.text.toString())
                        totalPriceTopping += topping.price!!
                        onTotalChanged.onTotalChanged(totalPriceTopping, selectedListTopping)
                    } else {
                        selectedCheckbox--
                        selectedListTopping.remove(buttonView.text.toString())
                        totalPriceTopping -= topping.price!!
                        onTotalChanged.onTotalChanged(totalPriceTopping, selectedListTopping)
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