package com.example.thecoffee.order.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.databinding.LayoutCategoryNameBinding
import com.example.thecoffee.databinding.LayoutEmptyWarningBinding
import com.example.thecoffee.databinding.LayoutItemDrinkCategoryBinding
import com.example.thecoffee.order.utils.CategoryDiffCallBack
import com.example.thecoffee.order.utils.DrinksByCategory
import com.example.thecoffee.order.utils.DrinksByCategoryDiffCallBack
import java.util.concurrent.Executors

interface ItemDrinkCategoryRecyclerInterface {
    fun onClickItemDrink(position: Drink)
}
class ItemDrinkCategoryRecyclerAdapter(
    context: Context,
    val onClickItemDrink: ItemDrinkCategoryRecyclerInterface,
    val marginBottom: Int,
    val isAdmin: Boolean,
): ListAdapter<DrinksByCategory, RecyclerView.ViewHolder>(
// chỉ định là DrinksByCategoryDiffCallBack được chạy dưới BackgroundThread để tránh gây giật lag View
    AsyncDifferConfig.Builder(DrinksByCategoryDiffCallBack())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    private companion object {
        private const val VIEW_TYPE_CATEGORY = 0
        private const val VIEW_TYPE_PRODUCT = 1
        private const val VIEW_TYPE_EMPTY = 2
    }
    inner class ProductViewHolder(val binding: LayoutItemDrinkCategoryBinding) : RecyclerView.ViewHolder (binding.root){
        fun bind(drink: Drink){
            binding.nameDrink.text = drink.name
            binding.priceDrink.text ="${String.format("%,d", drink.price)}đ"
            Glide.with(itemView.context).load(drink.image).into(binding.imageDrink)

            binding.viewDisable.visibility =  if(drink.isOutOfStock == true) View.VISIBLE else View.GONE
            binding.tvOutOfStock.visibility =  if(drink.isOutOfStock == true) View.VISIBLE else View.GONE

            if(isAdmin){
                binding.cardVideBtnAdd.visibility = View.GONE
                binding.viewDisable.visibility = View.GONE
            }
        }
        init {
            binding.cardVideBtnAdd.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val drink = getItem(position) as DrinksByCategory.TypeDrink
                    onClickItemDrink.onClickItemDrink(drink.drink)
                }
            }

            binding.viewItem.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val drink = getItem(position) as DrinksByCategory.TypeDrink
                    onClickItemDrink.onClickItemDrink(drink.drink)
                }
            }
        }

        fun setMarginForLastItem(isLastItem: Boolean, marginBottom: Int){
            val layoutParams = binding.viewItem.layoutParams as RecyclerView.LayoutParams
            if(isLastItem){
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, marginBottom)
            }
            binding.viewItem.layoutParams = layoutParams
        }
    }
    inner class CategoryViewHolder(val binding: LayoutCategoryNameBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(categoryName: String){
            binding.tvCategoryName.text = categoryName
        }
    }

    inner class EmptyWarningViewHolder(val binding: LayoutEmptyWarningBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(error: String){
            binding.tvEmptyWarning.text = error
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DrinksByCategory.TypeCategory -> VIEW_TYPE_CATEGORY
            is DrinksByCategory.TypeDrink -> VIEW_TYPE_PRODUCT
            is DrinksByCategory.TypeEmpty -> VIEW_TYPE_EMPTY
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> {
                val categoryView = LayoutCategoryNameBinding.inflate(inflater, parent, false)
                CategoryViewHolder(categoryView)
            }
            VIEW_TYPE_EMPTY -> {
                val emptyView = LayoutEmptyWarningBinding.inflate(inflater, parent, false)
                EmptyWarningViewHolder(emptyView)
            }
            else -> {
                val productView = LayoutItemDrinkCategoryBinding.inflate(inflater, parent, false)
                ProductViewHolder(productView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val isLastItem = position == itemCount - 1
        when(holder){
            is CategoryViewHolder -> {
                val category = item as DrinksByCategory.TypeCategory
                holder.bind(category.categoryName)
            }
            is ProductViewHolder -> {
                val product = item as DrinksByCategory.TypeDrink
                holder.bind(product.drink)
                holder.setMarginForLastItem(isLastItem, marginBottom)
            }
            is EmptyWarningViewHolder -> {
                val error = item as DrinksByCategory.TypeEmpty
                holder.bind(error.message)
            }
        }
    }

}
