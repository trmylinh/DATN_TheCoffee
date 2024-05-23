package com.example.thecoffee.order.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerInterface
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.viewmodel.BillViewModel
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID
import kotlin.coroutines.suspendCoroutine

interface ConfirmOrderBillFragmentListener {
    fun onBottomSheetClear()
    fun onBottomSheetClose(newList: List<Cart>? = null)
}

class ConfirmOrderBillFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentConfirmOrderBillBinding
    private lateinit var billViewModel: BillViewModel
    private var dataBill = emptyList<Cart>()
    var listener: ConfirmOrderBillFragmentListener? = null
    private var priceItems: Long = 0
    private var countItem: Long = 0
    private lateinit var adapter: ItemChosenBillRecyclerAdapter

    private var auth = FirebaseAuth.getInstance()

    private var timeSelected: Int = 0
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        billViewModel = ViewModelProvider(this, viewModelFactory)[BillViewModel::class.java]

        // data order -> confirm
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
        val sharedPreferences = requireContext().getSharedPreferences(
            "cart",
            Context.MODE_PRIVATE
        )

        binding.closeBtn.setOnClickListener {
            listener?.onBottomSheetClose(dataBill)
            dismiss()
        }

        binding.clearBill.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setCancelable(false)
            builder.setTitle("Xác nhận")
            builder.setMessage("Xóa toàn bộ sản phẩm đã chọn khỏi đơn hàng này của bạn?")
                .setPositiveButton("Xóa") { _, _ ->

                    sharedPreferences.edit()
                        .apply {
                            clear()
                        }.apply()

                    (dataBill as MutableList).removeAll(dataBill)

                    dismiss()
                    listener?.onBottomSheetClear()

                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.cancel()
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        updateUIBill()

        // luu bill -> database
        binding.orderBtn.setOnClickListener {
            val idOrder = sharedPreferences.getString("idCart", null)
            timeSelected = 4
            if (timeSelected > timeProgress) {
                binding.viewBottom.visibility = View.GONE

                binding.viewCancel.visibility = View.VISIBLE
                binding.progressCountdown.progress = timeProgress
                timeCountDown = object :
                    CountDownTimer((timeSelected * 1000).toLong() - pauseOffSet * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        timeProgress++
                        pauseOffSet = timeSelected.toLong() - millisUntilFinished / 1000
                        binding.progressCountdown.progress = timeSelected - timeProgress
                        binding.textCountdown.text = "${timeSelected - timeProgress}"

                        binding.btnCancel.setOnClickListener {
                            if (timeCountDown != null) {
                                timeCountDown!!.cancel()
                                timeProgress = 0
                                timeSelected = 0
                                pauseOffSet = 0
                                timeCountDown = null
                                binding.progressCountdown.progress = 0
                                binding.textCountdown.text = "0"
                                binding.viewCancel.visibility = View.GONE

                                order(idOrder!!, -1)
                                sharedPreferences.edit()
                                    .apply {
                                        clear()
                                    }.apply()

                                listener?.onBottomSheetClear()
                            }
                        }
                    }

                    override fun onFinish() {
                        if (timeCountDown != null) {
                            timeCountDown!!.cancel()
                            timeProgress = 0
                            timeSelected = 0
                            pauseOffSet = 0
                            timeCountDown = null
                            binding.progressCountdown.progress = 0
                            binding.textCountdown.text = "0"
                            binding.viewCancel.visibility = View.GONE

                            order(idOrder!!, 0)
                            sharedPreferences.edit()
                                .apply {
                                    clear()
                                }.apply()

                            listener?.onBottomSheetClear()
                        }
                    }
                }.start()
            }

        }
    }

    private fun updateUIBill() {
        val sharedPreferences = requireContext().getSharedPreferences(
            "cart",
            Context.MODE_PRIVATE
        )

        priceItems = 0
        countItem = 0
        for (item in dataBill) {
            priceItems += item.totalPrice!!
            countItem += item.quantity!!
        }
        binding.itemsPrice.text = "${String.format("%,d", priceItems)}đ"

        // recycler view items chosen
        adapter = ItemChosenBillRecyclerAdapter(dataBill, object: ItemChosenBillRecyclerInterface{
            override fun onItemDeleteListener(position: Int) {
                (dataBill as MutableList).removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, adapter.itemCount)
//                sharedPreferences.edit().apply{
//                    putString("dataCart", dataBill.toString())
//                }.apply()
            }
        }, false)
        binding.rvItemChoosen.adapter = adapter
        binding.rvItemChoosen.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

        // giao hang
        val shipFee = 15000
        binding.totalAmountItems.text = "$countItem sản phẩm"
        binding.pricePayFinal.text = "${String.format("%,d", (priceItems+shipFee))}đ"
        binding.totalPay.text = "${String.format("%,d", (priceItems+shipFee))}đ"

    }

    private fun order(id: String, statusBill: Long) {
//        val id: String = generateRandomId()
        val userId: String = auth.currentUser?.uid!!
        val address: String = "165 Cau Giay"
        val status: Long = statusBill
        val shipFee: Long = 18000
        val time = SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Calendar.getInstance().time)

        billViewModel.order(Bill(id, userId, address, dataBill, status, shipFee, time))
        billViewModel.loadingResult.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE

                binding.saveInfo.visibility = View.GONE
                binding.viewBottom.visibility = View.GONE
                binding.clearBill.visibility = View.GONE
                binding.addMore.visibility = View.GONE
                binding.viewCancel.visibility = View.GONE

                binding.viewAfterOrder.visibility = View.VISIBLE
                binding.title.text = "Trạng thái đơn hàng"
                binding.billCode.text = id
                if(status == -1L){
                    binding.statusBill.text = getString(R.string.status_cancel)
                }
            }
        }

    }


}