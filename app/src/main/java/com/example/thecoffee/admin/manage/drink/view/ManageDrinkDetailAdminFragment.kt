package com.example.thecoffee.admin.manage.drink.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.drink.adapter.ManageDrinkInfoAdapter
import com.example.thecoffee.databinding.FragmentManageDrinkDetailAdminBinding
import com.example.thecoffee.databinding.FragmentManagerDrinkAdminBinding

class ManageDrinkDetailAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageDrinkDetailAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageDrinkDetailAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val args = ManageDrinkDetailAdminFragmentArgs.fromBundle(it)
            val result = args.detailDrink
            Log.d("result", "$result")

            binding.nameProduct.text = result.name
            Glide.with(requireContext()).load(result.image).into(binding.imgProduct)
            binding.descProduct.text = result.desc

            binding.descProduct.text = result.desc
            binding.descProduct.setCollapsedText("Xem thêm")
            binding.descProduct.setExpandedText("Rút gọn")
            binding.descProduct.setCollapsedTextColor(R.color.orange_900)
            binding.descProduct.setExpandedTextColor(R.color.orange_900)
            binding.descProduct.setTrimLength(4)

            if(result.size?.isNotEmpty() == true){
                val apdaterSize = ManageDrinkInfoAdapter(result.size)
                binding.rvSize.adapter = apdaterSize
                binding.rvSize.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            } else {
                binding.layoutRvSize.visibility = View.GONE
            }

            if(result.topping?.isNotEmpty() == true){
                val apdaterTopping = ManageDrinkInfoAdapter(result.topping)
                binding.rvTopping.adapter = apdaterTopping
                binding.rvTopping.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            } else {
                binding.layoutRvTopping.visibility = View.GONE
            }
        }

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }
}