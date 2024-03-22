package com.example.thecoffee.voucher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.voucher.model.Bean
import com.example.thecoffee.databinding.LayoutItemChangeBeanBinding

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