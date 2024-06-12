package com.example.thecoffee.other.login.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentConfirmOrderBillBinding
import com.example.thecoffee.databinding.FragmentOtpMessageBinding
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpMessageFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOtpMessageBinding
    private lateinit var auth: FirebaseAuth
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
        binding = FragmentOtpMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val testVerificationCode = arguments?.getString("testVerificationCode")
        val testPhoneNumber = arguments?.getString("testPhoneNumber")
        auth = FirebaseAuth.getInstance()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.hintOtpMessage.text = (getString(R.string.hint_otp_message) to "0${testPhoneNumber?.substring(3)}").toString()
        binding.edtTextEmail.setText(testVerificationCode)
        binding.btnContinue.isEnabled = binding.edtTextEmail.text.trim().toString().isNotEmpty()

        binding.edtTextEmail.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnContinue.isEnabled = binding.edtTextEmail.text.trim().toString().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        // Đối với kiểm tra gửi mã:
        FirebaseAuth.getInstance().firebaseAuthSettings
            .setAutoRetrievedSmsCodeForPhoneNumber(testPhoneNumber, testVerificationCode)

        binding.btnContinue.setOnClickListener {
            val otp = binding.edtTextEmail.text.trim().toString()

//            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
//                storedVerificationId.toString(), otpTest)
//            val credential = PhoneAuthProvider.getCredential(verificationId, testVerificationCode)

            if (testPhoneNumber != null) {
                binding.progressBarGG.visibility = View.VISIBLE
                sendVerificationCode(testPhoneNumber)
            }
        }

    }

    private fun sendVerificationCode(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks) //OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Xác thực thành công mà không cần mã
            // Tự động nhập mã nếu cần
            Log.d("phone" , "onVerificationCompleted Success $credential")
//            storedVerificationId = verificationId
//            resendToken = token
            authenticationViewModel.signInWithPhone(credential)
            authenticationViewModel.getUserData.observe(viewLifecycleOwner){ user ->
                authenticationViewModel.getIsNewUser.observe(viewLifecycleOwner){isNew->
                    if(isNew){
                        val action = LoginFragmentDirections
                            .actionLoginFragmentToCreateUserInfoFragment(user)
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        findNavController().navigate(action, navOptions)
                        dismiss()
                    }else {
                            val action = LoginFragmentDirections
                                .actionLoginFragmentToHomeFragment(user)
                            val navOptions = NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .build()
                            findNavController().navigate(action, navOptions)
                        dismiss()
                    }
                    binding.progressBarGG.visibility = View.GONE
                }
            }

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // Xảy ra lỗi khi xác thực, thông báo cho người dùng
            Log.d("phone", "error: ${e.message}")
        }

//        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
//            // Mã đã được gửi, chuyển người dùng đến fragment hoặc screen nhập mã
////            val fragment = CodeInputFragment.newInstance(verificationId, token)
//            // Điều hướng người dùng sang fragment nhập mã với verificationId
//            Log.d("phone","onCodeSent: $verificationId")
//            storedVerificationId = verificationId
//            resendToken = token
//            val bundleVerificationId = Bundle()
//            bundleVerificationId.putString("verificationId", storedVerificationId)
//            bottomOtpMessageFragment.arguments = bundleVerificationId
//            bottomOtpMessageFragment.show(
//                parentFragmentManager,
//                bottomOtpMessageFragment.tag
//            )
//            bottomOtpMessageFragment.isCancelable = false
//        }

    }




}