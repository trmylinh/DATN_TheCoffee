package com.example.thecoffee.views

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.thecoffee.databinding.CustomDialogLoadingBinding
import com.example.thecoffee.databinding.FragmentUserInfoBinding
import com.example.thecoffee.viewmodel.AuthenticationViewModel
import com.example.thecoffee.viewmodel.MyViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UserInfoFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var loggedCheck: MutableLiveData<Boolean>
    private var imageUri: Uri? = null
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        authenticationViewModel =
            ViewModelProvider(this, viewModelFactory)[AuthenticationViewModel::class.java]
//        get data user
        firebaseUser = Firebase.auth.currentUser!!
        authenticationViewModel.getUserDetail(firebaseUser.uid)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fill data user from db
        authenticationViewModel.getUserDetail.observe(viewLifecycleOwner) {
            binding.edtTextFirstName.setText(it.name)
            binding.edtTextEmail.setText(it.email)
            binding.edtTextPhone.setText(it.phone)
            Glide.with(requireActivity()).load(it.avt).into(binding.imgAvt)
        }

        binding.edtTextFirstName.addTextChangedListener(editTextWatcher)
        binding.edtTextPhone.addTextChangedListener(editTextWatcher)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack();
        }

        binding.btnChangeAvt.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

        binding.btnCreateAccount.setOnClickListener {

        }



    }

    private val editTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val nameInput = binding.edtTextFirstName.text.toString().trim().isNotEmpty()
            val phoneInput = binding.edtTextPhone.text.toString().trim().isNotEmpty()
            binding.btnCreateAccount.isEnabled = nameInput && phoneInput
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                showDialog(true)
                imageUri = result.data!!.data
                authenticationViewModel.updateUserImage(imageUri!!)
                authenticationViewModel.checkUserImageUpdated.observe(viewLifecycleOwner){
                    if(it){
                        showDialog(false)
                    }
                    else {
                        showDialog(false)
                    }
                }

            }
        }
    }

    private fun showDialog(isShowDialog:Boolean) {
        if (isShowDialog) {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
            val binding = CustomDialogLoadingBinding.inflate(inflater)
            builder.setView(binding.root)
            loadingDialog = builder.create()
            loadingDialog?.show()
        } else {
            loadingDialog?.dismiss()
        }
    }



}