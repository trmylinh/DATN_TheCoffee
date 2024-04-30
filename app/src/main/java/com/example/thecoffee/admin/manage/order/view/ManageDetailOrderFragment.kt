package com.example.thecoffee.admin.manage.order.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.databinding.FragmentManageDetailOrderBinding
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerAdapter
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.view.ConfirmOrderBillFragmentListener
import com.example.thecoffee.order.viewmodel.BillViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

interface ManageDetailOrderFragmentListener {
    fun onBottomSheetClose(status: Long, idBill: String)
}

class ManageDetailOrderFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentManageDetailOrderBinding
    private lateinit var billViewModel: BillViewModel
    var listener: ManageDetailOrderFragmentListener? = null
    private lateinit var billDetail: Bill
    private lateinit var role: String
    private var statusUpdate: Long = -1L

    companion object {
        private const val USER = "user"
        private const val ADMIN = "admin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        billViewModel = ViewModelProvider(this, viewModelFactory)[BillViewModel::class.java]

        billDetail = arguments?.getSerializable("billDetail")!! as Bill
        role = arguments?.getString("role")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageDetailOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.closeBtn.setOnClickListener {
            dismiss()
            listener?.onBottomSheetClose(statusUpdate, billDetail.id!!)
            statusUpdate = -1L
        }

        binding.billCode.text = "${billDetail.id}"

        val adapter = ItemChosenBillRecyclerAdapter(billDetail.drinks!!)
        binding.rvItemChoosen.adapter = adapter
        binding.rvItemChoosen.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        var priceItems: Long = 0
        var countItem: Long = 0
        for (item in billDetail.drinks!!) {
            priceItems += item.totalPrice!!
            countItem += item.quantity!!
        }
        binding.itemsPrice.text = "${String.format("%,d", priceItems)}đ"
        binding.totalPay.text = "${String.format("%,d", priceItems)}đ"

        when(billDetail.status){
            -1L -> {
                // huy
                binding.layoutBtnConfirmStatusBill.visibility = View.GONE
                binding.statusBill.text = getString(R.string.status_cancel)
            }
            0L -> {
                // dang cho xac nhan - xac nhan
                binding.layoutBtnConfirmStatusBill.visibility = if(role == ADMIN) View.VISIBLE else View.GONE
                binding.statusBill.text = getString(R.string.status_pre_confirm)
                binding.btnConfirmStatusBill.text = getString(R.string.btn_status_confirm)
            }
            1L -> {
                // da xac nhan - giao hang
                binding.layoutBtnConfirmStatusBill.visibility = if(role == ADMIN) View.VISIBLE else View.GONE
                binding.statusBill.text = getString(R.string.status_confirm)
                binding.btnConfirmStatusBill.text = getString(R.string.btn_status_delivery)
            }
            2L -> {
                // dang giao hang - hoan thanh
                binding.layoutBtnConfirmStatusBill.visibility = if(role == ADMIN) View.VISIBLE else View.GONE
                binding.statusBill.text = getString(R.string.status_delivery)
                binding.btnConfirmStatusBill.text = getString(R.string.btn_status_done_delivery)
            }
            3L -> {
                // giao hang thanh cong
                binding.layoutBtnConfirmStatusBill.visibility = View.GONE
                binding.statusBill.text = getString(R.string.status_done_delivery)
            }
        }

        if (role == ADMIN) {
            binding.itemsTitle.text = "${getString(R.string.items)} ($countItem)"

            binding.layoutBtnConfirmStatusBill.setOnClickListener {
                when(billDetail.status){
                    0L -> {
                        // dang cho xac nhan -> da xac nhan - giao hang
                        billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.id!!, 1)
                        billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                            if (!loading) {
                                statusUpdate = 1L
                                binding.statusBill.visibility = View.VISIBLE
                                binding.statusBill.text = getString(R.string.status_confirm)
                                binding.btnConfirmStatusBill.text =
                                    getString(R.string.btn_status_delivery)
                                binding.progressBar.visibility = View.GONE
                            } else {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.statusBill.visibility = View.GONE
                            }
                        }
                    }
                    1L -> {
                        // da xac nhan -> dang giao hang - hoan thanh
                        billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.id!!, 2)
                        billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                            if (!loading) {
                                statusUpdate = 2L
                                binding.statusBill.visibility = View.VISIBLE
                                binding.statusBill.text = getString(R.string.status_delivery)
                                binding.btnConfirmStatusBill.text = getString(R.string.btn_status_done_delivery)
                                binding.progressBar.visibility = View.GONE
                            } else {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.statusBill.visibility = View.GONE
                            }
                        }
                    }
                    2L -> {
                        // dang giao hang -> giao hang thanh cong
                        billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.id!!, 3)
                        billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                            if (!loading) {
                                statusUpdate = 3L
                                binding.statusBill.visibility = View.VISIBLE
                                binding.statusBill.text = getString(R.string.status_done_delivery)
                                binding.layoutBtnConfirmStatusBill.visibility = View.GONE
                                binding.progressBar.visibility = View.GONE
                            } else {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.statusBill.visibility = View.GONE
                            }
                        }
                    }
                }
            }

        } else {
            binding.layoutBtnConfirmStatusBill.visibility = View.GONE
        }
    }

}