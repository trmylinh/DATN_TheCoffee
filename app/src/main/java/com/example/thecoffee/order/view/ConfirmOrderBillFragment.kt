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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.order.view.ManageDetailOrderFragment
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.base.SharedViewModel
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerInterface
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.viewmodel.BillViewModel
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID
import kotlin.coroutines.suspendCoroutine

interface ConfirmOrderBillFragmentListener {
    fun onBottomSheetClear()
    fun onBottomSheetClose(newList: List<Cart>? = null, event: String? = null)
}

class ConfirmOrderBillFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentConfirmOrderBillBinding
    private lateinit var billViewModel: BillViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
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

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var event: String? = null

    private var client = OkHttpClient()

    companion object {
        private const val BARE_URL = "https://fcm.googleapis.com/fcm/send"
        private const val USER = "user"
        private const val ADMIN = "admin"

        // may ao
        private const val TOKEN_DEVICE_ADMIN = ""
        private const val SERVER_KEY = "" // paste server key zo dey
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        billViewModel = ViewModelProvider(this, viewModelFactory)[BillViewModel::class.java]
        authenticationViewModel = ViewModelProvider(this, viewModelFactory)[AuthenticationViewModel::class.java]

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

        val auth = Firebase.auth
        val user = auth.currentUser
        if (user != null) {
            authenticationViewModel.getUserDetail(user.uid)
            authenticationViewModel.getUserDetail.observe(viewLifecycleOwner){
                    userInfo ->
                        binding.edtReceiver.setText(userInfo.name)
                        binding.noteTimeDelivery.setText(userInfo.phone)
            }
        }

        val sharedPreferences = requireContext().getSharedPreferences(
            "cart",
            Context.MODE_PRIVATE
        )

        binding.closeBtn.setOnClickListener {
            listener?.onBottomSheetClose(dataBill, event)
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

                    sharedViewModel.clearData()

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
            timeSelected = 6
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
                                event = "clear"
                                sharedPreferences.edit()
                                    .apply {
                                        clear()
                                    }.apply()

                                sharedViewModel.clearData()

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
                            event = "clear"
                            sharedPreferences.edit()
                                .apply {
                                    clear()
                                }.apply()

                            sharedViewModel.clearData()

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

        calculatePrice()

        // recycler view items chosen
        adapter = ItemChosenBillRecyclerAdapter(dataBill, object: ItemChosenBillRecyclerInterface{
            override fun onItemDeleteListener(position: Int) {
                (dataBill as MutableList).removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, adapter.itemCount)

                calculatePrice()

                if(dataBill.isEmpty()){
                    sharedPreferences.edit()
                        .apply {
                            clear()
                        }.apply()

                    sharedViewModel.clearData()
                    dismiss()
                    listener?.onBottomSheetClear()
                }
            }
        }, false)
        binding.rvItemChoosen.adapter = adapter
        binding.rvItemChoosen.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

    }

    private fun order(id: String, statusBill: Long) {
        val userId: String = auth.currentUser?.uid!!
        val address: String = binding.addressMap.text.toString()
        val status: Long = statusBill
        val shipFee: Long = 18000
        val time = SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Calendar.getInstance().time)
        val userReceiver = binding.edtReceiver.text.toString()
        val phoneReceiver = binding.noteTimeDelivery.text.toString()

        billViewModel.order(Bill(id, userId, address, dataBill, status, userReceiver, phoneReceiver, shipFee, time))
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
                sendNotification(id)
            }
        }

    }

    private fun calculatePrice() {
        priceItems = 0
        countItem = 0
        for (item in dataBill) {
            priceItems += item.totalPrice!!
            countItem += item.quantity!!
        }
        binding.itemsPrice.text = "${String.format("%,d", priceItems)}đ"

        // giao hang
        val shipFee = 18000
        binding.totalAmountItems.text = "$countItem sản phẩm"
        binding.pricePayFinal.text = "${String.format("%,d", (priceItems+shipFee))}đ"
        binding.totalPay.text = "${String.format("%,d", (priceItems+shipFee))}đ"
        binding.priceShip.text = "${String.format("%,d", (shipFee))}đ"
    }

    private fun sendNotification(idBill: String) {
        val requestBody = """
{
    "data": {
        "body":"Đơn hàng mới: ${idBill}",
        "title":"Thông báo đơn hàng mới"
    },
    "to" : "$TOKEN_DEVICE_ADMIN"
}
""".toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(requestBody)
            .addHeader("Authorization", "key=$SERVER_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("error", "$e")
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseString = response.body?.string()
//                    sharedNotificationBadgeViewModel.incrementBadge()
                    Log.d("response", responseString ?: "Response body is null")
                }
            }
        })

//        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
//        try {
//            val json = JSONObject()
//            json.put("to", token)
//
//            val notification = JSONObject()
//            notification.put("title", title)
//            notification.put("body", message)
//
//            json.put("notification", notification)
//
//            val jsonObjectRequest = object: JsonObjectRequest(
//                Method.POST, BARE_URL, json,
//                com.android.volley.Response.Listener{ response ->
//                    Log.d("response", "$response")
//                },
//                com.android.volley.Response.ErrorListener{ error ->
//                    Log.d("response", "$error")
//                }
//            ){
//                override fun getHeaders(): MutableMap<String, String> {
//                    val headers = HashMap<String, String>()
//                    headers["Authorization"] = "key=${SERVER_KEY}"
//                    headers["Content-Type"] = "application/json"
//                    // Thêm các headers tùy chỉnh ở đây
//                    return headers
//                }
//            }
//
//            queue.add(jsonObjectRequest)
//        } catch (e: JSONException){
//            e.printStackTrace()
//        }


    }


}