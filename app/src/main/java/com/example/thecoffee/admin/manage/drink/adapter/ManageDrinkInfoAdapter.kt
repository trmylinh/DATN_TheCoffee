package com.example.thecoffee.admin.manage.drink.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.databinding.LayoutItemDrinkHomeBinding
import com.example.thecoffee.databinding.LayoutItemManageDrinkBinding
import com.example.thecoffee.order.model.Size
import com.example.thecoffee.order.model.Topping
import java.io.Serializable

class ManageDrinkInfoAdapter(
    val listSize: List<Size>? = null,
    val listTopping: List<Topping>? = null,
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
        if(listSize != null){
            (listSize as MutableList).removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listSize.size)
            return
        }
        if(listTopping != null){
            (listTopping as MutableList).removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listTopping.size)
            return
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemManageDrinkBinding.inflate(view, parent, false)
        return DrinkInfoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if(listSize != null){
            return listSize.size
        }

        if(listTopping != null){
            return listTopping.size
        }

        return 0
    }

    override fun onBindViewHolder(holder: DrinkInfoViewHolder, position: Int) {
        holder.bind(if(listSize != null) listSize[position] else listTopping!![position], isEditable)
    }
}