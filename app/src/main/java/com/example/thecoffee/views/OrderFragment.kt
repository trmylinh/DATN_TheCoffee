package com.example.thecoffee.views

import android.os.Bundle
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
import com.example.thecoffee.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var productViewModel: ProductViewModel
    private var checkCallBack = false
    private var checkScroll = true
    private var totalCount = 0
    private var categoryList = listOf<Category>()
    private var drinkList = listOf<Drink>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)


//        productViewModel.loadAllCategoryItems().observe(viewLifecycleOwner, Observer { categoryItems ->
//            for (item in categoryItems) {
//                Log.e("categoryItems", item.data?.get("name").toString())
//                categoryList.add(Category(item.data?.get("id").toString(), item.data?.get("name").toString(), R.drawable.img))
//            }
//        })
//
//        productViewModel.loadAllDrinkItems().observe(viewLifecycleOwner, Observer { drinkItems ->
//            for (item in drinkItems) {
//                Log.e("drinkItems", item.data?.get("name").toString())
//                drinkList.add(
//                    Drink(
//                        item.data?.get("id").toString(),
//                        item.data?.get("name").toString(),
//                        item.data?.get("desc").toString(),
//                        R.drawable.img,
//                        item.data?.get("price").toString().toInt(),
//                        item.data?.get("discount").toString().toInt(),
//                        item.data?.get("categoryId").toString())
//                )
//            }
//        })
        lifecycleScope.launch(Dispatchers.Main) {
//            val categoryItems = productViewModel.loadAllCategoryItems()
//            categoryItems.value?.let { categoryList = it }
//            Log.e("check", categoryList[0].name)

//            val drinkItems = productViewModel.loadAllDrinkItems()
//            drinkItems.value?.let {drinkList = it }
            val categories = async(Dispatchers.IO){
                productViewModel.fetchCategoryItemsFromFirestore()
            }
            val drinks = async(Dispatchers.IO) {
                productViewModel.fetchDrinkItemsFromFirestore()
            }

            categoryList = categories.await() as List<Category>
            drinkList = drinks.await() as List<Drink>

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // list item theo category
        val adapterItemDrinkCategory = ItemDrinkCategoryRecyclerAdapter(
            drinkList,
            object : ItemDrinkCategoryRecyclerInterface {
                override fun onClickItemDrink(position: Int) {
                    TODO("Not yet implemented")
                }

            });
        binding.rvItemDrink.adapter = adapterItemDrinkCategory
        linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvItemDrink.layoutManager = linearLayoutManager

        // open bottom sheet category - menu danh muc spham
        binding.titleCategory.setOnClickListener {
            val dialogCategory = BottomSheetDialog(requireActivity())

            val adapterBottom = ItemCategoryRecyclerAdapter(
                categoryList,
                object : ItemCategoryRecyclerInterface {
                    override fun onClickItemDrink(position: Int) {
                        dialogCategory.dismiss()
//                    binding.titleCategory.text = (getCategoryList()[position].name == "Category 1").toString()
                        binding.titleCategory.text = categoryList[position].name
//                    binding.ttest.text = getCategoryList()[0].name
                        when(categoryList[position].name){
                            categoryList[0].name -> {
                                scrollToItem(0)
                            }
                            categoryList[1].name -> {
                                scrollToItem(5)
                            }
                            categoryList[2].name -> {
                                scrollToItem(10)
                            }
                        }
                    }
                })

            val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_category, null)

            val btnClose = view.findViewById<ImageView>(R.id.close_bottom_sheet_category)

            val recyclerViewCategory = view.findViewById<RecyclerView>(R.id.recyclerView_category)
            recyclerViewCategory.adapter = adapterBottom

            recyclerViewCategory.layoutManager = GridLayoutManager(
                requireContext(),
                4,
                LinearLayoutManager.VERTICAL,
                false
            )

            btnClose.setOnClickListener {
                dialogCategory.dismiss()
            }

            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialogCategory.setCancelable(false)

            dialogCategory.setContentView(view)

            dialogCategory.show()

        }
//        binding.iconArrowDown.setOnClickListener {
//            val dialogCategory = BottomSheetDialog(requireActivity())
//
//            val adapterBottom = ItemCategoryRecyclerAdapter(
//                getCategoryList(),
//                object : ItemCategoryRecyclerInterface {
//                    override fun onClickItemDrink(position: Int) {
//                        dialogCategory.dismiss()
//                        binding.titleCategory.text = getCategoryList()[position].name
//                    }
//                })
//
//            val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_category, null)
//
//            val btnClose = view.findViewById<ImageView>(R.id.close_bottom_sheet_category)
//
//            val recyclerViewCategory = view.findViewById<RecyclerView>(R.id.recyclerView_category)
//            recyclerViewCategory.adapter = adapterBottom
//
//            recyclerViewCategory.layoutManager = GridLayoutManager(
//                requireContext(),
//                4,
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//
//            btnClose.setOnClickListener {
//                dialogCategory.dismiss()
//            }
//
//            // below line is use to set cancelable to avoid
//            // closing of dialog box when clicking on the screen.
//            dialogCategory.setCancelable(false)
//
//            dialogCategory.setContentView(view)
//
//            dialogCategory.show()
//
//        }
//        setScrollListener()



    }


    // choose category -> scroll to item
    private fun scrollToItem(index: Int) {
        linearLayoutManager.scrollToPositionWithOffset(index, 0)

    }

    // scroll view -> category change
//    private fun setScrollListener(){
//        checkScroll = true
//        binding.rvItemDrink.addOnScrollListener(object: RecyclerView.OnScrollListener(){
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                val visiblePosition =  linearLayoutManager.findFirstCompletelyVisibleItemPosition()
//                if(binding.titleCategory.text != getListItemDrinkCategory()[visiblePosition].type.name ){
//                    binding.titleCategory.text = getListItemDrinkCategory()[visiblePosition].type.name
//                }
//            }
//        })
//    }

//    private fun getCategoryList(callback: (List<Category>) -> Unit) {
//        val categoryList = mutableListOf<Category>()
//        productViewModel.loadAllCategoryItems().observe(viewLifecycleOwner, Observer { categoryItems ->
//            for (item in categoryItems) {
//                Log.e("id", item.data?.get("name").toString())
//                categoryList.add(Category(item.data?.get("id").toString(), item.data?.get("name").toString(), R.drawable.img))
//            }
//            callback(categoryList)
//        })
//    }

//    private fun getListItemDrinkCategory(): List<Drink> {
//        val listItemDrinkCategory = mutableListOf<Drink>();
//        for (i in 1..5) {
//            listItemDrinkCategory.add(
//                Drink(
//                    i.toString(),
//                    "Espresso Hot $i",
//                    "Ngon",
//                    R.drawable.img,
//                    39000,
//                    0,
//                    categoryList[0]
//                )
//            )
//        }
//
//        for (i in 1..6) {
//            listItemDrinkCategory.add(
//                Drink(
//                    i.toString(),
//                    "Frosty $i",
//                    "Ngon",
//                    R.drawable.voucher1,
//                    39000,
//                    0,
//                    categoryList[1]
//                )
//            )
//        }
//
//        for (i in 1..3) {
//            listItemDrinkCategory.add(
//                Drink(
//                    i.toString(),
//                    "Mochi $i",
//                    "Ngon",
//                    R.drawable.image_login_screen,
//                    39000,
//                    0,
//                    categoryList[2]
//                )
//            )
//        }
//
//        return listItemDrinkCategory
//    }


}