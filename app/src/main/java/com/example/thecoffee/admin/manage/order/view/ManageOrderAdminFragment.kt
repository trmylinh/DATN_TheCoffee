package com.example.thecoffee.admin.manage.order.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.order.adapter.ManageItemBillAdapter
import com.example.thecoffee.admin.manage.order.adapter.ManageItemBillAdapterInterface
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentHomeAdminBinding
import com.example.thecoffee.databinding.FragmentManageOrderAdminBinding
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.view.ConfirmOrderBillFragment
import com.example.thecoffee.order.view.ConfirmOrderBillFragmentListener
import com.example.thecoffee.order.viewmodel.BillViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ManageOrderAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageOrderAdminBinding
    private lateinit var billViewModel: BillViewModel
    private var bills: List<Bill>? = null
    private val bottomSheetManageDetailOrder = ManageDetailOrderFragment()
    private lateinit var adapter: ManageItemBillAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        billViewModel = ViewModelProvider(this, viewModelFactory)[BillViewModel::class.java]

        billViewModel.getAllBills()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageOrderAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // show UI
        billViewModel.loadingBillsResult.observe(viewLifecycleOwner){loading ->
            if(loading){
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                CoroutineScope(Dispatchers.Main).launch {
                    bills = getAllBills()
                    if(bills?.isNotEmpty() == true){
                        binding.emptyView.visibility = View.GONE
                        binding.rvBills.visibility = View.VISIBLE

                        showRecyclerView(bills!!)
//                        adapter = ManageItemBillAdapter(bills!!, object: ManageItemBillAdapterInterface{
//                            override fun onClickItem(position: Bill) {
//                                // pass data -> confirm order bill fragment
//                                val bundleBill = Bundle()
//                                bundleBill.putSerializable("billDetail", position)
//                                bundleBill.putString("role", "admin")
//                                bottomSheetManageDetailOrder.arguments = bundleBill
//                                bottomSheetManageDetailOrder.listener = object:
//                                    ManageDetailOrderFragmentListener {
//                                    override fun onBottomSheetClose(status: Long, idBill: String){
//                                        if(status != -1L){
//                                            val newData = bills?.toMutableList()
//                                            if (newData != null) {
//                                                for(item in newData){
//                                                    if(item.id == idBill){
//                                                        item.status = status
//                                                    }
//                                                }
//                                                showRecyclerView(newData)
//                                            }
//                                        }
//                                    }
//                                }
//
//                                // hien thi confirm bill ui
//                                bottomSheetManageDetailOrder.show(
//                                    parentFragmentManager,
//                                    bottomSheetManageDetailOrder.tag
//                                )
//                                bottomSheetManageDetailOrder.isCancelable = false
//                            }
//                        })
//                        binding.rvBills.adapter = adapter
//                        binding.rvBills.layoutManager = LinearLayoutManager(
//                            requireContext(), LinearLayoutManager.VERTICAL, false
//                        )
                    }
                    else {
                        binding.rvBills.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private suspend fun getAllBills(): List<Bill>? =
        suspendCoroutine { continuation ->
            billViewModel.getBills.observe(viewLifecycleOwner){
                val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                val sortedItems = it.sortedByDescending  { item -> dateFormat.parse(item.time!!) }
                continuation.resume(sortedItems)
            }
        }

    private fun showRecyclerView(data: List<Bill>){
        adapter = ManageItemBillAdapter(data, object: ManageItemBillAdapterInterface{
            override fun onClickItem(position: Bill) {
                // pass data -> confirm order bill fragment
                val bundleBill = Bundle()
                bundleBill.putSerializable("billDetail", position)
                bundleBill.putString("role", "admin")
                bottomSheetManageDetailOrder.arguments = bundleBill
                bottomSheetManageDetailOrder.listener = object:
                    ManageDetailOrderFragmentListener {
                    override fun onBottomSheetClose(status: Long, idBill: String){
                        if(status != -1L){
                            val newData = data.toMutableList()
                            for(item in newData){
                                if(item.id == idBill){
                                    item.status = status
                                }
                            }
                            showRecyclerView(newData)
                        }
                    }
                }

                // hien thi confirm bill ui
                bottomSheetManageDetailOrder.show(
                    parentFragmentManager,
                    bottomSheetManageDetailOrder.tag
                )
                bottomSheetManageDetailOrder.isCancelable = false
            }
        })
        binding.rvBills.adapter = adapter
        binding.rvBills.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
    }
}