package com.example.thecoffee.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.thecoffee.R
import com.example.thecoffee.databinding.FragmentItemDrinkDetailBinding
import com.example.thecoffee.databinding.FragmentOrderBinding
import com.example.thecoffee.viewmodel.MyViewModelFactory
import com.example.thecoffee.viewmodel.ProductViewModel
import com.example.thecoffee.viewmodel.SharedViewModel


class ItemDrinkDetailFragment : Fragment() {
   private lateinit var binding: FragmentItemDrinkDetailBinding
   private lateinit var productViewModel: ProductViewModel
   private val args: ItemDrinkDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel =
            ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDrinkDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // receive the arguments in a variable
        val drinkDetail = args.drink
        binding.tv.text = drinkDetail.name
    }
}