package com.example.thecoffee.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.models.Drink
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
            binding.priceDisDrink.text = drink.price.toString().format("%,d")
            binding.priceDrink.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // gach ngang text
            binding.discountDrink.text = drink.discount.toString().format("%,d")
            binding.priceDisDrink.text = (drink.price!! - drink.discount!!).toString().format("%,d")

//            d"${String.format("%,d", drink.price)}đ"
        }
        init {
            binding.cardView.setOnClickListener {
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