package com.example.thecoffee.home.view

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.thecoffee.R
import com.example.thecoffee.home.adapter.ItemDrinkHomeRecyclerAdapter
import com.example.thecoffee.home.adapter.ItemDrinkHomeRecyclerInterface
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.databinding.FragmentHomeBinding
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.base.SharedNotificationBadgeViewModel
import com.example.thecoffee.base.SharedViewModel
import com.example.thecoffee.databinding.ActivityMainBinding
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.utils.DrinksByCategory
import com.example.thecoffee.order.view.BottomSheetListener
import com.example.thecoffee.order.view.ConfirmOrderBillFragment
import com.example.thecoffee.order.view.ConfirmOrderBillFragmentListener
import com.example.thecoffee.order.view.ItemDrinkDetailFragment
import com.example.thecoffee.order.view.OrderFragment
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel
import com.example.thecoffee.other.user.model.User
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.view.VoucherFragment
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bindingMain: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var productViewModel: ProductViewModel
    private var drinkList = mutableListOf<Drink>()
    private var drinkListFilter = mutableListOf<Drink>()

    private lateinit var adapterListDrink: ItemDrinkHomeRecyclerAdapter

    private val voucherFragment = VoucherFragment()
    private lateinit var voucherViewModel: VoucherViewModel

    private var voucherList = mutableListOf<Voucher>()

    //    private var orderFragment = OrderFragment()

    private  lateinit var listCartItem: MutableList<Cart>
    private val gson = Gson()
    private val bottomSheetDetail = ItemDrinkDetailFragment()
    private val bottomSheetConfirmBill = ConfirmOrderBillFragment()

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val sharedNotificationBadgeViewModel: SharedNotificationBadgeViewModel by activityViewModels()
    private var dataObserver: Observer<List<String>>? = null
    private var badgeObserver: Observer<Int>? = null

    private var listValue= mutableListOf<String>()

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel =
            ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
        voucherViewModel = ViewModelProvider(this, viewModelFactory)[VoucherViewModel::class.java]
        authenticationViewModel = ViewModelProvider(this, viewModelFactory)[AuthenticationViewModel::class.java]

        productViewModel.getDataDrinkList()
        //voucher
        voucherViewModel.getVoucherList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        bindingMain = ActivityMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val auth = Firebase.auth
        val user = auth.currentUser
        if (user != null) {
            authenticationViewModel.getUserDetail(user.uid)
            authenticationViewModel.getUserDetail.observe(viewLifecycleOwner){
                userInfo -> binding.textHelloUser.text = "Xin chào, ${userInfo.name}"
            }
        }

        // image slide show
        val imageList = ArrayList<SlideModel>()  // create image list
        imageList.add(SlideModel(R.drawable.slide1))
        imageList.add(SlideModel(R.drawable.slide2))
        imageList.add(SlideModel(R.drawable.slide3))
        imageList.add(SlideModel(R.drawable.slide4))
        imageList.add(SlideModel(R.drawable.slide5))

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)


        getVoucher()

        //detect scroll up/down
        binding.homeContent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (scrollY < oldScrollY) {
//                binding.textView.text = "Scroll up"
//            } else {
//
//            }
        }

        binding.btnNoti.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        // xem them - text underline
        binding.more.paintFlags = android.graphics.Paint.UNDERLINE_TEXT_FLAG

        binding.deliveryView.setOnClickListener {
            switchToTab(R.id.orderFragment)
        }

        binding.takeawayView.setOnClickListener {
            switchToTab(R.id.orderFragment)
        }

        binding.orderView.setOnClickListener {
            switchToTab(R.id.historyOrderFragment)
        }

        dataObserver = Observer { data ->
            listValue = data.toMutableList()
            if(listValue.isNotEmpty()){
                showCartView()
            }
        }

        badgeObserver = Observer {
            Log.d("badge", "badge: $it")
        }

        sharedViewModel.sharedData.observe(viewLifecycleOwner, dataObserver!!)
        sharedNotificationBadgeViewModel.notificationBadge.observe(viewLifecycleOwner, badgeObserver!!)



        // push notification
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("token", token)
        })

    }

    private fun switchToTab(tabId: Int) {
        navController.navigate(tabId)
    }

    private suspend fun getUidUser(): String =
        suspendCoroutine { continuation ->
            authenticationViewModel.getUidUser.observe(viewLifecycleOwner) {
                continuation.resume(it)
            }
        }

    private fun getVoucher() {
        voucherViewModel.getVoucherList.observe(viewLifecycleOwner) { vouchers ->
            voucherList = vouchers
            getDrinks()
        }
    }

    private fun getDrinks() {
        productViewModel.getDrinkList.observe(viewLifecycleOwner) {
            drinkList = it
            val voucherFilter =
                voucherList.filter { voucher -> voucher.type?.lowercase() == "drink" }
            val currentDate = voucherFragment.getDateOnly(Date())
            drinkList.forEach { drink ->
                if (drink.outOfStock == false) {
                    val voucherFound = voucherFilter.find { voucher ->
                        if (voucherFragment.stringToLocalDate(voucher.end_date!!) == currentDate) {
                            voucher.supportIdItems?.contains(drink.drinkId) == true
                        } else {
                            false
                        }
                    }

                    if (voucherFound?.unit == "%") {
                        drink.discount = voucherFound.discount!! * drink.price!! / 100
                    } else {
                        drink.discount = voucherFound?.discount
                    }

                    if (voucherFound != null) {
                        drinkListFilter.add(drink)
                    }
                }
            }

            if (drinkListFilter.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvCountDownTimer.visibility = View.GONE
                binding.tvVoucherTimer.visibility = View.GONE
                binding.recyclerViewItemDrinkHome.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.tvCountDownTimer.visibility = View.VISIBLE
                binding.tvVoucherTimer.visibility = View.VISIBLE
                binding.recyclerViewItemDrinkHome.visibility = View.VISIBLE

                var countDownTimer: CountDownTimer? = null

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 23) // Giờ kết thúc (tính từ 0 đến 23)
                calendar.set(Calendar.MINUTE, 59) // Phút kết thúc
                calendar.set(Calendar.SECOND, 59) // Giây kết thúc

                val endTime = calendar.timeInMillis // Thời gian kết thúc tính bằng milliseconds

                val currentTime =
                    System.currentTimeMillis() // Thời gian hiện tại tính bằng milliseconds

                val remainingTime =
                    endTime - currentTime // Thời gian còn lại tính bằng milliseconds

                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(remainingTime, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000
                        val hours = seconds / 3600
                        val minutes = (seconds % 3600) / 60
                        val remainingSeconds = seconds % 60

                        binding.tvCountDownTimer.text =
                            "${String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)}"
                    }

                    override fun onFinish() {
                        binding.tvCountDownTimer.visibility = View.GONE
                        binding.tvVoucherTimer.visibility = View.GONE
                    }
                }

                countDownTimer.start()


                adapterListDrink = ItemDrinkHomeRecyclerAdapter(
                    drinkListFilter,
                    object : ItemDrinkHomeRecyclerInterface {
                        override fun onClickItemDrink(position: Drink) {
//                        switchToTab(R.id.orderFragment)
                            // pass data -> item drink detail fragment
                            val bundleDetail = Bundle()
                            bundleDetail.putSerializable("dataDrink", position)
                            bottomSheetDetail.arguments = bundleDetail
                            bottomSheetDetail.listener = object : BottomSheetListener {
                                override fun onResult(value: String) {
//                                    showCartView(value)
//                                    productViewModel.setResultValue(value)
                                    sharedViewModel.addData(value)
                                }
                            }
                            bottomSheetDetail.show(parentFragmentManager, bottomSheetDetail.tag)
                            bottomSheetDetail.isCancelable = false
                        }
                    })
                binding.recyclerViewItemDrinkHome.adapter = adapterListDrink
                binding.recyclerViewItemDrinkHome.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }

        }
    }

    private fun showCartView() {
        listCartItem = mutableListOf()
        // luu cart vao sharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences(
            "cart",
            Context.MODE_PRIVATE
        )
        val isIdCartExist = sharedPreferences.contains("idCart")
        if (!isIdCartExist) {
            sharedPreferences.edit()
                .apply {
                    // tao idCart ~ idBill -> chi tao 1 lan
                    putString("idCart", "${UUID.randomUUID()}")
                }.apply()
        }

        for(value in listValue){
            sharedPreferences.edit()
                .apply {
                    putString("dataCart", value)
                }.apply()

            val json = sharedPreferences.getString("dataCart", null)
            val type = object : TypeToken<Cart>() {}.type

            // chuyen doi JSON -> cart object
            val itemCart: Cart = gson.fromJson(json, type)


            // case -> add trung spham
            var found = false
            for (item in listCartItem) {
                if (itemCart == item) {
                    found = true
                    break
                }
            }

            // xử lý tăng số lượng của spham trùng
            if (found) {
                val updateList = listCartItem.map { item ->
                    if (item == itemCart) {
                        item.copy(
                            totalPrice = item.totalPrice?.plus(itemCart.totalPrice!!),
                            quantity = item.quantity?.plus(itemCart.quantity!!)
                        )
                    } else {
                        item
                    }
                }
                listCartItem = updateList.toMutableList()
            } else {
                listCartItem.add(itemCart)
            }


            if (listCartItem.isNotEmpty()) {
                var total: Long = 0
                var countItem: Long = 0
                for (item in listCartItem) {
                    total += item.totalPrice!!
                    countItem += item.quantity!!
                }
                binding.viewCart.visibility = View.VISIBLE
                binding.amount.text = countItem.toString()
                binding.totalPrice.text = "${String.format("%,d", total)}đ"

                binding.viewCart.setOnClickListener {
                    // pass data -> confirm order bill fragment
                    val bundleBill = Bundle()
                    val jsonListCartItem = gson.toJson(listCartItem)
                    bundleBill.putString("dataBill", jsonListCartItem)
                    bottomSheetConfirmBill.arguments = bundleBill
                    bottomSheetConfirmBill.listener =
                        object : ConfirmOrderBillFragmentListener {
                            override fun onBottomSheetClear() {
                                binding.viewCart.visibility = View.GONE
                                listCartItem.removeAll(listCartItem)
//                                sharedViewModel.sharedData.value.orEmpty().toMutableList().clear()
                                listValue.clear()
                                sharedViewModel.clearData()
                            }

                            override fun onBottomSheetClose(newList: List<Cart>?, event: String?) {
                                if(event != null){
                                    sharedViewModel.clearData()
                                } else {
                                    if (newList != null) {
                                        listCartItem = newList.toMutableList()
                                        total = 0
                                        countItem = 0
                                        for (item in listCartItem) {
                                            total += item.totalPrice!!
                                            countItem += item.quantity!!
                                        }
                                        binding.viewCart.visibility = View.VISIBLE
                                        binding.amount.text = countItem.toString()
                                        binding.totalPrice.text = "${String.format("%,d", total)}đ"
                                    }
                                }

                            }
                        }
                    // hien thi confirm bill ui
                    bottomSheetConfirmBill.show(
                        parentFragmentManager,
                        bottomSheetConfirmBill.tag
                    )
                    bottomSheetConfirmBill.isCancelable = false
                }
            }
        }

        listValue.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hủy observer khi Fragment bị hủy View
        sharedViewModel.sharedData.removeObserver(dataObserver!!)
    }

    override fun onPause() {
        super.onPause()
        // Hủy observer khi Fragment vào trạng thái onPause
        sharedViewModel.sharedData.removeObserver(dataObserver!!)
    }

    override fun onResume() {
        super.onResume()
        // Khôi phục observer khi Fragment quay lại onResume
        sharedViewModel.sharedData.observe(viewLifecycleOwner, dataObserver!!)
    }


}