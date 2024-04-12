package com.example.thecoffee.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.thecoffee.databinding.LayoutItemChosenBillBinding
import com.example.thecoffee.order.model.Cart

class ItemChosenBillRecyclerAdapter(
    val list: List<Cart>
) : RecyclerView.Adapter<ItemChosenBillRecyclerAdapter.ItemChosenBillViewHolder>() {
    private lateinit var binding: LayoutItemChosenBillBinding
    private var viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    inner class ItemChosenBillViewHolder(binding: LayoutItemChosenBillBinding) :
        RecyclerView.ViewHolder(binding.root) {
            var swipeRevealLayout: SwipeRevealLayout
        fun bind(cart: Cart) {
            binding.nameItem.text = "x${cart.quantity} ${cart.drinkName}"
            binding.sizeItem.text = cart.drinkSize

            if (cart.drinkTopping?.isNotEmpty() == true) {
                if (cart.drinkTopping.size == 2) {
                    binding.toppingItem1.text = cart.drinkTopping[0]
                    binding.toppingItem2.text = cart.drinkTopping[1]
                } else {
                    binding.toppingItem1.text = cart.drinkTopping[0]
                    binding.toppingItem2.visibility = View.GONE
                }
            } else {
                binding.toppingItem1.visibility = View.GONE
                binding.toppingItem2.visibility = View.GONE
            }

            if (cart.note?.isNotEmpty() == true) {
                binding.noteItem.text = cart.note
            } else {
                binding.noteItem.visibility = View.GONE
            }

            binding.priceItem.text = "${String.format("%,d", cart.totalPrice)}đ"

        }

        init {
            swipeRevealLayout = binding.swipeLayout

            
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemChosenBillViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemChosenBillBinding.inflate(view, parent, false)
        return ItemChosenBillViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemChosenBillViewHolder, position: Int) {
        holder.bind(list[position])
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(holder.swipeRevealLayout, (position to list[position].drinkName).toString())

    }


}