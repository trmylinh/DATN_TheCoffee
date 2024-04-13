package com.example.thecoffee.home.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.databinding.LayoutItemDrinkHomeBinding

interface ItemDrinkHomeRecyclerInterface {
    fun onClickItemDrink(position: Drink)
}
class ItemDrinkHomeRecyclerAdapter(
    val list: List<Drink>,
    val onClickItemDrink: ItemDrinkHomeRecyclerInterface
) : RecyclerView.Adapter<ItemDrinkHomeRecyclerAdapter.ItemDrinkHomeViewHolder>(){
    private lateinit var binding: LayoutItemDrinkHomeBinding
    inner class ItemDrinkHomeViewHolder(val binding: LayoutItemDrinkHomeBinding)
        : RecyclerView.ViewHolder (binding.root){
        fun bind(drink: Drink){
            Glide.with(itemView.context).load(drink.image).into( binding.imageDrink)
            binding.nameDrink.text = drink.name
            binding.priceDrink.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // gach ngang text
            binding.priceDrink.text = "${String.format("%,d", drink.price)}đ"
            binding.discountDrink.text = "-${String.format("%,d", drink.discount)}đ"
            val priceDis = drink.price!! - drink.discount!!
            binding.priceDisDrink.text =  "${String.format("%,d", priceDis)}đ"

        }
        init {
            binding.btnAdd.setOnClickListener {
                onClickItemDrink.onClickItemDrink(list[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDrinkHomeViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemDrinkHomeBinding.inflate(view, parent, false)
        return ItemDrinkHomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemDrinkHomeViewHolder, position: Int) {
        holder.bind(list[position])
    }
}