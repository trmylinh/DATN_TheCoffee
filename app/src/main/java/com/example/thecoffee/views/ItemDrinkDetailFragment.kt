package com.example.thecoffee.views

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.thecoffee.R
import com.example.thecoffee.adapter.ItemToppingRecyclerAdapter
import com.example.thecoffee.databinding.FragmentItemDrinkDetailBinding
import com.example.thecoffee.viewmodel.MyViewModelFactory
import com.example.thecoffee.viewmodel.ProductViewModel


class ItemDrinkDetailFragment : Fragment() {
   private lateinit var binding: FragmentItemDrinkDetailBinding
   private lateinit var productViewModel: ProductViewModel
   private val args: ItemDrinkDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel =
            ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]

        productViewModel.getDataToppingList()
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

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack();
        }

        productViewModel.getToppingList.observe(viewLifecycleOwner){
            val adapterToppingView = ItemToppingRecyclerAdapter(it)
            binding.recyclerViewItemTopping.adapter = adapterToppingView
            binding.recyclerViewItemTopping.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        getDataDetail()




    }

    private fun getDataDetail() {
        // receive the arguments in a variable
        val drinkDetail = args.drink

        Glide.with(requireActivity()).load(drinkDetail.image).into( binding.imageDrink)
        binding.nameDrink.text = drinkDetail.name
        if(drinkDetail.discount!! != 0){
            binding.priceDiscountDrink.visibility = View.VISIBLE
            binding.priceDefaultDrink.visibility = View.VISIBLE

            binding.priceDiscountDrink.text = "-${String.format("%,d", drinkDetail.discount)}đ"
            val priceAfterDiscount = drinkDetail.price!! - drinkDetail.discount!!
            binding.priceDrink.text = "${String.format("%,d", priceAfterDiscount)}đ"
            binding.priceDefaultDrink.text = "${String.format("%,d", drinkDetail.price)}đ"
            binding.priceDefaultDrink.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.priceDrink.text = "${String.format("%,d", drinkDetail.price)}đ"
        }

        // read more - text
        binding.descDrink.text = drinkDetail.desc
        binding.descDrink.setCollapsedText("Xem thêm")
        binding.descDrink.setExpandedText("Rút gọn")
        binding.descDrink.setCollapsedTextColor(R.color.orange_900)
        binding.descDrink.setExpandedTextColor(R.color.orange_900)
        binding.descDrink.setTrimLength(3)

        // radio - price size
        binding.priceSizeBig.text = "${String.format("%,d", drinkDetail.price!! + 10000)}đ"
        binding.priceSizeRegular.text = "${String.format("%,d", drinkDetail.price)}đ"
        binding.priceSizeSmall.text = "${String.format("%,d", drinkDetail.price!! - 10000)}đ"
    }

}