package com.example.thecoffee.order.adapter

import android.icu.text.Transliterator.Position
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.databinding.LayoutItemSizeBinding
import com.example.thecoffee.order.model.Size

interface ItemSizeRecyclerInterface {
    fun onRadioChanged(price: Long?, sizeName: String)
}

class ItemSizeRecyclerAdapter(
    val list: List<Size>,
    val onRadioChanged: ItemSizeRecyclerInterface
) : RecyclerView.Adapter<ItemSizeRecyclerAdapter.ItemSizeViewHolder>() {
    private lateinit var binding: LayoutItemSizeBinding
    private var selectedPosition = 1

    inner class ItemSizeViewHolder(val binding: LayoutItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: Size, position: Int) {
            binding.radioBtn.isChecked = position == selectedPosition

            binding.radioBtn.text = size.name
            binding.priceSize.text = "${String.format("%,d", size.price)}đ"


            binding.radioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    selectedPosition = adapterPosition
                    notifyDataSetChanged()
                    onRadioChanged.onRadioChanged(size.price, buttonView.text.toString())
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSizeViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemSizeBinding.inflate(view, parent, false)
        return ItemSizeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemSizeViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

}