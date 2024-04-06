package com.example.thecoffee.order.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thecoffee.R
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.databinding.FragmentItemDrinkDetailBinding


class ConfirmOrderBillFragment : Fragment() {
    private lateinit var binding: FragmentConfirmOrderBillBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmOrderBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}