package com.example.thecoffee.views

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.thecoffee.R
import com.example.thecoffee.databinding.FragmentOtherBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class OtherFragment : Fragment() {
    private lateinit var binding: FragmentOtherBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        val auth = Firebase.auth
        val user = auth.currentUser
        if (user != null) {
            // user -> sign out
            binding.layoutSignOut.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setCancelable(false)
                builder.setTitle("Xác nhận")
                builder.setMessage("Bạn có chắc chắn muốn đăng xuất tài khoản không?")
                    .setPositiveButton("Xác nhận") { dialog, id ->
                        auth.signOut().apply {
                            binding.iconSginOut.setImageResource(R.drawable.icon_sign_in)
                            binding.textSignOut.text = "Đăng nhập"
                            binding.layoutSignOut.setOnClickListener {
                                navController.navigate(R.id.action_otherFragment_to_loginFragment)
                            }
                        }
                    }
                    .setNegativeButton("Hủy bỏ") { dialog, id ->
                        dialog.cancel()
                    }
                val alertDialog = builder.create()
                alertDialog.show()
            }

            // user-info
            binding.layoutUserInfo.setOnClickListener {
                navController.navigate(R.id.action_otherFragment_to_userInfoFragment)
            }
        } else {
            // user chua login -> login
            binding.iconSginOut.setImageResource(R.drawable.icon_sign_in)
            binding.textSignOut.text = "Đăng nhập"
            binding.layoutSignOut.setOnClickListener {
                navController.navigate(R.id.action_otherFragment_to_loginFragment)
            }

            // user-info if user not log in
            binding.layoutUserInfo.setOnClickListener {
                navController.navigate(R.id.action_otherFragment_to_userInfoFragment)
            }
        }


    }

}