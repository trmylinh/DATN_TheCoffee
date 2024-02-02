package com.example.thecoffee.views

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AbsListView
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.thecoffee.R
import com.example.thecoffee.databinding.FragmentHomeBinding
import com.example.thecoffee.viewmodel.LoginViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

//        binding.btnLogIn.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
////            val view = layoutInflater.inflate(R.layout.fragment_login, null, false)
////            val popupWindow = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
////            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
//        }

        // image slide show
        val imageList = ArrayList<SlideModel>()  // create image list
        imageList.add(SlideModel(R.drawable.slide1))
        imageList.add(SlideModel(R.drawable.slide2))
        imageList.add(SlideModel(R.drawable.slide3))
        imageList.add(SlideModel(R.drawable.slide4))
        imageList.add(SlideModel(R.drawable.slide5))

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)

        //detect scroll up/down
        binding.homeContent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < oldScrollY) {
                binding.textView.text = "Scroll up"
            } else {

            }
        }

        binding.btnNoti.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_loginFragment)
//            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }


}