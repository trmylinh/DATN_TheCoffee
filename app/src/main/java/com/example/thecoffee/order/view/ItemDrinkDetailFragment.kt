package com.example.thecoffee.order.view

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.thecoffee.R
import com.example.thecoffee.order.adapter.ItemToppingRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemToppingRecyclerInterface
import com.example.thecoffee.databinding.FragmentItemDrinkDetailBinding
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ItemDrinkDetailFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentItemDrinkDetailBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var drinkDetail: Drink
    private var totalPrice = 0
    private var amount = 1
    private var listOption = mutableMapOf<String, Int>()


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
        drinkDetail = arguments?.getSerializable("dataDrink")!! as Drink
        binding = FragmentItemDrinkDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        productViewModel.getToppingList.observe(viewLifecycleOwner) {
            val adapterToppingView =
                ItemToppingRecyclerAdapter(it, object : ItemToppingRecyclerInterface {
                    override fun onTotalChanged(total: Int?) {
                        if (total != null) {
                            listOption["topping"] = total
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
        }

        getDataDetail()

        // check radio button duoc chon lan dau tien
        if (binding.radioGroup.checkedRadioButtonId != -1) {
            val checkedRadioButton: RadioButton =
                view.findViewById(binding.radioGroup.checkedRadioButtonId)
            if (checkedRadioButton.id == R.id.radio_regular) {
                listOption["size"] = drinkDetail.price!!
                updateTotalPriceText()
            }
        }

        // handle event change radio button
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            var priceSize: Int
            when (radio.id) {
                R.id.radio_big -> {
                    priceSize = drinkDetail.price!! + 10000
                    listOption["size"] = priceSize
                    updateTotalPriceText()
                }

                R.id.radio_regular -> {
                    priceSize = drinkDetail.price!!
                    listOption["size"] = priceSize
                    updateTotalPriceText()
                }

                R.id.radio_small -> {
                    priceSize = drinkDetail.price!! - 10000
                    listOption["size"] = priceSize
                    updateTotalPriceText()
                }
            }
        }

        // handle amount of item
        binding.viewPlus.setOnClickListener {
            amount++
            binding.amount.text = amount.toString()
            binding.totalPrice.text = "${String.format("%,d", totalPrice*amount)}đ"
        }

        binding.viewMinus.setOnClickListener{view ->
            if (amount > 1){
                amount--
                binding.amount.text = amount.toString()
                binding.totalPrice.text = "${String.format("%,d", totalPrice*amount)}đ"
            }
        }
    }

    private fun getDataDetail() {
        // receive the arguments in a variable

        Glide.with(requireActivity()).load(drinkDetail.image).into(binding.imageDrink)
        binding.nameDrink.text = drinkDetail.name
        if (drinkDetail.discount!! != 0) {
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

    private fun updateTotalPriceText() {
        if (listOption.isNotEmpty()) {
            totalPrice = 0
            for ((key, value) in listOption){
                totalPrice += value
                Log.e("value", (listOption to totalPrice).toString())
            }
            binding.totalPrice.text = "${String.format("%,d", totalPrice*amount)}đ"
        }
    }

}