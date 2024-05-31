package com.example.thecoffee.voucher.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.voucher.adapter.ChangeBeanRecyclerAdapter
import com.example.thecoffee.voucher.adapter.ChangeBeanRecyclerInterface
import com.example.thecoffee.voucher.adapter.VoucherRecyclerAdapter
import com.example.thecoffee.voucher.adapter.VoucherRecyclerInterface
import com.example.thecoffee.voucher.model.Bean
import com.example.thecoffee.databinding.FragmentVoucherBinding
import com.example.thecoffee.voucher.model.Voucher
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VoucherFragment : Fragment() {
    private lateinit var binding: FragmentVoucherBinding
    private lateinit var voucherViewModel: VoucherViewModel

    private var voucherList = mutableListOf<Voucher>()

    private lateinit var voucherAdapter: VoucherRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        voucherViewModel = ViewModelProvider(this, viewModelFactory)[VoucherViewModel::class.java]

        voucherViewModel.getVoucherList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoucherBinding.inflate(inflater, container, false)
        return binding.root
        // Inflate the layout for this fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.apply {
            binding.swipeRefreshLayout.visibility = View.VISIBLE
            setColorSchemeColors(resources.getColor(R.color.orange_700, null))
            isRefreshing = true
            getVouchers()
            setOnRefreshListener {
                voucherList.clear()
                voucherViewModel.getVoucherList()
            }
        }

    }

    private fun getVouchers(){
        voucherViewModel.getVoucherList.observe(viewLifecycleOwner){
            voucherList = it
            binding.swipeRefreshLayout.isRefreshing = false
            if(voucherList.isNotEmpty()){
                binding.emptyView.visibility = View.GONE
                binding.swipeRefreshLayout.visibility = View.VISIBLE
                showVouchers()
            } else {
                binding.emptyView.visibility = View.VISIBLE
                binding.swipeRefreshLayout.visibility = View.GONE
            }
        }
    }

    private fun showVouchers(){
        val voucherFilter = voucherList.filter { it.expired == false }

        // chi show nhung voucher con han su dung cho user -> cho toi thoi diem hien tai
        val currentDate = getDateOnly(Date())
        val voucherAvailable = voucherList.filter { stringToLocalDate(it.end_date!!)!!.after(currentDate) || currentDate.compareTo(stringToLocalDate(it.end_date)) == 0 }

        voucherAdapter = VoucherRecyclerAdapter(requireContext(), object: VoucherRecyclerInterface{
            override fun onClickItemVoucher(voucher: Voucher) {

            }
        }, false)

        binding.recyclerViewMyVoucher.adapter = voucherAdapter

        voucherAdapter.submitList(voucherAvailable.sortedBy { stringToLocalDate(it.end_date!!) })
        
        binding.recyclerViewMyVoucher.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    // date - bao gom mui gio (theo miliseconds)
    // local date - khong bao gom mui gio (chi co ngay thang nam)

    fun stringToLocalDate(dateString: String): Date? {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try{
            sdf.parse(dateString)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    fun getDateOnly(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}