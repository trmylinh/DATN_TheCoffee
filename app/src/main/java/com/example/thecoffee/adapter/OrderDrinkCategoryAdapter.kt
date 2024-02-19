package com.example.thecoffee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.LayoutItemDrinkCategoryBinding

interface ItemDrinkCategoryRecyclerInterface {
    fun onClickItemDrink(position: Int)
}
class ItemDrinkCategoryRecyclerAdapter(
    val list: List<Drink>,
    val onClickItemDrink: ItemDrinkCategoryRecyclerInterface
): RecyclerView.Adapter<ItemDrinkCategoryRecyclerAdapter.ItemDrinkCategoryViewHolder>() {
    private lateinit var binding: LayoutItemDrinkCategoryBinding
    inner class ItemDrinkCategoryViewHolder(binding: LayoutItemDrinkCategoryBinding) : RecyclerView.ViewHolder (binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDrinkCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemDrinkCategoryBinding.inflate(view, parent, false)
        return ItemDrinkCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemDrinkCategoryViewHolder, position: Int) {
        holder.itemView.apply {
            run {
                binding.nameDrink.text = list[position].type.name
                binding.imageDrink.setImageResource(list[position].images)
                binding.priceDrink.text = "${String.format("%,d", list[position].price)}đ"
            }

            holder.itemView.setOnClickListener {
                onClickItemDrink.onClickItemDrink(position)
            }
        }
    }

}
