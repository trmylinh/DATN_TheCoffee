package com.example.thecoffee.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.databinding.LayoutItemDrinkHomeBinding
import com.example.thecoffee.databinding.LayoutItemToppingBinding
import com.example.thecoffee.models.Drink
import com.example.thecoffee.models.Topping

interface ItemToppingRecyclerInterface {
    fun onClickItemDrink(position: Topping)
}
class ItemToppingRecyclerAdapter(
    val list: List<Topping>
) : RecyclerView.Adapter<ItemToppingRecyclerAdapter.ItemToppingViewHolder>(){
    private lateinit var binding: LayoutItemToppingBinding
    inner class ItemToppingViewHolder(val binding: LayoutItemToppingBinding)
        : RecyclerView.ViewHolder (binding.root){
        fun bind(topping: Topping){
            binding.checkBox.text = topping.name
            binding.priceTopping.text = "${String.format("%,d", topping.price)}đ"
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