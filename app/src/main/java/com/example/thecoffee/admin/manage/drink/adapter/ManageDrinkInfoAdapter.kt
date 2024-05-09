package com.example.thecoffee.admin.manage.drink.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.databinding.LayoutItemDrinkHomeBinding
import com.example.thecoffee.databinding.LayoutItemManageDrinkBinding
import com.example.thecoffee.order.model.Size
import com.example.thecoffee.order.model.Topping

class ManageDrinkInfoAdapter(
    val list: List<Any>
): RecyclerView.Adapter<ManageDrinkInfoAdapter.DrinkInfoViewHolder>(){
    private lateinit var binding: LayoutItemManageDrinkBinding
    inner class DrinkInfoViewHolder(binding: LayoutItemManageDrinkBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any){
            val name = when(item){
                is Size -> item.name
                is Topping -> item.name
                else -> throw IllegalArgumentException("Unrecognized type")
            }
            val price = when(item){
                is Size -> item.price
                is Topping -> item.price
                else -> throw IllegalArgumentException("Unrecognized type")
            }
            binding.edtTextName.setText(name)
            binding.edtTextPrice.setText("${String.format("%,d", price)}đ")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemManageDrinkBinding.inflate(view, parent, false)
        return DrinkInfoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DrinkInfoViewHolder, position: Int) {
        holder.bind(list[position])
    }
}