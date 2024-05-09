package com.example.thecoffee.admin.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentHomeAdminBinding
import com.example.thecoffee.databinding.FragmentUserInfoBinding
import com.example.thecoffee.order.viewmodel.BillViewModel


class HomeAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.qlOrder.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_manageOrderAdminFragment)
        }

        binding.qlDrink.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_managerDrinkAdminFragment)
        }
    }


}