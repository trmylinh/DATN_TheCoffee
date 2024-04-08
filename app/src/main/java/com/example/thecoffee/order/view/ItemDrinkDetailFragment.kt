package com.example.thecoffee.order.view

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.thecoffee.R
import com.example.thecoffee.order.adapter.ItemToppingRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemToppingRecyclerInterface
import com.example.thecoffee.databinding.FragmentItemDrinkDetailBinding
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.adapter.ItemSizeRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemSizeRecyclerInterface
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.viewmodel.CartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson


interface BottomSheetListener {
    fun onResult(value: String)
}

class ItemDrinkDetailFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentItemDrinkDetailBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var drinkDetail: Drink
    private var totalPrice: Long = 0
    private var amount = 1
    private var listOption = mutableMapOf<String, Long>()
    private var auth = FirebaseAuth.getInstance()
    private var listTopping = emptyList<String>()
    private var drinkSize: String = ""
    var listener: BottomSheetListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drinkDetail = arguments?.getSerializable("dataDrink")!! as Drink
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
            reset()
            dismiss()
        }

        getDataDetail()

        // handle amount of item
        binding.viewPlus.setOnClickListener {
            amount++
            binding.amount.text = amount.toString()
            binding.totalPrice.text = "${String.format("%,d", totalPrice*amount)}đ"
        }

        binding.viewMinus.setOnClickListener { view ->
            if (amount > 1) {
                amount--
                binding.amount.text = amount.toString()
                binding.totalPrice.text = "${String.format("%,d", totalPrice*amount)}đ"
            }
        }

        binding.viewAddBtn.setOnClickListener {
            if (auth.currentUser != null) {

                val cart = Cart((totalPrice * amount), amount, drinkDetail.name, drinkSize, listTopping,
                    note = if(binding.edtTextNote.text.isEmpty()) "" else binding.edtTextNote.text.toString())
                addToCartSharedPrefer(cart)
            } else {
                // handle login - here ---> navigation sang screen Login
                Toast.makeText(requireContext(), "Log In required", Toast.LENGTH_LONG).show()
            }
            reset()
            dismiss()
        }

    }


    private fun addToCartSharedPrefer(cart: Cart){
        val gson = Gson()
        val json = gson.toJson(cart)
        listener?.onResult(json)
    }


    private fun getDataDetail() {
        Log.e("detail", drinkDetail.toString())
        listOption["size"] = drinkDetail.price!!.toLong()
        updateTotalPriceText()

        Glide.with(requireActivity()).load(drinkDetail.image).into(binding.imageDrink)
        binding.nameDrink.text = drinkDetail.name

        // discount
        if (drinkDetail.discount!! > 0) {
            binding.viewDiscount.visibility = View.VISIBLE
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

        //size
        if(drinkDetail.size?.isNotEmpty() == true){
            binding.viewSize.visibility = View.VISIBLE
            drinkSize = drinkDetail.size?.get(1)?.name.toString()
            val adapterViewSize = ItemSizeRecyclerAdapter(drinkDetail.size!!, object : ItemSizeRecyclerInterface{
                override fun onRadioChanged(price: Long?, sizeName: String) {
                    listOption["size"] = price!!
                    drinkSize = sizeName
                    updateTotalPriceText()
                }
            })
            binding.recyclerViewItemSize.adapter = adapterViewSize
            binding.recyclerViewItemSize.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        } else {
            binding.viewSize.visibility = View.GONE
        }


        // topping
        if(drinkDetail.topping?.isNotEmpty() == true) {
            binding.viewTopping.visibility = View.VISIBLE
            val adapterToppingView =
                ItemToppingRecyclerAdapter(
                    drinkDetail.topping!!,
                    object : ItemToppingRecyclerInterface {
                        override fun onTotalChanged(total: Long?, list: List<String>) {
                            if (total != null) {
                                listOption["topping"] = total
                                listTopping = list
                                updateTotalPriceText()
                            }
                        }
                    })
            binding.recyclerViewItemTopping.adapter = adapterToppingView
            binding.recyclerViewItemTopping.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        } else {
            binding.viewTopping.visibility = View.GONE
        }

    }

    private fun updateTotalPriceText() {
        if (listOption.isNotEmpty()) {
            totalPrice = 0
            for ((key, value) in listOption) {
                totalPrice += value
            }
            binding.totalPrice.text = "${String.format("%,d", totalPrice * amount)}đ"
        }
    }

    private fun reset() {
        totalPrice = 0
        amount = 1
        drinkSize = ""
        listTopping = emptyList()
        listOption.clear()
    }


}