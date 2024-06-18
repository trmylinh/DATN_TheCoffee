package com.example.thecoffee.admin.manage.order.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.order.adapter.ManageItemBillAdapter
import com.example.thecoffee.admin.manage.order.adapter.ManageItemBillAdapterInterface
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManageOrderAdminBinding
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.viewmodel.BillViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapadoo.alerter.Alerter
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
    private var selectedRadioButtonId: Int = -1


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

        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(resources.getColor(R.color.orange_700, null))
            isRefreshing = true
            getBills()
            setOnRefreshListener {
                binding.statusName.text = "Trạng thái"
                binding.statusName.setTextColor(resources.getColor(R.color.grey_700, null))
                binding.layoutFilterStatus.backgroundTintList = resources.getColorStateList(R.color.grey_300, null)

                (bills as MutableList).clear()
                billViewModel.getAllBills()
            }
        }

    }

    private fun displayBottomSheetDialogFilterStatus() {
        binding.layoutFilterStatus.setOnClickListener {
            val layoutBottomSheet = layoutInflater.inflate(R.layout.layout_bottom_sheet_dialog_filter_status, null)
            val dialog = BottomSheetDialog(requireActivity())
            dialog.setCancelable(true)
            dialog.setContentView(layoutBottomSheet)
            dialog.show()

            val radioGroup = layoutBottomSheet.findViewById<RadioGroup>(R.id.radioGroup)
            val btnFilter = layoutBottomSheet.findViewById<TextView>(R.id.btn_filter)
            val btnClear = layoutBottomSheet.findViewById<TextView>(R.id.clear_filter)

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                selectedRadioButtonId = checkedId
            }

            if (selectedRadioButtonId != -1) {
                radioGroup.check(selectedRadioButtonId)
            }

            btnFilter.setOnClickListener {
                val selectOption = radioGroup.checkedRadioButtonId
                val radioBtn = layoutBottomSheet.findViewById<RadioButton>(selectOption)
                binding.statusName.text = radioBtn.text
                binding.statusName.setTextColor(resources.getColor(R.color.green_900, null))
                binding.layoutFilterStatus.backgroundTintList = resources.getColorStateList(R.color.green_200, null)
                dialog.dismiss()
                dialog.setOnDismissListener {
                    selectedRadioButtonId = radioGroup.checkedRadioButtonId
                }

                var statusFilter: Long = -2L
                when(binding.statusName.text){
                    getString(R.string.status_pre_confirm) -> statusFilter = 0L
                    getString(R.string.status_confirm) -> statusFilter = 1L
                    getString(R.string.status_delivery) -> statusFilter = 2L
                    getString(R.string.status_done_delivery) -> statusFilter = 3L
                    getString(R.string.status_cancel) -> statusFilter = -1L
                    "Trạng thái" -> statusFilter = -2L
                }
                val filterBill = bills?.filter { it.status == statusFilter}
                if(statusFilter != -2L){
                    if (filterBill != null) {
                        if(filterBill.isNotEmpty()){
                            binding.swipeRefreshLayout.visibility = View.VISIBLE
                            binding.emptyView.visibility = View.GONE
                            showRecyclerView(filterBill)
                        } else {
                            binding.swipeRefreshLayout.visibility = View.GONE
                            binding.emptyView.visibility = View.VISIBLE
                        }
                    }
                }
            }

            btnClear.setOnClickListener {
                dialog.dismiss()
                radioGroup.clearCheck()
                binding.statusName.text = "Trạng thái"
                binding.statusName.setTextColor(resources.getColor(R.color.grey_700, null))
                binding.layoutFilterStatus.backgroundTintList = resources.getColorStateList(R.color.grey_300, null)

                binding.swipeRefreshLayout.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
                showRecyclerView(bills!!)
            }
        }
    }

    private fun getBills(){
        billViewModel.getBills.observe(viewLifecycleOwner){ items ->
            bills = items
            val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
            val sortedItems = bills?.sortedByDescending  { item -> dateFormat.parse(item.time!!) }
            binding.swipeRefreshLayout.isRefreshing = false
            if(sortedItems?.isNotEmpty() == true){
                binding.emptyView.visibility = View.GONE
                binding.swipeRefreshLayout.visibility = View.VISIBLE
                binding.layoutFilterStatus.visibility = View.VISIBLE

                //filter status bottom dialog
                displayBottomSheetDialogFilterStatus()

                // noi dung
                showRecyclerView(sortedItems)
            }
            else {
                binding.swipeRefreshLayout.visibility = View.GONE
                binding.layoutFilterStatus.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            }
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
                    override fun onBottomSheetClose(){
                        binding.swipeRefreshLayout.isRefreshing = true
                        billViewModel.getAllBills()
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