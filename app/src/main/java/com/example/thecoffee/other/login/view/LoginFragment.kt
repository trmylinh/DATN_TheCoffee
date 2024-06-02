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
import androidx.navigation.fragment.findNavController
import com.example.thecoffee.R
import com.example.thecoffee.databinding.FragmentLoginBinding
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel
import com.example.thecoffee.base.MyViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loggedCheck: MutableLiveData<Boolean>
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var authenticationViewModel: AuthenticationViewModel
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
        authenticationViewModel.getUserData.observe(viewLifecycleOwner){
                user ->
            loggedCheck.observe(this){
                loading(false)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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