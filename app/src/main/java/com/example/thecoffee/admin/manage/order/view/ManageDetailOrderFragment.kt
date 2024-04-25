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

interface ManageDetailOrderFragmentListener {
    fun onBottomSheetClose()
}

class ManageDetailOrderFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentManageDetailOrderBinding
    private lateinit var billViewModel: BillViewModel
    var listener: ManageDetailOrderFragmentListener? = null
    private lateinit var billDetail: Bill
    private lateinit var role: String

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
            listener?.onBottomSheetClose()
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

        binding.statusBill.text = when (billDetail.status) {
            -1L -> getString(R.string.status_cancel)
            0L -> getString(R.string.status_pre_confirm)
            1L -> getString(R.string.status_confirm)
            2L -> getString(R.string.delivery)
            3L -> getString(R.string.status_done_delivery)
            else -> "-----"
        }

        /* update color cho status text
        here
        */
        if (role == ADMIN) {
            binding.itemsTitle.text = "${getString(R.string.items)} ($countItem)"

            binding.layoutBtnConfirmStatusBill.visibility = View.VISIBLE

            binding.layoutBtnConfirmStatusBill.setOnClickListener {
                if (billDetail.status!! == 0L) {
                    billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.id!!, 1)
                    billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                        if (!loading) {
                            binding.statusBill.visibility = View.VISIBLE
                            binding.statusBill.text = getString(R.string.status_confirm)
                            binding.statusBill.setTextColor(resources.getColor(R.color.orange_900))
                            binding.btnConfirmStatusBill.text =
                                getString(R.string.btn_status_delivery)
                            binding.progressBar.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.statusBill.visibility = View.GONE
                        }
                    }
                } else if (billDetail.status!! == 1L) {
                    billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.id!!, 2)
                    billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                        if (!loading) {
                            binding.statusBill.visibility = View.VISIBLE
                            binding.statusBill.text = getString(R.string.status_delivery)
                            binding.statusBill.setTextColor(resources.getColor(R.color.green_900))
                            binding.btnConfirmStatusBill.text = getString(R.string.btn_status_done_delivery)
                            binding.progressBar.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.statusBill.visibility = View.GONE
                        }
                    }
                } else if (billDetail.status!! == 2L) {
                    billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.id!!, 3)
                    billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                        if (!loading) {
                            binding.statusBill.visibility = View.VISIBLE
                            binding.statusBill.text = getString(R.string.status_done_delivery)
                            binding.statusBill.setTextColor(resources.getColor(R.color.green_900))
                            binding.layoutBtnConfirmStatusBill.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.statusBill.visibility = View.GONE
                        }
                    }
                }
            }
        } else {
            binding.layoutBtnConfirmStatusBill.visibility = View.GONE
        }
    }

}