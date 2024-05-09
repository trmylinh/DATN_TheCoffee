package com.example.thecoffee.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.thecoffee.R
import com.example.thecoffee.home.adapter.ItemDrinkHomeRecyclerAdapter
import com.example.thecoffee.home.adapter.ItemDrinkHomeRecyclerInterface
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.databinding.FragmentHomeBinding
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.order.viewmodel.ProductViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var productViewModel: ProductViewModel
    private var drinkList = mutableListOf<Drink>()
    private lateinit var adapterListDrink: ItemDrinkHomeRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel =
            ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
        productViewModel.getDataDrinkBySale()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        val auth = Firebase.auth
        val user = auth.currentUser
        if(user != null){
            binding.textHelloUser.text = "Xin chào, ${user.displayName}"
        }

        // image slide show
        val imageList = ArrayList<SlideModel>()  // create image list
        imageList.add(SlideModel(R.drawable.slide1))
        imageList.add(SlideModel(R.drawable.slide2))
        imageList.add(SlideModel(R.drawable.slide3))
        imageList.add(SlideModel(R.drawable.slide4))
        imageList.add(SlideModel(R.drawable.slide5))

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)


        //item drink home
        productViewModel.getDrinkList.observe(viewLifecycleOwner){
            drinkList = it
            adapterListDrink = ItemDrinkHomeRecyclerAdapter(it, object: ItemDrinkHomeRecyclerInterface{
                override fun onClickItemDrink(position: Drink) {
                    Log.e("press", position.name.toString())
                }
            })
            binding.recyclerViewItemDrinkHome.adapter = adapterListDrink
            binding.recyclerViewItemDrinkHome.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)

        }

        //detect scroll up/down
        binding.homeContent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (scrollY < oldScrollY) {
//                binding.textView.text = "Scroll up"
//            } else {
//
//            }
        }

        binding.btnNoti.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        // xem them - text underline
        binding.more.paintFlags = android.graphics.Paint.UNDERLINE_TEXT_FLAG

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("token", token)
        })

    }

}