package com.example.thecoffee.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.LayoutItemDrinkHomeBinding

interface ItemDrinkHomeRecyclerInterface {
    fun onClickItemDrink(position: Int)
}
class ItemDrinkHomeRecyclerAdapter(
    val list: List<Drink>,
    val onClickItemDrink: ItemDrinkHomeRecyclerInterface
) : RecyclerView.Adapter<ItemDrinkHomeRecyclerAdapter.ItemDrinkHomeViewHolder>(){
    private lateinit var binding: LayoutItemDrinkHomeBinding
    inner class ItemDrinkHomeViewHolder(binding: LayoutItemDrinkHomeBinding) : RecyclerView.ViewHolder (binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDrinkHomeViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemDrinkHomeBinding.inflate(view, parent, false)
        return ItemDrinkHomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemDrinkHomeViewHolder, position: Int) {
        holder.itemView.apply {
            run {
                binding.imageDrink.setImageResource(list[position].images)
                binding.nameDrink.text = list[position].name
                binding.priceDrink.text = "${String.format("%,d", list[position].price)}đ"
                binding.discountDrink.text = "-${String.format("%,d", list[position].discount)}đ"
                binding.priceDisDrink.text = "${String.format("%,d", list[position].price - list[position].discount)}đ"
                binding.priceDrink.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }

            holder.itemView.setOnClickListener {
                onClickItemDrink.onClickItemDrink(position)
            }
        }
    }
}