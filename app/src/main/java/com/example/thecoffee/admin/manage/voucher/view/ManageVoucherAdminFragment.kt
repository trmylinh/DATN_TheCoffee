package com.example.thecoffee.admin.manage.voucher.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentHomeAdminBinding
import com.example.thecoffee.databinding.FragmentManageVoucherAdminBinding
import com.example.thecoffee.voucher.adapter.VoucherRecyclerAdapter
import com.example.thecoffee.voucher.adapter.VoucherRecyclerInterface
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.viewmodel.VoucherViewModel
import java.util.Date

class ManageVoucherAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageVoucherAdminBinding

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
        binding = FragmentManageVoucherAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

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
//        val currentDate = getDateOnly(Date())
//        val voucherAvailable = voucherList.filter { stringToLocalDate(it.end_date!!)!!.after(currentDate) || stringToLocalDate(it.end_date) == currentDate }

        voucherAdapter = VoucherRecyclerAdapter(requireContext(), object: VoucherRecyclerInterface {
            override fun onClickItemVoucher(position: Int) {
            }
        }, true)

        binding.recyclerViewVoucher.adapter = voucherAdapter

        voucherAdapter.submitList(voucherList)

        binding.recyclerViewVoucher.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }


}