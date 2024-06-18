package com.example.thecoffee.order.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.base.SharedViewModel
import com.example.thecoffee.databinding.FragmentFavoriteBinding
import com.example.thecoffee.databinding.FragmentManageDetailOrderBinding
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerInterface
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.utils.DrinksByCategory
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.view.VoucherFragment
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import java.util.Date

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var voucherViewModel: VoucherViewModel

    private var drinkList = ArrayList<Drink>()
    private var favoriteList = mutableListOf<DrinksByCategory.TypeDrink>()
    private var voucherList = mutableListOf<Voucher>()
    private val bottomSheetDetail = ItemDrinkDetailFragment()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val voucherFragment = VoucherFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
        voucherViewModel = ViewModelProvider(this, viewModelFactory)[VoucherViewModel::class.java]

        voucherViewModel.getVoucherList()
        productViewModel.getListFavorite()
        productViewModel.getDataDrinkList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        voucherViewModel.getVoucherList.observe(viewLifecycleOwner) { vouchers ->
            voucherList = vouchers
            productViewModel.getDrinkList.observe(viewLifecycleOwner){
                drinkList = it
                getListFavorite()
            }
        }
    }

    private fun getListFavorite(){
        productViewModel.getListFavorite.observe(viewLifecycleOwner){
            drinkList.forEach { drink ->
                if (it.contains(drink.drinkId)){
                    if(drink.outOfStock == false){
                        val currentDate = voucherFragment.getDateOnly(Date())
                        val voucherFound = voucherList.find { voucher ->
                            if (voucher.type?.lowercase() == "category" &&
                                (voucherFragment.stringToLocalDate(voucher.end_date!!)!!.after(currentDate)
                                        || voucherFragment.stringToLocalDate(voucher.end_date) == currentDate)) {
                                voucher.supportIdItems?.contains(drink.categoryId) == true
                            } else if (voucher.type?.lowercase() == "drink" &&
                                (voucherFragment.stringToLocalDate(voucher.end_date!!)!!.after(currentDate)
                                        || voucherFragment.stringToLocalDate(voucher.end_date) == currentDate)) {
                                voucher.supportIdItems?.contains(drink.drinkId) == true
                            } else {
                                false
                            }
                        }

                        if(voucherFound?.unit == "%"){
                            drink.discount =  voucherFound.discount!! * drink.price!! / 100
                        } else {
                            drink.discount =  voucherFound?.discount
                        }
                    }
                    favoriteList.add(DrinksByCategory.TypeDrink(drink))
                }
            }
            val adapter = ItemDrinkCategoryRecyclerAdapter(requireContext(), object: ItemDrinkCategoryRecyclerInterface{
                override fun onClickItemDrink(position: Drink) {
                    val bundleDetail = Bundle()
                    bundleDetail.putSerializable("dataDrink", position)
                    bottomSheetDetail.arguments = bundleDetail
                    bottomSheetDetail.listener = object : BottomSheetListener {
                        override fun onResult(value: String) {
                            sharedViewModel.addData(value)
                            findNavController().popBackStack()
                        }
                    }
                    bottomSheetDetail.show(parentFragmentManager, bottomSheetDetail.tag)
                    bottomSheetDetail.isCancelable = false
                }

            },
                marginBottom = 150,
                false)
            if (favoriteList.isNotEmpty()){
                binding.rvItem.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                binding
            } else {
                binding.rvItem.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }
            adapter.submitList(favoriteList as List<DrinksByCategory>?)
            binding.rvItem.adapter = adapter
            binding.rvItem.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

}