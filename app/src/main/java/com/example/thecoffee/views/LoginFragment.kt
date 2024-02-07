package com.example.thecoffee.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.example.thecoffee.MainActivity
import com.example.thecoffee.R
import com.example.thecoffee.data.models.ResponseState
import com.example.thecoffee.data.repositories.AuthenticationRepository
import com.example.thecoffee.databinding.FragmentLoginBinding
import com.example.thecoffee.viewmodel.LoginViewModel
import com.example.thecoffee.viewmodel.MyViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var loginViewModel: LoginViewModel
    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authRepository = AuthenticationRepository()
        val viewModelFactory = MyViewModelFactory(authRepository)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
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

        binding.edtPhoneNumber.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.layoutEdtPhone.setBackgroundResource(R.drawable.custom_focus_border)
            } else {
                binding.layoutEdtPhone.setBackgroundResource(R.drawable.custom_default_border)
            }
        }

        initGoogleSignInClient()

        binding.btnLogInGG.setOnClickListener {
            signInUsingGoogle()
        }

        binding.btnClose.setOnClickListener {
            // back lai HomeFragment
            findNavController().popBackStack();
        }
    }
    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
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
        loginViewModel.signInWithGoogle(googleAuthCredential)
        loginViewModel.authenticateUserLiveData.observe(viewLifecycleOwner) { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    authenticatedUser.message?.let {
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show()
                    }
                }
                is ResponseState.Success -> {
                    if (authenticatedUser.data != null) {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        Toast.makeText(context, "Authentication success", Toast.LENGTH_LONG).show()
                    }
                }
                is ResponseState.Loading -> {

                }
            }
        }
    }

}