package com.example.thecoffee.admin.manage.voucher.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
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
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel

class ManageDetailVoucherAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageDetailVoucherAdminBinding
    private lateinit var adapterDrink: ItemDrinkCategoryRecyclerAdapter
    private lateinit var voucherViewModel: VoucherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        voucherViewModel = ViewModelProvider(this, viewModelFactory)[VoucherViewModel::class.java]
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
            val isUser = args.isUser
            val data: MutableList<DrinksByCategory> = mutableListOf()
            drinks.forEach {drink ->
                data.add(DrinksByCategory.TypeEmpty(drink))
            }
            if(isUser){
                binding.btnDelete.visibility = View.GONE
                binding.btnEditVoucher.visibility = View.GONE
            }
            binding.nameVoucher.text = result.name
            binding.voucherEndDate.text = result.end_date
            binding.voucherTimeline.text = "- Áp dụng từ ngày ${result.start_date} đến hết ${result.end_date}"
            binding.tvDiscountVoucher.text = "- Giảm giá: ${result.discount} ${result.unit}/1 sản phẩm"

            adapterDrink = ItemDrinkCategoryRecyclerAdapter(requireContext(), null, null, null)
            binding.rvItemsVoucher.adapter = adapterDrink
            adapterDrink.submitList(data)
            binding.rvItemsVoucher.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


            //delete
            binding.btnDelete.setOnClickListener {
                voucherViewModel.deleteVoucher(result.voucherId!!)
                voucherViewModel.loadingDeleteVoucher.observe(viewLifecycleOwner){ loading->
                    if(!loading){
                        voucherViewModel.getMessageDeleteVoucher.observe(viewLifecycleOwner){message ->
                            setFragmentResult("refresh", bundleOf("deleteVoucher" to message))
                            findNavController().popBackStack()
                        }
                    }
                }
            }

            binding.btnEditVoucher.setOnClickListener {
                val action = ManageDetailVoucherAdminFragmentDirections.actionManageDetailVoucherAdminFragmentToManageUpdateVoucherFragment(result)
                val navOptions = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
                findNavController().navigate(action, navOptions)
            }
        }

    }

}