package com.example.thecoffee.admin.manage.order.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.databinding.FragmentManageDetailOrderBinding
import com.example.thecoffee.fcm.MyFirebaseMessagingService.Companion.FCM_TOKEN
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemChosenBillRecyclerInterface
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.view.ConfirmOrderBillFragmentListener
import com.example.thecoffee.order.viewmodel.BillViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.RemoteMessage
import com.tapadoo.alerter.Alerter
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

interface ManageDetailOrderFragmentListener {
    fun onBottomSheetClose()
}

class ManageDetailOrderFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentManageDetailOrderBinding
    private lateinit var billViewModel: BillViewModel
    var listener: ManageDetailOrderFragmentListener? = null
    private lateinit var billDetail: Bill
    private lateinit var role: String
    private var statusUpdate: Long = -1L
    private var client = OkHttpClient()

    companion object {
        private const val BARE_URL = "https://fcm.googleapis.com/fcm/send"
        private const val USER = "user"
        private const val ADMIN = "admin"
        private const val SERVER_KEY =
            ""

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
            statusUpdate = -1L
        }

        binding.billCode.text = "${billDetail.billId}"

        val adapter = ItemChosenBillRecyclerAdapter(billDetail.drinks!!, object: ItemChosenBillRecyclerInterface{
            override fun onItemDeleteListener(position: Int) {
            }
        }, true)
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

        when (billDetail.status) {
            -1L -> {
                // huy
                binding.layoutBtnConfirmStatusBill.visibility = View.GONE
                binding.statusBill.text = getString(R.string.status_cancel)
            }

            0L -> {
                // dang cho xac nhan - xac nhan
                binding.layoutBtnConfirmStatusBill.visibility =
                    if (role == ADMIN) View.VISIBLE else View.GONE
                binding.statusBill.text = getString(R.string.status_pre_confirm)
                binding.btnConfirmStatusBill.text = getString(R.string.btn_status_confirm)
            }

            1L -> {
                // da xac nhan - giao hang
                binding.layoutBtnConfirmStatusBill.visibility =
                    if (role == ADMIN) View.VISIBLE else View.GONE
                binding.statusBill.text = getString(R.string.status_confirm)
                binding.btnConfirmStatusBill.text = getString(R.string.btn_status_delivery)
            }

            2L -> {
                // dang giao hang - hoan thanh
                binding.layoutBtnConfirmStatusBill.visibility =
                    if (role == ADMIN) View.VISIBLE else View.GONE
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
                when (billDetail.status) {
                    0L -> {
                        // dang cho xac nhan -> da xac nhan - giao hang
                        billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.billId!!, 1)
                        billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                            if (!loading) {
                                statusUpdate = 1L
                                binding.statusBill.visibility = View.VISIBLE
                                binding.statusBill.text = getString(R.string.status_confirm)
                                binding.btnConfirmStatusBill.text =
                                    getString(R.string.btn_status_delivery)
                                binding.progressBar.visibility = View.GONE

                                billViewModel.getMessageUpdateBill.observe(viewLifecycleOwner){ message ->
                                    showAlert(message)
                                }
                            } else {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.statusBill.visibility = View.GONE
                            }
                        }
                    }

                    1L -> {
                        // da xac nhan -> dang giao hang - hoan thanh
                        billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.billId!!, 2)
                        billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                            if (!loading) {
                                statusUpdate = 2L
                                binding.statusBill.visibility = View.VISIBLE
                                binding.statusBill.text = getString(R.string.status_delivery)
                                binding.btnConfirmStatusBill.text =
                                    getString(R.string.btn_status_done_delivery)
                                binding.progressBar.visibility = View.GONE

                                billViewModel.getMessageUpdateBill.observe(viewLifecycleOwner){ message ->
                                    showAlert(message)
                                }
                            } else {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.statusBill.visibility = View.GONE
                            }
                        }
                    }

                    2L -> {
                        // dang giao hang -> giao hang thanh cong
                        billViewModel.updateStatusBillUser(billDetail.userId!!, billDetail.billId!!, 3)
                        billViewModel.loadingUpdateStatusBillResult.observe(viewLifecycleOwner) { loading ->
                            if (!loading) {
                                statusUpdate = 3L
                                binding.statusBill.visibility = View.VISIBLE
                                binding.statusBill.text = getString(R.string.status_done_delivery)
                                binding.layoutBtnConfirmStatusBill.visibility = View.GONE
                                binding.progressBar.visibility = View.GONE

                                billViewModel.getMessageUpdateBill.observe(viewLifecycleOwner){ message ->
                                    showAlert(message)
                                }
                            } else {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.statusBill.visibility = View.GONE
                            }
                        }
                    }
                }
            }

            binding.push.setOnClickListener {
                Log.d("push", "push")
                sendNotification("", "", "")
            }

        } else {
            binding.layoutBtnConfirmStatusBill.visibility = View.GONE
        }
    }

    private fun showAlert(message: String) {
        Alerter.create(requireActivity())
//            .setTitle("Thông báo")
            .setText(message)
            .enableSwipeToDismiss()
            .setIcon(R.drawable.icon_bell)
            .setIconColorFilter(0) // optional - removes white tint
            .setBackgroundColorRes(R.color.orange_700)
            .setDuration(3000)
            .show()
    }

    private fun sendNotification(token: String, title: String, message: String) {
//        val requestBody = """
//{
//    "data": {
//        "body":"This is an FCM notification message order status 5 update!",
//        "title":"FCM Message Order 20 Status"
//    },
//    "to" : "$FCM_TOKEN"
//}
//""".toRequestBody("application/json".toMediaTypeOrNull())
//
//        val request = Request.Builder()
//            .url("https://fcm.googleapis.com/fcm/send")
//            .post(requestBody)
//            .addHeader("Authorization", "key=$SERVER_KEY")
//            .addHeader("Content-Type", "application/json")
//            .build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.d("error", "$e")
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                    val responseString = response.body?.string()
//                    Log.d("response", responseString ?: "Response body is null")
//                }
//            }
//        })

        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
        try {
            val json: JSONObject = JSONObject()
            json.put("to", token)

            val notification: JSONObject = JSONObject()
            notification.put("title", title)
            notification.put("body", message)

            json.put("notification", notification)

            val jsonObjectRequest = object: JsonObjectRequest(
                Method.POST, BARE_URL, json,
                com.android.volley.Response.Listener{ response ->
                    Log.d("response", "$response")
                },
                com.android.volley.Response.ErrorListener{ error ->
                    Log.d("response", "$error")
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "key=${SERVER_KEY}"
                    headers["Content-Type"] = "application/json"
                    // Thêm các headers tùy chỉnh ở đây
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        } catch (e: JSONException){
            e.printStackTrace()
        }


    }

}