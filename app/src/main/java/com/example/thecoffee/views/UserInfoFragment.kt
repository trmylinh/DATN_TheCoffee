package com.example.thecoffee.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thecoffee.R
import com.example.thecoffee.databinding.FragmentUserInfoBinding

class UserInfoFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // check input text
        areAllInputFilled();
    }

    private fun areAllInputFilled() {
        // kiem tra tat ca cac input
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                // kiem tra tat cac cac o input
                val isFirstNameFilled = !binding.edtTextFirstName.toString().trim().isEmpty()
                val isLastNameFilled = !binding.edtTextLastName.toString().trim().isEmpty()
                val isEmailFilled = !binding.edtTextEmail.toString().trim().isEmpty()
                val isPhoneFilled = !binding.edtTextPhone.toString().trim().isEmpty()

                binding.btnCreateAccount.isEnabled = isFirstNameFilled
                        && isLastNameFilled
                        && isEmailFilled
                        && isPhoneFilled
            }
        }
        binding.edtTextFirstName.addTextChangedListener(textWatcher)
        binding.edtTextLastName.addTextChangedListener(textWatcher)
        binding.edtTextEmail.addTextChangedListener(textWatcher)
        binding.edtTextPhone.addTextChangedListener(textWatcher)
    }

}