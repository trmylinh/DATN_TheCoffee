package com.example.thecoffee.order.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerAdapter
import com.example.thecoffee.order.model.Cart
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface ConfirmOrderBillFragmentListener{
    fun onBottomSheetClear()
}

class ConfirmOrderBillFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentConfirmOrderBillBinding
    private var dataBill = emptyList<Cart>()
    var listener: ConfirmOrderBillFragmentListener? = null
    private var priceItems: Long = 0
    private lateinit var adapter: ItemChosenBillRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsonListCartItem = arguments?.getString("dataBill")
        val type = object : TypeToken<List<Cart>>() {}.type
        val gson = Gson()
        dataBill = gson.fromJson(jsonListCartItem, type)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmOrderBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        binding.clearBill.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences(
                "cart",
                Context.MODE_PRIVATE
            )
            val builder = AlertDialog.Builder(context)
            builder.setCancelable(false)
            builder.setTitle("Xác nhận")
            builder.setMessage("Xóa toàn bộ sản phẩm đã chọn khỏi đơn hàng này của bạn?")
                .setPositiveButton("Xóa") { dialog, id ->

                    sharedPreferences.edit()
                        .apply {
                            clear()
                        }.apply()

                    (dataBill as MutableList).removeAll(dataBill)

                    dismiss()
                    listener?.onBottomSheetClear()

                }
                .setNegativeButton("Hủy") { dialog, id ->
                    dialog.cancel()
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        updateUIBill()



    }

    private fun updateUIBill(){
        priceItems = 0
        for (item in dataBill) {
            priceItems += item.totalPrice!!
        }
        binding.itemsPrice.text = "${String.format("%,d", priceItems)}đ"

        // recycler view items chosen
        adapter = ItemChosenBillRecyclerAdapter(dataBill)
        binding.rvItemChoosen.adapter = adapter
        binding.rvItemChoosen.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
    }




}