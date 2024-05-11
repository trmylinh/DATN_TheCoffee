package com.example.thecoffee.admin.manage.drink.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.drink.adapter.ManageDrinkInfoAdapter
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManageDrinkDetailAdminBinding
import com.example.thecoffee.databinding.FragmentManagerDrinkAdminBinding
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.model.Size
import com.example.thecoffee.order.model.Topping
import com.example.thecoffee.order.viewmodel.ProductViewModel

class ManageDrinkDetailAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageDrinkDetailAdminBinding
    private lateinit var adapterSize: ManageDrinkInfoAdapter
    private lateinit var adapterTopping: ManageDrinkInfoAdapter
    private lateinit var productViewModel: ProductViewModel

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageDrinkDetailAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val args = ManageDrinkDetailAdminFragmentArgs.fromBundle(it)
            val result = args.detailDrink
            Log.d("result", "$result")

            binding.nameProduct.text = result.name
            Glide.with(requireContext()).load(result.image).into(binding.imgProduct)
            binding.descProduct.text = result.desc

            binding.descProduct.text = result.desc
            binding.descProduct.setCollapsedText("Xem thêm")
            binding.descProduct.setExpandedText("Rút gọn")
            binding.descProduct.setCollapsedTextColor(R.color.orange_900)
            binding.descProduct.setExpandedTextColor(R.color.orange_900)
            binding.descProduct.setTrimLength(4)

            if(result.size?.isNotEmpty() == true){
                adapterSize = ManageDrinkInfoAdapter(result.size)
                binding.rvSize.adapter = adapterSize
                binding.rvSize.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            } else {
                binding.layoutRvSize.visibility = View.GONE
            }

            if(result.topping?.isNotEmpty() == true){
                adapterTopping = ManageDrinkInfoAdapter(result.topping)
                binding.rvTopping.adapter = adapterTopping
                binding.rvTopping.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            } else {
                binding.layoutRvTopping.visibility = View.GONE
            }

            var isEditable = false
            // btn update
            binding.btnUpdate.setOnClickListener {
                isEditable = !isEditable

                if (isEditable){
                    binding.nameProduct.visibility = View.GONE
                    binding.descProduct.visibility = View.GONE

                    binding.edtNameProduct.visibility = View.VISIBLE
                    binding.edtDescProduct.visibility = View.VISIBLE

                    binding.edtNameProduct.setText(result.name)
                    binding.edtDescProduct.setText(result.desc)

                    binding.btnAddSize.visibility = View.VISIBLE
                    binding.btnAddTopping.visibility = View.VISIBLE

                    // add more size, topping
                    binding.btnAddSize.setOnClickListener {
                        showDialogAddMore("Thêm size")
                    }

                    binding.btnAddTopping.setOnClickListener {
                        showDialogAddMore("Thêm topping")
                    }

                    // change img
                    binding.btnChangeImg.visibility = View.VISIBLE
                    binding.btnChangeImg.setOnClickListener {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        pickImage.launch(intent)
                    }

                } else {
                    binding.nameProduct.visibility = View.VISIBLE
                    binding.descProduct.visibility = View.VISIBLE

                    binding.nameProduct.text = binding.edtNameProduct.text.toString()
                    binding.descProduct.text = binding.edtDescProduct.text.toString()

                    binding.edtNameProduct.visibility = View.GONE
                    binding.edtDescProduct.visibility = View.GONE
                    binding.btnChangeImg.visibility = View.GONE

                    binding.btnAddSize.visibility = View.GONE
                    binding.btnAddTopping.visibility = View.GONE

                    val edtNameProduct = binding.edtNameProduct.text.toString()
                    val edtDescProduct = binding.edtDescProduct.text.toString()
                    val imgProduct = imageUri ?: result.image

                    val newItem = Drink(result.id, edtNameProduct, edtDescProduct,
                        imgProduct.toString(), result.price, 0, result.categoryId, adapterSize.list as List<Size>, adapterTopping.list as List<Topping>)
                    productViewModel.updateDataDrink(result.id!!, newItem)
                }

                // size - topping
                adapterSize.isEditable = !adapterSize.isEditable
                adapterTopping.isEditable = !adapterTopping.isEditable

            }

            binding.btnDelete.setOnClickListener {
                productViewModel.deleteDataDrink(result.id!!)
                findNavController().popBackStack()
            }
        }

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
//                showDialog(true)
                imageUri = result.data!!.data
                Glide.with(requireContext()).load(imageUri).into(binding.imgProduct)

            }
        }
    }

    private fun showDialogAddMore(title: String){
        val customLayoutDialogAddMore = layoutInflater.inflate(R.layout.layout_edittext_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setView(customLayoutDialogAddMore)

        builder.setPositiveButton("Thêm"){ dialog,id ->
            val edtNameSize = customLayoutDialogAddMore.findViewById<EditText>(R.id.edtTextName)
            val edtPriceSize = customLayoutDialogAddMore.findViewById<EditText>(R.id.edtTextPrice)

            val result = hashMapOf(
                "name" to edtNameSize.text.toString(),
                "price" to edtPriceSize.text.toString()
            )
            sendDialogDataToFragment(result, type= if(title == "Thêm size") "size" else "topping")
        }.setNegativeButton("Hủy") { dialog, id ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun sendDialogDataToFragment(data: Map<String, String>, type: String){
        val name = data["name"]
        val price = data["price"]
        if(type == "size"){
            val insertPosition = adapterSize.list.size
            (adapterSize.list as MutableList).add(Size(name, price?.toLong()))
            adapterSize.notifyItemInserted(insertPosition)
        } else {
            val insertPosition = adapterTopping.list.size
            (adapterTopping.list as MutableList).add(Topping(name, price?.toLong()))
            adapterTopping.notifyItemInserted(insertPosition)
        }

    }
}