package com.example.thecoffee.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecoffee.R
import com.example.thecoffee.adapter.ItemCategoryRecyclerAdapter
import com.example.thecoffee.adapter.ItemCategoryRecyclerInterface
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerAdapter
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerInterface
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.FragmentHomeBinding
import com.example.thecoffee.databinding.FragmentOrderBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding

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

        val categoryList = mutableListOf<Category>()
        for (i in 1..12) {
            categoryList.add(Category(i.toString(), "Cà phê - CloudFee $i", R.drawable.img))
        }


        // open bottom sheet category - menu danh muc spham
        binding.titleCategory.setOnClickListener {
            val dialogCategory = BottomSheetDialog(requireActivity())

            val adapterBottom = ItemCategoryRecyclerAdapter(categoryList, object: ItemCategoryRecyclerInterface {
                override fun onClickItemDrink(position: Int) {
                    dialogCategory.dismiss()
                    binding.titleCategory.text = categoryList[position].name
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
                false)

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

            val adapterBottom = ItemCategoryRecyclerAdapter(categoryList, object: ItemCategoryRecyclerInterface {
                override fun onClickItemDrink(position: Int) {
                    dialogCategory.dismiss()
                    binding.titleCategory.text = categoryList[position].name
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
                false)

            btnClose.setOnClickListener {
                dialogCategory.dismiss()
            }

            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialogCategory.setCancelable(false)

            dialogCategory.setContentView(view)

            dialogCategory.show()

        }




    }


}