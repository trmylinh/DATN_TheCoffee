package com.example.thecoffee.admin.manage.drink.view

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
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ManageDrinkAdminFragment : Fragment() {
    private lateinit var binding: FragmentManagerDrinkAdminBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var productViewModel: ProductViewModel
    private var adapterListDrink: ItemDrinkCategoryRecyclerAdapter? = null
    private lateinit var adapterBottom: ItemCategoryRecyclerAdapter
    private var itemList = mutableListOf<Any>()
    private var categoryList = mutableListOf<Category>()
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

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // open bottom sheet category - menu danh muc spham
        productViewModel.loadingCategoryResult.observe(viewLifecycleOwner) {
            if (it) { // true show processing bar
                binding.loadingCategoryList.visibility = View.VISIBLE
            } else {
                binding.loadingCategoryList.visibility = View.GONE
                showMenuCategory()
            }
        }

        productViewModel.loadingDrinkResult.observe(viewLifecycleOwner) { loading ->
            if (loading) { // true show processing bar
                binding.loadingDrinkList.visibility = View.VISIBLE
            } else {
                binding.loadingDrinkList.visibility = View.GONE
                showListDrink()
            }
        }

        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val id = bundle.getString("id")
            val newList = adapterListDrink?.list?.find { item ->
                item is Drink && item.drinkId == id
            }
            (adapterListDrink?.list as MutableList).remove(newList)
            adapterListDrink?.notifyDataSetChanged()
        }

        binding.iconAdd.setOnClickListener {
            findNavController().navigate(R.id.action_managerDrinkAdminFragment_to_manageAddDrinkFragment)
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
                        val list = filterDrink(category.categoryId!!)
                        itemList.add(category.name!!)
                        itemList.addAll(list)
                    }
                }

                adapterBottom = ItemCategoryRecyclerAdapter(categoryItems,
                    object : ItemCategoryRecyclerInterface {
                        override fun onClickItemDrink(position: Category) {
                            dialogCategory.dismiss()
                            binding.statusName.text = position.name

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

        binding.layoutFilterStatus.setOnClickListener {
            recyclerViewCategory.layoutManager = GridLayoutManager(
                requireContext(), 4, LinearLayoutManager.VERTICAL, false
            )
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
                    if (firstVisibleItem is String && binding.statusName.text != firstVisibleItem) {
                        binding.statusName.text = firstVisibleItem
                    }
                }
            }
        })
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

    private fun showListDrink() {
        adapterListDrink =
            ItemDrinkCategoryRecyclerAdapter(
                itemList,
                object : ItemDrinkCategoryRecyclerInterface {
                    override fun onClickItemDrink(position: Drink) {
                        val action = ManageDrinkAdminFragmentDirections
                            .actionManagerDrinkAdminFragmentToManageDrinkDetailAdminFragment(position)
                        findNavController().navigate(action)
                    }
                }, marginBottom = 150, true
            )
        binding.rvDrinks.adapter = adapterListDrink
        linearLayoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvDrinks.layoutManager = linearLayoutManager
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

    override fun onDestroyView() {
        super.onDestroyView()
        drinkList.clear()
        categoryList.clear()
    }
}