package com.example.thecoffee.views

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.adapter.ItemCategoryRecyclerAdapter
import com.example.thecoffee.adapter.ItemCategoryRecyclerInterface
import com.example.thecoffee.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.adapter.ItemDrinkCategoryRecyclerInterface
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerAdapter
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerInterface
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.FragmentHomeBinding
import com.example.thecoffee.databinding.FragmentOrderBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var checkCallBack = false
    private var checkScroll = true
    private var totalCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // list item theo category
        val adapterItemDrinkCategory = ItemDrinkCategoryRecyclerAdapter(
            getListItemDrinkCategory(),
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
                getCategoryList(),
                object : ItemCategoryRecyclerInterface {
                    override fun onClickItemDrink(position: Int) {
                        dialogCategory.dismiss()
//                    binding.titleCategory.text = (getCategoryList()[position].name == "Category 1").toString()
                        binding.titleCategory.text = getCategoryList()[position].name
//                    binding.ttest.text = getCategoryList()[0].name
                        when(getCategoryList()[position].name){
                            getCategoryList()[0].name -> {
                                scrollToItem(0)
                            }
                            getCategoryList()[1].name -> {
                                scrollToItem(5)
                            }
                            getCategoryList()[2].name -> {
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
        binding.iconArrowDown.setOnClickListener {
            val dialogCategory = BottomSheetDialog(requireActivity())

            val adapterBottom = ItemCategoryRecyclerAdapter(
                getCategoryList(),
                object : ItemCategoryRecyclerInterface {
                    override fun onClickItemDrink(position: Int) {
                        dialogCategory.dismiss()
                        binding.titleCategory.text = getCategoryList()[position].name
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
        setScrollListener()


    }


    // choose category -> scroll to item
    private fun scrollToItem(index: Int) {
        linearLayoutManager.scrollToPositionWithOffset(index, 0)

    }

    // scroll view -> category change
    private fun setScrollListener(){
        checkScroll = true
        binding.rvItemDrink.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visiblePosition =  linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if(binding.titleCategory.text != getListItemDrinkCategory()[visiblePosition].type.name ){
                    binding.titleCategory.text = getListItemDrinkCategory()[visiblePosition].type.name
                }
            }
        })
    }

    private fun getCategoryList(): List<Category> {
        val categoryList = mutableListOf<Category>()
        for (i in 1..12) {
            categoryList.add(Category(i.toString(), "Category $i", R.drawable.img))
        }
        return categoryList
    }

    private fun getListItemDrinkCategory(): List<Drink> {
        val listItemDrinkCategory = mutableListOf<Drink>();
        for (i in 1..5) {
            listItemDrinkCategory.add(
                Drink(
                    i.toString(),
                    "Espresso Hot $i",
                    "Ngon",
                    R.drawable.img,
                    39000,
                    0,
                    getCategoryList()[0]
                )
            )
        }

        for (i in 1..6) {
            listItemDrinkCategory.add(
                Drink(
                    i.toString(),
                    "Frosty $i",
                    "Ngon",
                    R.drawable.voucher1,
                    39000,
                    0,
                    getCategoryList()[1]
                )
            )
        }

        for (i in 1..3) {
            listItemDrinkCategory.add(
                Drink(
                    i.toString(),
                    "Mochi $i",
                    "Ngon",
                    R.drawable.image_login_screen,
                    39000,
                    0,
                    getCategoryList()[2]
                )
            )
        }

        return listItemDrinkCategory
    }


}