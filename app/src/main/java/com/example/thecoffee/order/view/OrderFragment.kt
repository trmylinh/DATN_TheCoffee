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
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import com.example.thecoffee.order.adapter.ItemCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemCategoryRecyclerInterface
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerInterface
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.databinding.FragmentOrderBinding
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.utils.DrinksByCategory
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var productViewModel: ProductViewModel
    private lateinit var voucherViewModel: VoucherViewModel

    private var voucherList = mutableListOf<Voucher>()

    private var categoryList = listOf<Category>()
    private var drinkList = mutableListOf<Drink>()
    private var itemList = mutableListOf<DrinksByCategory>()
    private lateinit var adapterBottom: ItemCategoryRecyclerAdapter
    private lateinit var adapterListDrink: ItemDrinkCategoryRecyclerAdapter
    private val bottomSheetDetail = ItemDrinkDetailFragment()
    private val bottomSheetConfirmBill = ConfirmOrderBillFragment()
    private var listCartItem = mutableListOf<Cart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]

        voucherViewModel = ViewModelProvider(this, viewModelFactory)[VoucherViewModel::class.java]

        productViewModel.getDataDrinkList()
        productViewModel.getDataCategoryList()

        //voucher
        voucherViewModel.getVoucherList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(resources.getColor(R.color.orange_700, null))
            isRefreshing = true
            getCategory()
            setOnRefreshListener {
                drinkList.clear()
                (categoryList as MutableList).clear()
                itemList.clear()
                voucherList.clear()

                voucherViewModel.getVoucherList()
                productViewModel.getDataCategoryList()
                productViewModel.getDataDrinkList()
            }
        }

    }

    private fun getCategory() {
        productViewModel.getCategoryList.observe(viewLifecycleOwner) { categories ->
            Log.d("TAG", "categories: $categories")
            categoryList = categories
            if (categoryList.isNotEmpty()) {
                getVoucher()
//                getDrink()
                showMenuCategory()
            }
        }

    }

    private fun getVoucher() {
        voucherViewModel.getVoucherList.observe(viewLifecycleOwner) { vouchers ->
            voucherList = vouchers
            getDrink()
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

        adapterBottom = ItemCategoryRecyclerAdapter(requireContext(),
            object : ItemCategoryRecyclerInterface {
                override fun onClickItemCategory(category: Category) {
                    dialogCategory.dismiss()
                    binding.titleCategory.text = category.name

                    val positionCategoryName =
                        getPositionOfItem(DrinksByCategory.TypeCategory(category.name!!))
                    if (positionCategoryName != RecyclerView.NO_POSITION) {
                        linearLayoutManager.scrollToPositionWithOffset(
                            positionCategoryName, 0
                        )
                    }
                }
            })
        recyclerViewCategory.adapter = adapterBottom

        adapterBottom.submitList(categoryList)

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

    private fun getDrink() {
        productViewModel.getDrinkList.observe(viewLifecycleOwner) { drinks ->
            drinkList = drinks
            if (drinkList.isNotEmpty()) {
                binding.swipeRefreshLayout.isRefreshing = false
//                getVoucher()
                showListDrink()
            }
        }
    }

    private fun showListDrink() {
        var count = 0
        while(count < categoryList.size){
            val categoryName = DrinksByCategory.TypeCategory(categoryList[count].name!!)
            itemList.add(categoryName)
//            drinkList
//                .filter { item -> item.categoryId == categoryList[count].categoryId }
//                .forEach { itemList.add(DrinksByCategory.TypeDrink(it)) }
            val filteredDrinks =
                drinkList.filter { item -> item.categoryId == categoryList[count].categoryId }
            if (filteredDrinks.isEmpty()) {
                itemList.add(DrinksByCategory.TypeEmpty("Hiện tại chưa có sản phẩm nào"))
            } else {
                filteredDrinks.forEach { drink ->
                    val voucherFound = voucherList.find { voucher ->
                        if (voucher.type?.lowercase() == "category" && voucher.expired == false) {
                            voucher.supportIdItems?.contains(drink.categoryId) == true
                        } else if (voucher.type?.lowercase() == "drink" && voucher.expired == false) {
                            voucher.supportIdItems?.contains(drink.drinkId) == true
                        } else {
                           false
                        }
                    }

                    if(voucherFound?.unit == "%"){
                        drink.discount =  voucherFound.discount!! * drink.price!! / 100
                    } else {
                        drink.discount =  voucherFound?.discount
                    }

                    itemList.add(DrinksByCategory.TypeDrink(drink))
                }
            }

            count++
        }
        drinkList.clear()

        adapterListDrink =
            ItemDrinkCategoryRecyclerAdapter(
                requireContext(),
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
                                if (!isIdCartExist) {
                                    sharedPreferences.edit()
                                        .apply {
                                            // tao idCart ~ idBill -> chi tao 1 lan
                                            putString("idCart", generateRandomId())
                                        }.apply()
                                }

                                sharedPreferences.edit()
                                    .apply {
                                        putString("dataCart", value)
                                    }.apply()


                                // doc chuoi JSON tu sharedPreferences
                                val gson = Gson()
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
                                                }

                                                override fun onBottomSheetClose(newList: List<Cart>?) {
                                                    if(newList != null){
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
        adapterListDrink.submitList(itemList)

        linearLayoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvItemDrink.layoutManager = linearLayoutManager
    }

    private fun getPositionOfItem(item: DrinksByCategory.TypeCategory): Int {
        for (i in 0 until itemList.size) {
            val listItem = itemList[i]
            if (listItem is DrinksByCategory.TypeCategory && listItem == item) {
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
                    if (firstVisibleItem is DrinksByCategory.TypeCategory && binding.titleCategory.text != firstVisibleItem) {
                        binding.titleCategory.text = firstVisibleItem.categoryName
                    }
                }
            }
        })
    }

    private fun generateRandomId(): String {
        return "${UUID.randomUUID()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productViewModel.getCategoryList.removeObservers(viewLifecycleOwner)
        productViewModel.getDrinkList.removeObservers(viewLifecycleOwner)
    }


}