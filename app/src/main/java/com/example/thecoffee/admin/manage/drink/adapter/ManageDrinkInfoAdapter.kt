package com.example.thecoffee.admin.manage.drink.adapter

import android.view.LayoutInflater
import android.view.View
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

    var isEditable: Boolean = false
        set(value){
            field = value
            notifyDataSetChanged()
        }
    inner class DrinkInfoViewHolder(val binding: LayoutItemManageDrinkBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any, isEditable: Boolean){
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
            binding.edtTextName.isEnabled = isEditable
            binding.edtTextName.isCursorVisible = isEditable
            binding.edtTextName.isFocusableInTouchMode = isEditable

            binding.edtTextPrice.isCursorVisible = isEditable
            binding.edtTextPrice.isEnabled = isEditable
            binding.edtTextPrice.isFocusableInTouchMode = isEditable

            binding.edtTextName.setText(name)
            binding.edtTextPrice.setText("${String.format("%,d", price)}")

            binding.btnDelete.visibility = if (isEditable) View.VISIBLE else View.GONE

            binding.btnDelete.setOnClickListener {
                removeItem(adapterPosition)
            }
        }

    }

    private fun removeItem(position: Int){
        (list as MutableList).removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
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
        holder.bind(list[position], isEditable)
    }
}