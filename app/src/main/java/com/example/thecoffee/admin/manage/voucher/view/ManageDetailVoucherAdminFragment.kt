package com.example.thecoffee.admin.manage.voucher.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.drink.view.ManageDrinkDetailAdminFragmentArgs
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManageDetailVoucherAdminBinding
import com.example.thecoffee.databinding.FragmentManageVoucherAdminBinding
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.utils.DrinksByCategory
import com.example.thecoffee.order.viewmodel.ProductViewModel

class ManageDetailVoucherAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageDetailVoucherAdminBinding
    private lateinit var adapterDrink: ItemDrinkCategoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageDetailVoucherAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            setFragmentResult("refresh", bundleOf("isRefreshing" to true))
            findNavController().popBackStack()
        }

        arguments?.let {
            val args = ManageDetailVoucherAdminFragmentArgs.fromBundle(it)
            val result = args.detailVoucher
            val drinks = args.detailVoucherDrinks
            val data: MutableList<DrinksByCategory> = mutableListOf()
            drinks.forEach {drink ->
                data.add(DrinksByCategory.TypeEmpty(drink))
            }

            binding.nameVoucher.text = result.name
            binding.voucherEndDate.text = result.end_date
            binding.voucherTimeline.text = "- Áp dụng từ ngày ${result.start_date} đến hết ${result.end_date}"

            adapterDrink = ItemDrinkCategoryRecyclerAdapter(requireContext(), null, null, null)
            binding.rvItemsVoucher.adapter = adapterDrink
            adapterDrink.submitList(data)
            binding.rvItemsVoucher.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

}