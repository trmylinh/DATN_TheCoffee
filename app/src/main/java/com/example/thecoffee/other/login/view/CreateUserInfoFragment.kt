package com.example.thecoffee.other.login.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.drink.view.ManageDrinkDetailAdminFragmentArgs
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentCreateUserInfoBinding
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel

class CreateUserInfoFragment : Fragment() {
    private lateinit var binding: FragmentCreateUserInfoBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        authenticationViewModel = ViewModelProvider(this, viewModelFactory)[AuthenticationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let{
            val args = CreateUserInfoFragmentArgs.fromBundle(it)
            val userInfo = args.userInfo
            binding.edtTextFirstName.setText(userInfo.name ?: "")
            binding.edtTextEmail.setText(userInfo.email ?: "")
            binding.edtTextPhone.setText(userInfo.phone ?: "")

            if(userInfo.email != null){
                binding.edtTextEmail.isEnabled = false
                binding.edtTextEmail.backgroundTintList = resources.getColorStateList(R.color.grey_200, null)
                binding.edtTextEmail.setTextColor(resources.getColor(R.color.black_900, null))
            }

            if(userInfo.phone != null){
                binding.edtTextPhone.isEnabled = false
                binding.edtTextPhone.backgroundTintList = resources.getColorStateList(R.color.grey_200, null)
                binding.edtTextPhone.setTextColor(resources.getColor(R.color.black_900, null))
            }

            val isEnableBtn = {
                val isEmailValid = isEmailValid(binding.edtTextEmail.text.toString().trim())
                val isPhoneValid = isPhoneNumberValid(binding.edtTextPhone.text.toString().trim())
                val isNameValid = binding.edtTextFirstName.text.isNotEmpty()
                binding.btnDone.isEnabled = isEmailValid && isPhoneValid && isNameValid
            }

            val textWatcher = object: TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    isEnableBtn()
                }

            }

            binding.edtTextEmail.addTextChangedListener(textWatcher)
            binding.edtTextPhone.addTextChangedListener(textWatcher)
            binding.edtTextFirstName.addTextChangedListener(textWatcher)

            binding.btnDone.setOnClickListener {
//                binding.progressBarGG.visibility = View.VISIBLE
                if(userInfo.email == null){
                    authenticationViewModel.updateUserEmail(userInfo.uid!!, binding.edtTextEmail.text.toString())
                }
                if(userInfo.phone == null){
                    authenticationViewModel.updateUserPhone(userInfo.uid!!, binding.edtTextPhone.text.toString())
                }
                authenticationViewModel.updateUserName(userInfo.uid!!, binding.edtTextFirstName.text.toString())
                authenticationViewModel.getUserDetail(userInfo.uid)
                authenticationViewModel.getLoadingState.observe(viewLifecycleOwner){loading ->
                    if(loading){
                        binding.progressBarGG.visibility = View.VISIBLE
                    } else {
                        binding.progressBarGG.visibility = View.GONE
                        authenticationViewModel.getUserDetail.observe(viewLifecycleOwner){user ->
                            val action = CreateUserInfoFragmentDirections
                                .actionCreateUserInfoFragmentToHomeFragment(user)
                            val navOptions = NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .build()
                            findNavController().navigate(action, navOptions)
                        }
                    }

                }


            }
        }


    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})$"
        return email.matches(emailRegex.toRegex())
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.all { it.isDigit() } // Kiểm tra độ dài và chỉ chứa số
    }
}