package com.example.thecoffee.other.history.view

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
import com.example.thecoffee.admin.manage.order.view.ManageDetailOrderFragment
import com.example.thecoffee.admin.manage.order.view.ManageDetailOrderFragmentListener
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentHistoryOrderBinding
import com.example.thecoffee.databinding.FragmentManageOrderAdminBinding
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.viewmodel.BillViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HistoryOrderFragment : Fragment() {
    private lateinit var binding: FragmentHistoryOrderBinding
    private lateinit var billViewModel: BillViewModel
    private var bills: List<Bill>? = null
    private val bottomSheetManageDetailOrder = ManageDetailOrderFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        billViewModel = ViewModelProvider(this, viewModelFactory)[BillViewModel::class.java]

        billViewModel.getAllBillsUser()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        billViewModel.loadingBillsUserResult.observe(viewLifecycleOwner){loading ->
            if(loading){
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE

                CoroutineScope(Dispatchers.Main).launch {
                    bills = getAllBillsUser()
                    if(bills?.isNotEmpty() == true){
                        binding.emptyView.visibility = View.GONE
                        binding.rvBills.visibility = View.VISIBLE
                        val adapter = ManageItemBillAdapter(bills!!, object: ManageItemBillAdapterInterface {
                            override fun onClickItem(position: Bill) {
                                val bundleBill = Bundle()
                                bundleBill.putSerializable("billDetail", position)
                                bundleBill.putString("role", "user")
                                bottomSheetManageDetailOrder.arguments = bundleBill
                                bottomSheetManageDetailOrder.listener = object:
                                    ManageDetailOrderFragmentListener {
                                    override fun onBottomSheetClose(status: Long, idBill: String) {

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
                    } else {
                        binding.rvBills.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private suspend fun getAllBillsUser(): List<Bill>? =
        suspendCoroutine { continuation ->
            billViewModel.getBillsUser.observe(viewLifecycleOwner){
                if(it != null){
                    val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                    val sortedItems = it.sortedByDescending  { item -> dateFormat.parse(item.time!!) }
                    continuation.resume(sortedItems)
                }
            }
        }

}