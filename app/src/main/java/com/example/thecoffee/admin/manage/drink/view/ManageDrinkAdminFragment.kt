package com.example.thecoffee.admin.manage.drink.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManagerDrinkAdminBinding
import com.example.thecoffee.order.adapter.ItemCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemCategoryRecyclerInterface
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerInterface
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.utils.DrinksByCategory
import com.example.thecoffee.order.view.BottomSheetListener
import com.example.thecoffee.order.view.ConfirmOrderBillFragmentListener
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ManageDrinkAdminFragment : Fragment() {
    private lateinit var binding: FragmentManagerDrinkAdminBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var productViewModel: ProductViewModel
    private lateinit var adapterListDrink: ItemDrinkCategoryRecyclerAdapter
    private lateinit var adapterBottom: ItemCategoryRecyclerAdapter
    private var itemList = mutableListOf<DrinksByCategory>()
    private var categoryList = listOf<Category>()
    private var drinkList = mutableListOf<Drink>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]

        productViewModel.getDataCategoryList()
        productViewModel.getDataDrinkList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManagerDrinkAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(resources.getColor(R.color.orange_700, null))
            isRefreshing = true
//            productViewModel.getDataCategoryList()
            getCategory()
            setOnRefreshListener {
                drinkList.clear()
                (categoryList as MutableList).clear()
                itemList.clear()
                productViewModel.getDataCategoryList()
                productViewModel.getDataDrinkList()
            }
        }

        // delete hoặc back lại từ màn trước
        setFragmentResultListener("refresh") { _, bundle ->
            val deleteMessage = bundle.getString("deleteDrink_message")
            val createMessage = bundle.getString("createDrink_message")
            if (deleteMessage != null) {
                showAlert(deleteMessage)
            }
            if(createMessage != null){
                showAlert(createMessage)
            }
            binding.swipeRefreshLayout.isRefreshing = true
            drinkList.clear()
            (categoryList as MutableList).clear()
            itemList.clear()
            productViewModel.getDataCategoryList()
            productViewModel.getDataDrinkList()
        }

        binding.iconAdd.setOnClickListener {
            findNavController().navigate(R.id.action_managerDrinkAdminFragment_to_manageAddDrinkFragment)
        }

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    //show alerter
    private fun showAlert(message: String) {
        Alerter.create(requireActivity())
//            .setTitle("Thông báo")
            .setText(message)
            .enableSwipeToDismiss()
            .setIcon(R.drawable.icon_bell_white)
            .setIconColorFilter(0) // optional - removes white tint
            .setBackgroundColorRes(R.color.black_900)
            .setDuration(3000)
            .show()
    }

    private fun getCategory() {
        productViewModel.getCategoryList.observe(viewLifecycleOwner) { categories ->
            categoryList = categories
            if (categoryList.isNotEmpty()) {
                getDrink()
                showMenuCategory()
            }
        }

    }

    private fun getDrink() {
        productViewModel.getDrinkList.observe(viewLifecycleOwner) { drinks ->
            drinkList = drinks
            if (drinkList.isNotEmpty()) {
                binding.swipeRefreshLayout.isRefreshing = false
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

        adapterBottom = ItemCategoryRecyclerAdapter(requireContext(),
            object : ItemCategoryRecyclerInterface {
                override fun onClickItemCategory(category: Category) {
                    dialogCategory.dismiss()
                    binding.statusName.text = category.name

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

        binding.statusName.setOnClickListener {
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

    private fun setScrollListener() {
        binding.rvDrinks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (visiblePosition != RecyclerView.NO_POSITION) {
                    val firstVisibleItem = itemList[visiblePosition]
                    if (firstVisibleItem is DrinksByCategory.TypeCategory && binding.statusName.text != firstVisibleItem) {
                        binding.statusName.text = firstVisibleItem.categoryName
                    }
                }
            }
        })
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
                        val action = ManageDrinkAdminFragmentDirections
                            .actionManagerDrinkAdminFragmentToManageDrinkDetailAdminFragment(
                                position
                            )
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true) // Launches Fragment as a single top if it's present in the back stack
                            .build()
                        findNavController().navigate(action, navOptions)
                    }
                }, marginBottom = 150, true
            )
        binding.rvDrinks.adapter = adapterListDrink
        adapterListDrink.submitList(itemList)

        linearLayoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvDrinks.layoutManager = linearLayoutManager
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        drinkList.clear()
//        (categoryList as MutableList).clear()
//    }
}