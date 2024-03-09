package com.example.thecoffee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.models.Drink
import com.example.thecoffee.databinding.LayoutCategoryNameBinding
import com.example.thecoffee.databinding.LayoutItemDrinkCategoryBinding

interface ItemDrinkCategoryRecyclerInterface {
    fun onClickItemDrink(position: Drink)
}
class ItemDrinkCategoryRecyclerAdapter(
    val list: List<Any>,
    val onClickItemDrink: ItemDrinkCategoryRecyclerInterface
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private companion object {
        private const val VIEW_TYPE_CATEGORY = 0
        private const val VIEW_TYPE_PRODUCT = 1
    }
    inner class ProductViewHolder(val binding: LayoutItemDrinkCategoryBinding) : RecyclerView.ViewHolder (binding.root){
        fun bind(drink: Drink){
            binding.nameDrink.text = drink.name
            binding.priceDrink.text ="${String.format("%,d", drink.price)}đ"
            Glide.with(itemView.context).load(drink.image).into(binding.imageDrink)
        }
        init {
            binding.cardVideBtnAdd.setOnClickListener {
//                onClickItemDrink.onClickItemDrink()
            }
        }
    }
    inner class CategoryViewHolder(val binding: LayoutCategoryNameBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return if (item is String) VIEW_TYPE_CATEGORY else VIEW_TYPE_PRODUCT
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDrinkCategoryViewHolder {
//        val view = LayoutInflater.from(parent.context)
//        binding = LayoutItemDrinkCategoryBinding.inflate(view, parent, false)
//        return ItemDrinkCategoryViewHolder(binding)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == VIEW_TYPE_CATEGORY){
            val categoryView = LayoutCategoryNameBinding.inflate(inflater, parent, false)
            CategoryViewHolder(categoryView)
        } else {
            val productView = LayoutItemDrinkCategoryBinding.inflate(inflater, parent, false)
            ProductViewHolder(productView)
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(holder is CategoryViewHolder){
            val category = item as String
            holder.binding.tvCategoryName.text = category
        } else if (holder is ProductViewHolder){
            val product = item as Drink
            holder.bind(product)
        }
    }

}
