package com.example.thecoffee.admin.manage.voucher.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.voucher.adapter.SupportItemsVoucherAdapter
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManageAddVoucherBinding
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ManageAddVoucherFragment : Fragment() {
    private lateinit var binding: FragmentManageAddVoucherBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var voucherViewModel: VoucherViewModel

    private lateinit var categories: List<Category>
    private lateinit var drinks: List<Drink>

    private lateinit var supportItemsAdapter: SupportItemsVoucherAdapter

    private val checkedStatesDrink = HashMap<Int, Boolean>()
    private val checkedStatesCategory = HashMap<Int, Boolean>()
    private var supportItems = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
        voucherViewModel = ViewModelProvider(this, viewModelFactory)[VoucherViewModel::class.java]

        productViewModel.getDataDrinkList()
        productViewModel.getDataCategoryList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageAddVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            setFragmentResult("refresh", bundleOf("isRefreshing" to true))
            findNavController().popBackStack()
        }

        CoroutineScope(Dispatchers.Main).launch {
            categories = initCategoryList()
            drinks = initDrinkList()

            //unit
            var unitVoucher: String? = null
            val units = resources.getStringArray(R.array.voucherUnit)
            val adapterUnit =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
            binding.spinnerUnit.adapter = adapterUnit
            binding.spinnerUnit.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        unitVoucher = units[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }

            //calendar
            binding.btnPickStartDate.setOnClickListener {
                val calendar = Calendar.getInstance();
                val datePicker = DatePickerDialog(requireContext())
                datePicker.setOnDateSetListener { view, year, month, dayOfMonth ->
                    val date = (dayOfMonth.toString() + "/"
                            + (month + 1) + "/" + year)
                    binding.tvStartDate.text = date
                    checkEnableBtn()
                }
                datePicker.datePicker.minDate = calendar.getTimeInMillis()
                datePicker.show()
            }

            binding.btnPickEndDate.setOnClickListener {
                val calendar = Calendar.getInstance();
                val datePicker = DatePickerDialog(requireContext())
                datePicker.setOnDateSetListener { view, year, month, dayOfMonth ->
                    val date = (dayOfMonth.toString() + "/"
                            + (month + 1) + "/" + year)
                    binding.tvEndDate.text = date
                    checkEnableBtn()
                }
                datePicker.datePicker.minDate = calendar.getTimeInMillis()
                datePicker.show()
            }

            //spham
            var type: String? = null
            binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    binding.typeDrink.id -> {
                        type = "Drink"
                        binding.viewPickDrink.visibility = View.VISIBLE
                        binding.viewPickCategory.visibility = View.GONE
                        supportItems.clear()
                        checkedStatesCategory.clear()

                        supportItemsAdapter = SupportItemsVoucherAdapter(
                            drinks,
                            checkedStatesDrink
                        ) { position, isChecked ->
                            if (isChecked) {
                                supportItems.add(drinks[position].drinkId!!)
                            } else {
                                supportItems.remove(drinks[position].drinkId!!)
                            }
                            Log.d("TAG", "supportItems: $supportItems")
                            checkEnableBtn()
                        }
                        binding.rvDrinkSpinner.adapter = supportItemsAdapter
                        binding.rvDrinkSpinner.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )

                        binding.edtSearchDrink.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                val filter = if (binding.edtSearchDrink.text.isNullOrEmpty()) {
                                    drinks
                                } else {
                                    drinks.filter {
                                        it.name!!.lowercase(Locale.getDefault()).contains(
                                            binding.edtSearchDrink.text.toString()
                                                .lowercase(Locale.getDefault())
                                        )
                                    }
                                }
                                supportItemsAdapter.updateData(filter)

                            }

                            override fun afterTextChanged(s: Editable?) {}
                        })

                        checkEnableBtn()
                    }

                    binding.typeCategory.id -> {
                        type = "Category"
                        binding.viewPickCategory.visibility = View.VISIBLE
                        binding.viewPickDrink.visibility = View.GONE
                        supportItems.clear()
                        checkedStatesDrink.clear()

                        supportItemsAdapter = SupportItemsVoucherAdapter(
                            categories,
                            checkedStatesCategory
                        ) { position, isChecked ->
                            if (isChecked) {
                                supportItems.add(categories[position].categoryId!!)
                            } else {
                                supportItems.remove(categories[position].categoryId!!)
                            }
                            Log.d("TAG", "supportItems: $supportItems")
                            checkEnableBtn()
                        }

                        binding.rvCategorySpinner.adapter = supportItemsAdapter
                        binding.rvCategorySpinner.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        binding.edtSearchCategory.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                val filter = if (binding.edtSearchCategory.text.isNullOrEmpty()) {
                                    categories
                                } else {
                                    categories.filter {
                                        it.name!!.lowercase(Locale.getDefault()).contains(
                                            binding.edtSearchCategory.text.toString()
                                                .lowercase(Locale.getDefault())
                                        )
                                    }
                                }
                                supportItemsAdapter.updateData(filter)
                            }

                            override fun afterTextChanged(s: Editable?) {}
                        })

                        checkEnableBtn()
                    }
                }
            }

            binding.edtNameVoucher.addTextChangedListener {
                checkEnableBtn()
            }

            binding.edtDiscountVoucher.addTextChangedListener {
                checkEnableBtn()
            }

            binding.btnAddVoucher.setOnClickListener {
                val name = binding.edtNameVoucher.text.toString()
                val discount = binding.edtDiscountVoucher.text.toString().toInt()
                val startDate = binding.tvStartDate.text.toString()
                val endDate = binding.tvEndDate.text.toString()
                val supportItems = supportItems

                val voucher = Voucher(
                    voucherId = "${UUID.randomUUID()}",
                    name = name,
                    discount = discount,
                    start_date = startDate,
                    end_date = endDate,
                    unit = unitVoucher,
                    supportIdItems = supportItems,
                    type = type
                )
                voucherViewModel.createVoucher(voucher)
                voucherViewModel.loadingCreateVoucher.observe(viewLifecycleOwner) {
                    if (it) {
                        binding.progressBarAddVoucher.visibility = View.VISIBLE
                    } else {
                        binding.progressBarAddVoucher.visibility = View.GONE
                        voucherViewModel.getMessageCreateVoucher.observe(viewLifecycleOwner) { message ->
                            setFragmentResult(
                                "refresh",
                                bundleOf("createVoucher_message" to message)
                            )
                            findNavController().popBackStack()
                        }
                    }
                }

            }
        }
    }

    private fun checkEnableBtn() {
        var emptyFieldCount = 0

        if(binding.edtNameVoucher.text.isEmpty()
            || binding.edtDiscountVoucher.text.isEmpty()
            || binding.tvStartDate.text.isEmpty()
            || binding.tvEndDate.text.isEmpty()
            || supportItems.isEmpty()){
            emptyFieldCount++
        }

        binding.btnAddVoucher.isEnabled = emptyFieldCount == 0
        binding.btnAddVoucher.backgroundTintList =
            if (emptyFieldCount == 0)
                requireContext().getColorStateList(R.color.light_blue_900)
            else requireContext().getColorStateList(R.color.grey_400)

    }

    private suspend fun initCategoryList(): List<Category> =
        suspendCoroutine { continuation ->
            productViewModel.getCategoryList.observe(viewLifecycleOwner) {
                continuation.resume(it)
            }
        }

    private suspend fun initDrinkList(): List<Drink> =
        suspendCoroutine { continuation ->
            productViewModel.getDrinkList.observe(viewLifecycleOwner) {
                continuation.resume(it)
            }
        }

}