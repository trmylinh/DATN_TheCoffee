package com.example.thecoffee.other.user.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.thecoffee.databinding.CustomDialogLoadingBinding
import com.example.thecoffee.databinding.FragmentUserInfoBinding
import com.example.thecoffee.other.login.viewmodel.AuthenticationViewModel
import com.example.thecoffee.base.MyViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth


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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fill data user from db
        authenticationViewModel.getLoadingState.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingUserInfo.visibility = View.VISIBLE // Hiển thị ProgressBar
            } else {
                binding.loadingUserInfo.visibility = View.GONE // Ẩn ProgressBar
                getDatUser()
            }
        }

        binding.btnEditName.setOnClickListener {
            binding.edtTextFirstName.isFocusableInTouchMode = true
            binding.edtTextFirstName.isFocusable = true;
            binding.edtTextFirstName.isCursorVisible = true
            binding.edtTextFirstName.requestFocus()
            Log.e("press", "edit")
        }

        binding.btnEdtPhone.setOnClickListener {
            binding.edtTextPhone.isFocusableInTouchMode = true
            binding.edtTextPhone.isFocusable = true;
            binding.edtTextPhone.isCursorVisible = true
            binding.edtTextPhone.requestFocus()
            Log.e("press", "edit")
        }

        binding.edtTextFirstName.addTextChangedListener(editTextUserNameWatcher)
        binding.edtTextPhone.addTextChangedListener(editTextPhoneWatcher)

        binding.checkedtName.setOnClickListener {
            Log.e("update", "name")
            onChangeUserName()
        }

        binding.checkedtPhone.setOnClickListener {
            onChangeUserPhone()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack();
        }

        binding.btnChangeAvt.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

    }


    private fun getDatUser() {
        authenticationViewModel.getUserDetail.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.loadingUserInfo.visibility = View.GONE
                binding.edtTextFirstName.setText(it.name)
                binding.edtTextEmail.setText(it.email)
                binding.edtTextPhone.setText(it.phone)
                Glide.with(requireActivity()).load(it.avt).into(binding.imgAvt)
            }
        }

    }

    private val editTextUserNameWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val nameInput = binding.edtTextFirstName.text.toString().trim().isEmpty()
            if (nameInput) {
                binding.checkedtName.visibility = View.GONE
            } else {
                authenticationViewModel.getUserDetail.observe(viewLifecycleOwner) {
                    if (binding.edtTextFirstName.text.toString() != it.name) {
                        binding.checkedtName.visibility = View.VISIBLE
                        binding.btnEditName.visibility = View.GONE
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
    private val editTextPhoneWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val phoneInput = binding.edtTextPhone.text.toString().trim().isEmpty()
            if (phoneInput) {
                binding.checkedtPhone.visibility = View.GONE
            } else {
                authenticationViewModel.getUserDetail.observe(viewLifecycleOwner) {
                    if (binding.edtTextPhone.text.toString() != it.phone) {
                        binding.checkedtPhone.visibility = View.VISIBLE
                        binding.btnEdtPhone.visibility = View.GONE
                    } else {

                    }
                }
            }
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
                authenticationViewModel.checkUserImageUpdated.observe(viewLifecycleOwner) {
                    if (it) {
                        showDialog(false)
                    } else {
                        showDialog(false)
                    }
                }

            }
        }
    }

    private fun showDialog(isShowDialog: Boolean) {
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

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun onChangeUserName() {
        val txtUsername = binding.edtTextFirstName.text.toString()
        if (txtUsername.isNotEmpty()) {
            Log.e("check", firebaseUser.uid)
            authenticationViewModel.updateUserName(firebaseUser.uid, txtUsername)
            binding.edtTextFirstName.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
            authenticationViewModel.checkUserNameUpdated.observe(viewLifecycleOwner) {
                if (it) {
                    binding.checkedtName.visibility = View.GONE
                    binding.btnEditName.visibility = View.VISIBLE
                    binding.edtTextFirstName.isFocusableInTouchMode = false
                    binding.edtTextFirstName.isFocusable = false
                    binding.edtTextFirstName.isCursorVisible = false
                } else {
                    binding.checkedtName.visibility = View.GONE
                    binding.btnEditName.visibility = View.VISIBLE
                    binding.edtTextFirstName.isFocusableInTouchMode = false
                    binding.edtTextFirstName.isFocusable = false
                    binding.edtTextFirstName.isCursorVisible = false
                }
            }
        }
    }

    private fun onChangeUserPhone() {
        val txtPhone = binding.edtTextPhone.text.toString()
        if (txtPhone.isNotEmpty()) {
            authenticationViewModel.updateUserPhone(firebaseUser.uid, txtPhone)
            binding.edtTextPhone.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
            authenticationViewModel.checkUserPhoneUpdated.observe(viewLifecycleOwner) {
                if (it) {
                    binding.checkedtPhone.visibility = View.GONE
                    binding.btnEdtPhone.visibility = View.VISIBLE
                    binding.edtTextPhone.isFocusableInTouchMode = false
                    binding.edtTextPhone.isFocusable = false
                    binding.edtTextPhone.isCursorVisible = false
                } else {
                    binding.checkedtPhone.visibility = View.GONE
                    binding.btnEdtPhone.visibility = View.VISIBLE
                    binding.edtTextPhone.isFocusableInTouchMode = false
                    binding.edtTextPhone.isFocusable = false
                    binding.edtTextPhone.isCursorVisible = false
                }
            }
        }
    }


}