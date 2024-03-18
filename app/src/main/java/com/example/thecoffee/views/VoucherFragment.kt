package com.example.thecoffee.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.adapter.ChangeBeanRecyclerAdapter
import com.example.thecoffee.adapter.ChangeBeanRecyclerInterface
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerAdapter
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerInterface
import com.example.thecoffee.adapter.VoucherRecyclerAdapter
import com.example.thecoffee.adapter.VoucherRecyclerInterface
import com.example.thecoffee.data.models.Bean
import com.example.thecoffee.data.models.Voucher
import com.example.thecoffee.databinding.FragmentHomeBinding
import com.example.thecoffee.databinding.FragmentVoucherBinding

class VoucherFragment : Fragment() {
    private lateinit var binding: FragmentVoucherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val listVoucher = mutableListOf<Voucher>()
        listVoucher.add(Voucher("1", "29/2/2024", "1/3/2024", "Voucher", "Giam 1 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))
        listVoucher.add(Voucher("2", "29/2/2024", "1/3/2024", "Voucher", "Giam 2 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))
        listVoucher.add(Voucher("3", "29/2/2024", "1/3/2024", "Voucher", "Giam 3 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))
        listVoucher.add(Voucher("4", "29/2/2024", "1/3/2024", "Voucher", "Giam 4 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))
        listVoucher.add(Voucher("5", "29/2/2024", "1/3/2024", "Voucher", "Giam 5 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))
        listVoucher.add(Voucher("6", "29/2/2024", "1/3/2024", "Voucher", "Giam 6 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))
        listVoucher.add(Voucher("7", "29/2/2024", "1/3/2024", "Voucher", "Giam 7 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))
        listVoucher.add(Voucher("8", "29/2/2024", "1/3/2024", "Voucher", "Giam 8 35 % + FREESHIP Don Tu 10 Ly Toi Da 500K", 50000, 500000, "Don Tu 10 Ly"))

        val adapterVoucher = VoucherRecyclerAdapter(listVoucher, object: VoucherRecyclerInterface {
            override fun onClickItemVoucher(position: Int) {
                Toast.makeText(requireContext(), "Choose ${listVoucher[position].title}", Toast.LENGTH_LONG).show()
            }
        })
        binding.recyclerViewMyVoucher.adapter = adapterVoucher
        // 1 list
        binding.recyclerViewMyVoucher.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false)

        val listBean = mutableListOf<Bean>()
        listBean.add(Bean("1", "Mua 2 Tang 1 Tra Xanh Tay Bac", "Change Bean", 99, "29/2/2024", "5/3/2024", 4))
        listBean.add(Bean("2", "Mua 2 Tang 1 Tra Xanh Tay Bac", "Change Bean", 9, "29/2/2024", "5/3/2024", 4))
        listBean.add(Bean("3", "Mua 2 Tang 1 Tra Xanh Tay Bac", "Change Bean", 99, "29/2/2024", "5/3/2024", 4))
        listBean.add(Bean("4", "Mua 2 Tang 1 Tra Xanh Tay Bac", "Change Bean", 9, "29/2/2024", "5/3/2024", 4))

        val adapterBean = ChangeBeanRecyclerAdapter(listBean, object: ChangeBeanRecyclerInterface {
            override fun onClickItemBean(position: Int) {
                Toast.makeText(requireContext(), "Choose ${listBean[position].title}", Toast.LENGTH_LONG).show()
            }
        })
        binding.recyclerViewChangeBean.adapter = adapterBean
        // 1 list
        binding.recyclerViewChangeBean.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false)
    }

}