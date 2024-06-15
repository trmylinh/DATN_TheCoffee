package com.example.thecoffee.other.login.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thecoffee.MainActivity
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.voucher.view.ManageVoucherAdminFragmentDirections
import com.example.thecoffee.databinding.FragmentLoginBinding
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel
import com.example.thecoffee.base.MyViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loggedCheck: MutableLiveData<Boolean>
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private val bottomOtpMessageFragment = OtpMessageFragment()

    // create instance of firebase auth
    private lateinit var auth: FirebaseAuth

    // we will use this to match the sent otp from firebase
    private lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val USERID_ADMIN = "wFBO2VxN5lfkGBCndiH4rdT9wX33"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        authenticationViewModel = ViewModelProvider(this, viewModelFactory)[AuthenticationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        loggedCheck = authenticationViewModel.getLoggedStatus

        binding.edtPhoneNumber.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.layoutEdtPhone.setBackgroundResource(R.drawable.custom_focus_border)
            } else {
                binding.layoutEdtPhone.setBackgroundResource(R.drawable.custom_default_border)
            }
        }

        binding.edtPhoneNumber.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnLogIn.isEnabled = binding.edtPhoneNumber.text.toString().length == 10
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        initGoogleSignInClient()
        binding.btnLogInGG.setOnClickListener {
            signInUsingGoogle()
        }

        binding.edtPhoneNumber.setText("0123456789")

        binding.btnLogIn.setOnClickListener {
            val number = binding.edtPhoneNumber.text.toString()
            var phoneNumber = "+84" + "${number.substring(1)}"
            Log.d("TAG", "phoneNumber: $phoneNumber")

            val testPhoneNumber = phoneNumber
            val testVerificationCode = "160402"

            val bundleVerificationId = Bundle()
//            bundleVerificationId.putString("verificationId", storedVerificationId)
            bundleVerificationId.putString("testPhoneNumber", testPhoneNumber)
            bundleVerificationId.putString("testVerificationCode", testVerificationCode)
            bottomOtpMessageFragment.arguments = bundleVerificationId
            bottomOtpMessageFragment.show(
                parentFragmentManager,
                bottomOtpMessageFragment.tag
            )

            bottomOtpMessageFragment.isCancelable = false

        }

        binding.btnClose.setOnClickListener {
            // back lai other fragment
            findNavController().popBackStack();
        }
    }
    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.revokeAccess();
    }
    private fun signInUsingGoogle() {
        val signInGoogleIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInGoogleIntent, RC_SIGN_IN)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    getGoogleAuthCredential(account)
                }
            } catch (e : Exception){
                Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        val googleTokeId = account.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokeId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        loading(true)
        authenticationViewModel.signInWithGoogle(googleAuthCredential)
        authenticationViewModel.getUserData.observe(viewLifecycleOwner){ user ->
            authenticationViewModel.getIsNewUser.observe(viewLifecycleOwner){isNew->
                if(isNew){
                    val action = LoginFragmentDirections
                        .actionLoginFragmentToCreateUserInfoFragment(user)
                    val navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                    findNavController().navigate(action, navOptions)
                }else {
                    loggedCheck.observe(this){
                        loading(false)
                        val action = LoginFragmentDirections
                            .actionLoginFragmentToHomeFragment(user)
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        findNavController().navigate(action, navOptions)
                    }
                }
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnLogInGG.visibility = View.INVISIBLE
            binding.progressBarGG.visibility = View.VISIBLE
        } else {
            binding.btnLogInGG.visibility = View.VISIBLE
            binding.progressBarGG.visibility = View.INVISIBLE
        }
    }

}