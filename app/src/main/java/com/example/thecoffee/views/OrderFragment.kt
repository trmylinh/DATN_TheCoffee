package com.example.thecoffee.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.adapter.ItemCategoryRecyclerAdapter
import com.example.thecoffee.adapter.ItemCategoryRecyclerInterface
import com.example.thecoffee.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.adapter.ItemDrinkCategoryRecyclerInterface
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.FragmentOrderBinding
import com.example.thecoffee.viewmodel.AuthenticationViewModel
import com.example.thecoffee.viewmodel.MyViewModelFactory
import com.example.thecoffee.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var productViewModel: ProductViewModel
    private var categoryList = listOf<Category>()
    private var drinkList = mutableListOf<Drink>()
    private lateinit var adapterBottom: ItemCategoryRecyclerAdapter
    private lateinit var adapterListDrink: ItemDrinkCategoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel =
            ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
        //
        productViewModel.getDataCategoryList()
        productViewModel.getDataDrinkList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // open bottom sheet category - menu danh muc spham
        showMenuCategory()
        // list item theo category
        productViewModel.loadingDrinkResult.observe(viewLifecycleOwner) {
            if (it) { // true show processing bar
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

                adapterBottom = ItemCategoryRecyclerAdapter(
                    categoryItems,
                    object : ItemCategoryRecyclerInterface {
                        override fun onClickItemDrink(position: Category) {
                            val list = filterDrink(position.id!!)
                            dialogCategory.dismiss()
                            binding.titleCategory.text = position.name

                            // update adapter drink list - filter theo category
                            adapterListDrink =
                                ItemDrinkCategoryRecyclerAdapter(
                                    list,
                                    object : ItemDrinkCategoryRecyclerInterface {
                                        override fun onClickItemDrink(position: Drink) {
                                            Log.e("drink", position.name.toString())
                                        }
                                    })

                            binding.rvItemDrink.adapter = adapterListDrink
                            linearLayoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            binding.rvItemDrink.layoutManager = linearLayoutManager
                        }
                    })
                recyclerViewCategory.adapter = adapterBottom
            }
        }

        binding.titleCategory.setOnClickListener {
            recyclerViewCategory.layoutManager = GridLayoutManager(
                requireContext(),
                4,
                LinearLayoutManager.VERTICAL,
                false
            )
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialogCategory.setCancelable(false)
            dialogCategory.setContentView(layoutBottomSheet)
            dialogCategory.show()
        }
    }

    fun filterDrink(categoryId: String): List<Drink> {
//        val completableFuture = CompletableFuture<List<Drink>>()
//        productViewModel.getDrinkList.observe(viewLifecycleOwner) {
//            val displayArr = it.filter { item ->
//                item.categoryId == categoryId
//            }
//            completableFuture.complete(displayArr)
//
//        }
//        return completableFuture.get()
        val displayArr = drinkList.filter { item ->
            item.categoryId == categoryId
        }
        return displayArr
    }

    private fun showListDrink() {
        productViewModel.getDrinkList.observe(viewLifecycleOwner) {
            drinkList = it
            adapterListDrink =
                ItemDrinkCategoryRecyclerAdapter(it, object : ItemDrinkCategoryRecyclerInterface {
                    override fun onClickItemDrink(position: Drink) {
                        Log.e("drink", position.name.toString())
                    }
                })
        }
        binding.rvItemDrink.adapter = adapterListDrink
        linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvItemDrink.layoutManager = linearLayoutManager
    }

}