package com.example.thecoffee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.LayoutItemDrinkCategoryBinding

interface ItemDrinkCategoryRecyclerInterface {
    fun onClickItemDrink(position: Drink)
}
class ItemDrinkCategoryRecyclerAdapter(
    val list: List<Drink>,
    val onClickItemDrink: ItemDrinkCategoryRecyclerInterface
): RecyclerView.Adapter<ItemDrinkCategoryRecyclerAdapter.ItemDrinkCategoryViewHolder>() {
    private lateinit var binding: LayoutItemDrinkCategoryBinding
    inner class ItemDrinkCategoryViewHolder(binding: LayoutItemDrinkCategoryBinding) : RecyclerView.ViewHolder (binding.root){
        fun bind(drink: Drink){
            binding.nameDrink.text = drink.name
            binding.priceDrink.text ="${String.format("%,d", drink.price)}đ"
            Glide.with(itemView.context).load(drink.image).into(binding.imageDrink)
        }
        init {
            binding.cardVideBtnAdd.setOnClickListener {
                onClickItemDrink.onClickItemDrink(list[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDrinkCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemDrinkCategoryBinding.inflate(view, parent, false)
        return ItemDrinkCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemDrinkCategoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

}
