package com.example.thecoffee.admin.manage.order.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.databinding.FragmentManageDetailOrderBinding
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerAdapter
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.view.ConfirmOrderBillFragmentListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface ManageDetailOrderFragmentListener {
    fun onBottomSheetClose()
}
class ManageDetailOrderFragment  : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentManageDetailOrderBinding
    var listener: ManageDetailOrderFragmentListener? = null
    private lateinit var billDetail: Bill
    private lateinit var role: String

    companion object {
        private const val USER = "user"
        private const val ADMIN = "admin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        if(role == ADMIN){
            binding.itemsTitle.text = "${getString(R.string.items)} ($countItem)"

            binding.layoutBtnConfirmStatusBill.visibility = View.VISIBLE

            binding.layoutBtnConfirmStatusBill.setOnClickListener {
                if(binding.statusBill.text == getString(R.string.status_pre_confirm)){
                    binding.statusBill.text = getString(R.string.status_confirm)
                    binding.btnConfirmStatusBill.text = getString(R.string.btn_status_delivery)
                } else if(binding.statusBill.text == getString(R.string.status_confirm)){
                    binding.statusBill.text = getString(R.string.status_delivery)
                    binding.btnConfirmStatusBill.text = getString(R.string.btn_status_done_delivery)
                } else if (binding.statusBill.text == getString(R.string.status_delivery)) {
                    binding.statusBill.text = getString(R.string.status_done_delivery)
                    binding.layoutBtnConfirmStatusBill.visibility = View.GONE
                }
            }
        } else {
            binding.layoutBtnConfirmStatusBill.visibility = View.GONE
        }

    }

}