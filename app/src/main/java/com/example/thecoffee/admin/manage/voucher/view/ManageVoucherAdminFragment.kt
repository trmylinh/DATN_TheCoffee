package com.example.thecoffee.admin.manage.voucher.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentHomeAdminBinding
import com.example.thecoffee.databinding.FragmentManageVoucherAdminBinding
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.example.thecoffee.voucher.adapter.VoucherRecyclerAdapter
import com.example.thecoffee.voucher.adapter.VoucherRecyclerInterface
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import com.tapadoo.alerter.Alerter
import java.util.Date

class ManageVoucherAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageVoucherAdminBinding

    private lateinit var voucherViewModel: VoucherViewModel
    private lateinit var productViewModel: ProductViewModel

    private var voucherList = mutableListOf<Voucher>()
    private var drinkList = mutableListOf<Drink>()

    private lateinit var voucherAdapter: VoucherRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        voucherViewModel = ViewModelProvider(this, viewModelFactory)[VoucherViewModel::class.java]
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]

        productViewModel.getDataDrinkList()
        voucherViewModel.getVoucherList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageVoucherAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.iconAdd.setOnClickListener {
            findNavController().navigate(R.id.action_manageVoucherAdminFragment_to_manageAddVoucherFragment)
        }

        binding.swipeRefreshLayout.apply {
            binding.swipeRefreshLayout.visibility = View.VISIBLE
            setColorSchemeColors(resources.getColor(R.color.orange_700, null))
            isRefreshing = true
            getVouchers()
            setOnRefreshListener {
                voucherList.clear()
                voucherViewModel.getVoucherList()
            }
        }

        setFragmentResultListener("refresh") { _, bundle ->
            val deleteVoucherMessage = bundle.getString("deleteVoucher")
            val createVoucherMessage = bundle.getString("createVoucher_message")
            val updateVoucherMessage = bundle.getString("updateVoucher_message")
            if(deleteVoucherMessage != null){
                showAlert(deleteVoucherMessage)
            }

            if(createVoucherMessage != null){
                Log.d("TAG", "createVoucherMessage: $createVoucherMessage")
                showAlert(createVoucherMessage)
            }

            if(updateVoucherMessage != null){
                showAlert(updateVoucherMessage)
            }

            binding.swipeRefreshLayout.isRefreshing = true
            voucherList.clear()
            voucherViewModel.getVoucherList()
        }
    }

    private fun getVouchers(){
        voucherViewModel.getVoucherList.observe(viewLifecycleOwner){
            voucherList = it
            getDrink()
//            binding.swipeRefreshLayout.isRefreshing = false
//            if(voucherList.isNotEmpty()){
//                binding.emptyView.visibility = View.GONE
//                binding.swipeRefreshLayout.visibility = View.VISIBLE
//                showVouchers()
//            } else {
//                binding.emptyView.visibility = View.VISIBLE
//                binding.swipeRefreshLayout.visibility = View.GONE
//            }
        }
    }

    private fun getDrink() {
        productViewModel.getDrinkList.observe(viewLifecycleOwner) { drinks ->
            drinkList = drinks
            if (drinkList.isNotEmpty()) {
                binding.swipeRefreshLayout.isRefreshing = false
                if(voucherList.isNotEmpty()){
                    binding.emptyView.visibility = View.GONE
                    binding.swipeRefreshLayout.visibility = View.VISIBLE
                    showVouchers()
                } else {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.visibility = View.GONE
                }
            }
        }
    }

    //show alerter
    private fun showAlert(message: String) {
        Alerter.create(requireActivity())
//            .setTitle("Thông báo")
            .setText(message)
            .enableSwipeToDismiss()
            .setIcon(R.drawable.icon_bell_white)
            .setBackgroundColorRes(R.color.light_blue_900)
            .setDuration(3000)
            .show()
    }

    private fun showVouchers(){
        voucherAdapter = VoucherRecyclerAdapter(requireContext(), object: VoucherRecyclerInterface {
            override fun onClickItemVoucher(voucher: Voucher) {
                val drinkFilterVoucher = mutableListOf<String>()
                if(voucher.type?.lowercase() == "drink"){
                    drinkList.forEach { drink ->
                        if(voucher.supportIdItems?.contains(drink.drinkId) == true){
                            drinkFilterVoucher.add(drink.name!!)
                        }
                    }
                } else if(voucher.type?.lowercase() == "category"){
                    drinkList.forEach { drink ->
                        if(voucher.supportIdItems?.contains(drink.categoryId) == true){
                            drinkFilterVoucher.add(drink.name!!)
                        }
                    }
                }
                val action = ManageVoucherAdminFragmentDirections
                    .actionManageVoucherAdminFragmentToManageDetailVoucherAdminFragment(
                    voucher,
                    drinkFilterVoucher.toTypedArray()
                )
                val navOptions = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
                findNavController().navigate(action, navOptions)
            }
        }, true)

        binding.recyclerViewVoucher.adapter = voucherAdapter

        voucherAdapter.submitList(voucherList)

        binding.recyclerViewVoucher.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }


}