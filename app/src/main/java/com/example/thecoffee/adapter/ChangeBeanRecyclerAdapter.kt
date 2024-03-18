package com.example.thecoffee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.thecoffee.data.models.Bean
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.LayoutItemChangeBeanBinding
import com.example.thecoffee.databinding.LayoutItemMyVoucherBinding

interface ChangeBeanRecyclerInterface {
    fun onClickItemBean(position: Int)
}
class ChangeBeanRecyclerAdapter(
    val list: List<Bean>,
    val onClickItemBean: ChangeBeanRecyclerInterface
) : RecyclerView.Adapter<ChangeBeanRecyclerAdapter.ChangeBeanViewHolder>(){
    private lateinit var binding: LayoutItemChangeBeanBinding
    inner class ChangeBeanViewHolder(binding: LayoutItemChangeBeanBinding): RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeBeanViewHolder {
        val view = LayoutInflater.from(parent.context)
        binding = LayoutItemChangeBeanBinding.inflate(view, parent, false)
        return ChangeBeanViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return  list.size
    }

    override fun onBindViewHolder(holder: ChangeBeanViewHolder, position: Int) {
        holder.itemView.apply {
            run {
                binding.titleChangeBean.text = list[position].title
                binding.amountBean.text = list[position].amountBean.toString()
            }

            holder.itemView.setOnClickListener {
                onClickItemBean.onClickItemBean(position)
            }
        }
    }
}