package com.example.thecoffee.admin.manage.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.user.adapter.ManageItemUserAdapter
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManageStatisticAdminBinding
import com.example.thecoffee.databinding.FragmentManageUserBinding
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel

class ManageUserFragment : Fragment() {
    private lateinit var binding: FragmentManageUserBinding
    private lateinit var adapterUserManage: ManageItemUserAdapter
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        authenticationViewModel = ViewModelProvider(this, viewModelFactory)[AuthenticationViewModel::class.java]
        authenticationViewModel.getAllUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        authenticationViewModel.loadingAllUserResult.observe(viewLifecycleOwner){
            loading ->
            if (loading){
                binding.progressBar.visibility = View.VISIBLE

            } else{
                binding.progressBar.visibility = View.GONE
                showListUser()
            }
        }
    }

    private fun showListUser() {
        authenticationViewModel.getUserList.observe(viewLifecycleOwner){users ->
            adapterUserManage = ManageItemUserAdapter(users)
            binding.rvUser.adapter = adapterUserManage
            binding.rvUser.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
}