package com.example.thecoffee.order.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.order.adapter.ItemCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemCategoryRecyclerInterface
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerInterface
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.databinding.FragmentOrderBinding
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var productViewModel: ProductViewModel
    private var categoryList = listOf<Category>()
    private var drinkList = mutableListOf<Drink>()
    private var itemList = mutableListOf<Any>()
    private lateinit var adapterBottom: ItemCategoryRecyclerAdapter
    private lateinit var adapterListDrink: ItemDrinkCategoryRecyclerAdapter
    private val bottomSheetDetail = ItemDrinkDetailFragment()
    private val bottomSheetConfirmBill = ConfirmOrderBillFragment()
    private var listCartItem = mutableListOf<Cart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]

        productViewModel.getDataCategoryList()
        productViewModel.getDataDrinkList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // open bottom sheet category - menu danh muc spham
        productViewModel.loadingCategoryResult.observe(viewLifecycleOwner) {
            if (it) { // true show processing bar
                binding.loadingCategoryList.visibility = View.VISIBLE
            } else {
                binding.loadingCategoryList.visibility = View.GONE
                showMenuCategory()
            }
        }

        // list item theo category
        productViewModel.loadingDrinkResult.observe(viewLifecycleOwner) { loading ->
            if (loading) { // true show processing bar
                binding.loadingDrinkList.visibility = View.VISIBLE
            } else {
                binding.loadingDrinkList.visibility = View.GONE
                showListDrink()
            }
        }

    }

    private fun showMenuCategory() {
        val layoutBottomSheet = layoutInflater.inflate(R.layout.layout_bottom_sheet_category, null)
        val dialogCategory = BottomSheetDialog(requireActivity())
        val recyclerViewCategory =
            layoutBottomSheet.findViewById<RecyclerView>(R.id.recyclerView_category)
        val btnClose = layoutBottomSheet.findViewById<ImageView>(R.id.close_bottom_sheet_category)

        btnClose.setOnClickListener {
            dialogCategory.dismiss()
        }
        productViewModel.getCategoryList.observe(viewLifecycleOwner) { categoryItems ->
            if (categoryItems != null) {
                categoryList = categoryItems
                for (category in categoryItems) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val list = filterDrink(category.id!!)
                        itemList.add(category.name!!)
                        itemList.addAll(list)
                    }
                }

                adapterBottom = ItemCategoryRecyclerAdapter(categoryItems,
                    object : ItemCategoryRecyclerInterface {
                        override fun onClickItemDrink(position: Category) {
                            dialogCategory.dismiss()
                            binding.titleCategory.text = position.name

                            val positionCategoryName = getPositionOfItem(position.name!!)
                            if (positionCategoryName != RecyclerView.NO_POSITION) {
                                linearLayoutManager.scrollToPositionWithOffset(
                                    positionCategoryName, 0
                                )
                            }
                        }
                    })
                recyclerViewCategory.adapter = adapterBottom
            }
        }

        binding.titleCategory.setOnClickListener {
            recyclerViewCategory.layoutManager = GridLayoutManager(
                requireContext(), 4, LinearLayoutManager.VERTICAL, false
            )
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialogCategory.setCancelable(false)
            dialogCategory.setContentView(layoutBottomSheet)
            dialogCategory.show()
        }

        setScrollListener()

    }

    private suspend fun filterDrink(categoryId: String): List<Drink> =
        suspendCoroutine { continuation ->
            productViewModel.getDrinkList.observe(viewLifecycleOwner) {
                drinkList = it
                val displayArr = drinkList.filter { item ->
                    item.categoryId == categoryId
                }
                continuation.resume(displayArr)
            }
        }


    private fun showListDrink() {
        adapterListDrink =
            ItemDrinkCategoryRecyclerAdapter(
                itemList,
                object : ItemDrinkCategoryRecyclerInterface {
                    override fun onClickItemDrink(position: Drink) {
                        // pass data -> item drink detail fragment
                        val bundleDetail = Bundle()
                        bundleDetail.putSerializable("dataDrink", position)
                        bottomSheetDetail.arguments = bundleDetail
                        bottomSheetDetail.listener = object : BottomSheetListener {
                            override fun onResult(value: String) {
                                // luu cart vao sharedPreferences
                                val sharedPreferences = requireContext().getSharedPreferences(
                                    "cart",
                                    Context.MODE_PRIVATE
                                )
                                val isIdCartExist = sharedPreferences.contains("idCart")
                                if(!isIdCartExist){
                                    sharedPreferences.edit()
                                        .apply {
                                            // tao idCart ~ idBill -> chi tao 1 lan
                                            putString("idCart", generateRandomId())
                                        }.apply()
                                }

                                sharedPreferences.edit()
                                    .apply {
                                        putString("dataCart", value)

                                        // tao idCart ~ idBill -> chi tao 1 lan
//                                        putString("idCart", generateRandomId())
                                    }.apply()


                                // doc chuoi JSON tu sharedPreferences
                                val gson = Gson()
                                val json = sharedPreferences.getString("dataCart", null)
                                val type = object : TypeToken<Cart>() {}.type

                                // chuyen doi JSON -> cart object
                                val itemCart: Cart = gson.fromJson(json, type)

                                // case -> add trung spham
                                var found = false
                                for (item in listCartItem){
                                    if(itemCart == item){
                                        found = true
                                        break
                                    }
                                }

                                // xử lý tăng số lượng của spham trùng
                                if(found){
                                    val updateList = listCartItem.map { item ->
                                        if(item == itemCart){
                                            item.copy(
                                                totalPrice = item.totalPrice?.plus(itemCart.totalPrice!!),
                                                quantity = item.quantity?.plus(itemCart.quantity!!))
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
                                        bottomSheetConfirmBill.listener = object: ConfirmOrderBillFragmentListener{
                                            override fun onBottomSheetClear() {
                                                binding.viewCart.visibility = View.GONE
                                                listCartItem.removeAll(listCartItem)
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
                        }
                        bottomSheetDetail.show(parentFragmentManager, bottomSheetDetail.tag)
                        bottomSheetDetail.isCancelable = false
                    }

                }, marginBottom = 150, false
            )
        binding.rvItemDrink.adapter = adapterListDrink
        linearLayoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvItemDrink.layoutManager = linearLayoutManager
    }

    private fun getPositionOfItem(item: String): Int {
        for (i in 0 until itemList.size) {
            val listItem = itemList[i]
            if (listItem is String && listItem == item) {
                return i
            }
        }
        return RecyclerView.NO_POSITION
    }

    private fun setScrollListener() {
        binding.rvItemDrink.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (visiblePosition != RecyclerView.NO_POSITION) {
                    val firstVisibleItem = itemList[visiblePosition]
                    if (firstVisibleItem is String && binding.titleCategory.text != firstVisibleItem) {
                        binding.titleCategory.text = firstVisibleItem
                    }
                }
            }
        })
    }
    private fun generateRandomId(): String {
        return "${UUID.randomUUID()}"
    }


}