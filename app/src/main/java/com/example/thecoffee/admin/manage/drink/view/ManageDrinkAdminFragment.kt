package com.example.thecoffee.admin.manage.drink.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManagerDrinkAdminBinding
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerAdapter
import com.example.thecoffee.order.adapter.ItemDrinkCategoryRecyclerInterface
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.view.ItemDrinkDetailFragment
import com.example.thecoffee.order.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ManageDrinkAdminFragment : Fragment() {
    private lateinit var binding: FragmentManagerDrinkAdminBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var adapterListDrink: ItemDrinkCategoryRecyclerAdapter

    private var itemList = mutableListOf<Any>()
    private var categoryList = mutableListOf<Category>()
    private var drinkList = mutableListOf<Drink>()

    private val drinkDetailManageFragment = ManageDrinkDetailAdminFragment()

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
                binding.loadingDrinkList.visibility = View.VISIBLE
            } else {
                binding.loadingDrinkList.visibility = View.GONE
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
    }

    private fun showMenuCategory() {
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
            }
        }
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
        binding.rvDrinks.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
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