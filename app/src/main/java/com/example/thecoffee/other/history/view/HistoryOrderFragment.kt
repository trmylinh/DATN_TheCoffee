package com.example.thecoffee.other.history.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.order.adapter.ManageItemBillAdapter
import com.example.thecoffee.admin.manage.order.adapter.ManageItemBillAdapterInterface
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentHistoryOrderBinding
import com.example.thecoffee.databinding.FragmentManageOrderAdminBinding
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.viewmodel.BillViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HistoryOrderFragment : Fragment() {
    private lateinit var binding: FragmentHistoryOrderBinding
    private lateinit var billViewModel: BillViewModel
    private var bills = emptyList<Bill>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        billViewModel = ViewModelProvider(this, viewModelFactory)[BillViewModel::class.java]

        billViewModel.getAllBillsUser()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        billViewModel.loadingBillsUserResult.observe(viewLifecycleOwner){loading ->
            if(loading){
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE

                CoroutineScope(Dispatchers.Main).launch {
                    Log.e("bill", "${getAllBillsUser()}")
                    bills = getAllBillsUser()
                    val adapter = ManageItemBillAdapter(bills, object: ManageItemBillAdapterInterface {
                        override fun onClickItem(position: Bill) {
                            Log.e("click", position.id.toString())
                        }
                    })
                    binding.rvBills.adapter = adapter
                    binding.rvBills.layoutManager = LinearLayoutManager(
                        requireContext(), LinearLayoutManager.VERTICAL, false
                    )
                }
            }
        }
    }

    private suspend fun getAllBillsUser(): List<Bill> =
        suspendCoroutine { continuation ->
            billViewModel.getBillsUser.observe(viewLifecycleOwner){
                val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                val sortedItems = it.sortedByDescending  { item -> dateFormat.parse(item.time!!) }
                continuation.resume(sortedItems)
            }
        }

}