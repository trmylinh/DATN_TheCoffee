package com.example.thecoffee.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.thecoffee.R
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerAdapter
import com.example.thecoffee.adapter.ItemDrinkHomeRecyclerInterface
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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


        //fake data item drink home
        val list = mutableListOf<Drink>()
        val category = mutableListOf<Category>()

//        category.add(Category("1", "cake", R.drawable.img))
//        list.add(Drink("1", "Bear 1", "Delicious", R.drawable.img, 35000, 4000, "0"))
//        list.add(Drink("2", "Bear 2", "Delicious", R.drawable.img, 35000, 4000, "0"))
//        list.add(Drink("3", "Bear 3", "Delicious", R.drawable.img, 35000, 4000,"0"))
//        list.add(Drink("4", "Bear 4", "Delicious", R.drawable.img, 35000, 4000, "0"))
//        list.add(Drink("5", "Bear 5", "Delicious", R.drawable.img, 35000, 4000, "0"))
//        list.add(Drink("6", "Bear 6", "Delicious", R.drawable.img, 35000, 4000, "0"))
//        list.add(Drink("7", "Bear 7", "Delicious", R.drawable.img, 35000, 4000, "0"))
//        list.add(Drink("8", "Bear 8", "Delicious", R.drawable.img, 35000, 4000, "0"))
//        list.add(Drink("9", "Bear 9", "Delicious", R.drawable.img, 35000, 4000, "0"))
//
//        val adapter = ItemDrinkHomeRecyclerAdapter(list, object: ItemDrinkHomeRecyclerInterface{
//            override fun onClickItemDrink(position: Drink) {
//                Toast.makeText(requireContext(), "Choose ${list[position].name}", Toast.LENGTH_LONG).show()
//            }
//        })
//        binding.recyclerViewItemDrinkHome.adapter = adapter
//        // 1 list
//        binding.recyclerViewItemDrinkHome.layoutManager = LinearLayoutManager(
//            requireContext(),
//            LinearLayoutManager.HORIZONTAL,
//            false)
//
//        // 2 list
//        binding.recyclerViewItemRecommend.adapter = adapter
//        binding.recyclerViewItemRecommend.layoutManager = LinearLayoutManager(
//            requireContext(),
//            LinearLayoutManager.HORIZONTAL,
//            false)

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

        binding.more.paintFlags = android.graphics.Paint.UNDERLINE_TEXT_FLAG

    }


}