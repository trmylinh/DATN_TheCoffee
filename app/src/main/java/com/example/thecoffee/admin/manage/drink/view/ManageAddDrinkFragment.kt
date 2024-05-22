package com.example.thecoffee.admin.manage.drink.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecoffee.R
import com.example.thecoffee.admin.manage.drink.adapter.ManageCategorySpinnerAdapter
import com.example.thecoffee.admin.manage.drink.adapter.ManageCategorySpinnerInterface
import com.example.thecoffee.admin.manage.drink.adapter.ManageDrinkInfoAdapter
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManageAddDrinkBinding
import com.example.thecoffee.databinding.FragmentManagerDrinkAdminBinding
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.model.Size
import com.example.thecoffee.order.model.Topping
import com.example.thecoffee.order.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ManageAddDrinkFragment : Fragment() {
    private lateinit var binding: FragmentManageAddDrinkBinding
    private lateinit var productViewModel: ProductViewModel

    private lateinit var categories: List<Category>
    private lateinit var categorySpinnerAdapter: ManageCategorySpinnerAdapter

    private var imageProductUri: Uri? = null
    private var imageCategoryUri: Uri? = null

    private var listSize: MutableList<Size> = mutableListOf()
    private var listTopping: MutableList<Topping> = mutableListOf()

    private lateinit var adapterSize: ManageDrinkInfoAdapter
    private lateinit var adapterTopping: ManageDrinkInfoAdapter

    private lateinit var imageViewInDialog: ImageView
    private lateinit var clearImgCategory: ImageView
    private lateinit var viewAddImgCategory: LinearLayout
    private lateinit var viewImgCategory: RelativeLayout

    private var categoryIdSpinner: String = ""
    private var categoryIdNew: String = ""

    private lateinit var alertDialog: AlertDialog
    private var newCategory: Category? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]

        productViewModel.getDataCategoryList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageAddDrinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            setFragmentResult("refresh", bundleOf("isRefreshing" to true))
            findNavController().popBackStack()
        }

        // category
        CoroutineScope(Dispatchers.Main).launch {
            categories = initCategoryList()

            binding.viewDropdownCategory.setOnClickListener {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.layout_dialog_search_category_spinner)

                dialog.window?.setLayout(700, 800)
                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                dialog.show()

                val edtText = dialog.findViewById<TextView>(R.id.edit_text)
                val rvCategory = dialog.findViewById<RecyclerView>(R.id.rv_category_spinner)

                categorySpinnerAdapter = ManageCategorySpinnerAdapter(
                    categories,
                    object : ManageCategorySpinnerInterface {
                        override fun onCategorySelected(category: Category) {
                            Log.d("category", "onCategorySelected: ${category.categoryId}")
                            binding.spinnerCategory.text = category.name
                            binding.imgCategory.visibility = View.VISIBLE
                            Glide.with(requireContext()).load(category.image)
                                .into(binding.imgCategory)

                            categoryIdSpinner = category.categoryId!!
                            dialog.dismiss()
                        }
                    })
                rvCategory.adapter = categorySpinnerAdapter
                rvCategory.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                if(newCategory != null){
                    (categorySpinnerAdapter.list as MutableList).add(newCategory!!)
                    categorySpinnerAdapter.notifyItemInserted(
                        categorySpinnerAdapter.list.size - 1
                    )
                }

                edtText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        categorySpinnerAdapter.filter(edtText.text.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
                dialog.setCancelable(true)
            }

        }

        binding.edtName.addTextChangedListener {
            checkEditTexts()
        }

        binding.edtPrice.addTextChangedListener {
            checkEditTexts()
        }

        // pick image product
        binding.btnAddImage.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImageProduct.launch(intent)
        }

        // add size
        binding.btnAddSize.setOnClickListener {
            showDialogAddMore("Thêm size")
        }

        // add topping
        binding.btnAddTopping.setOnClickListener {
            showDialogAddMore("Thêm topping")
        }

        binding.viewAddNewCategory.setOnClickListener {
            showDialogAddNewCategory()
        }

        binding.btnAddProduct.setOnClickListener {
            val drinkId = generateRandomId()
            val name = binding.edtName.text.toString()
            val price = binding.edtPrice.text.toString().toInt()
            val image = imageProductUri.toString()
            val categoryId = categoryIdSpinner.ifEmpty { categoryIdNew }
            val size = listSize
            val topping = listTopping
            val discount = 0
            val desc = binding.edtDesc.text.toString()

            val drink =
                Drink(drinkId, name, desc, image, price, categoryId, false, size, topping)
            productViewModel.createDrink(drink)
            productViewModel.loadingAddDrinkResult.observe(viewLifecycleOwner) {
                if (it) {
                    binding.progressBarAddProduct.visibility = View.VISIBLE
                } else {
                    binding.progressBarAddProduct.visibility = View.GONE
                    productViewModel.getMessageCreateDrink.observe(viewLifecycleOwner) { message ->
                        setFragmentResult("refresh", bundleOf("create_message" to message))
                        findNavController().popBackStack()
                    }
                }
            }

        }

    }

    private fun checkEditTexts() {
        var emptyFieldCount = 0

        if (binding.edtName.text.isEmpty()) {
            emptyFieldCount++
        }

        if (binding.edtPrice.text.isEmpty()) {
            emptyFieldCount++
        }

        if (imageProductUri == null) {
            emptyFieldCount++
        }

        binding.btnAddProduct.isEnabled = emptyFieldCount == 0
        binding.btnAddProduct.backgroundTintList =
            if (emptyFieldCount == 0)
                requireContext().getColorStateList(R.color.light_blue_900)
            else requireContext().getColorStateList(R.color.grey_400)
    }

    private fun generateRandomId(): String {
        return "${UUID.randomUUID()}"
    }

    private fun showDialogAddMore(title: String) {
        val customLayoutDialogAddMore =
            layoutInflater.inflate(R.layout.layout_edittext_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setView(customLayoutDialogAddMore)

        val edtNameSize = customLayoutDialogAddMore.findViewById<EditText>(R.id.edtTextName)
        val edtPriceSize = customLayoutDialogAddMore.findViewById<EditText>(R.id.edtTextPrice)
        viewAddImgCategory = customLayoutDialogAddMore.findViewById(R.id.view_add_img_category)
        viewImgCategory = customLayoutDialogAddMore.findViewById(R.id.view_img_category)

        edtPriceSize.visibility = View.VISIBLE
        viewAddImgCategory.visibility = View.GONE
        viewImgCategory.visibility = View.GONE

        builder.setPositiveButton("Thêm") { dialog, id ->
            val result = hashMapOf(
                "name" to edtNameSize.text.toString(),
                "price" to edtPriceSize.text.toString()
            )

            sendDialogDataToFragment(result, type = if (title == "Thêm size") "size" else "topping")
        }.setNegativeButton("Hủy") { dialog, id ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDialogAddNewCategory() {
        val customLayoutDialogAddMore =
            layoutInflater.inflate(R.layout.layout_edittext_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thêm danh mục mới")
        builder.setView(customLayoutDialogAddMore)

        val edtName = customLayoutDialogAddMore.findViewById<EditText>(R.id.edtTextName)
        val edtPrice = customLayoutDialogAddMore.findViewById<EditText>(R.id.edtTextPrice)
        viewAddImgCategory = customLayoutDialogAddMore.findViewById(R.id.view_add_img_category)
        viewImgCategory = customLayoutDialogAddMore.findViewById(R.id.view_img_category)
        imageViewInDialog = customLayoutDialogAddMore.findViewById(R.id.img_new_category)
        clearImgCategory = customLayoutDialogAddMore.findViewById(R.id.icon_delete_img_category)

        edtPrice.visibility = View.GONE

        viewAddImgCategory.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImageCategory.launch(intent)
        }

        builder.setPositiveButton("Thêm", null)
            .setNegativeButton("Hủy") { dialog, id ->
                dialog.cancel()
            }

        alertDialog = builder.create()

        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.isEnabled = false

            positiveButton.setOnClickListener {
                if (imageCategoryUri != null) {
                    categoryIdNew = generateRandomId()
                    val result = hashMapOf(
                        "id" to categoryIdNew,
                        "name" to edtName.text.toString(),
                        "image" to "$imageCategoryUri"
                    )
                    val newCategory = Category(
                        result["id"],
                        result["name"],
                        result["image"]
                    )
                    productViewModel.createCategory(newCategory)
                    productViewModel.loadingAddCategoryResult.observe(viewLifecycleOwner) { loading ->
                        if (loading) {
                            binding.progressBarAddProduct.visibility = View.VISIBLE
                        } else {
                            binding.progressBarAddProduct.visibility = View.GONE
                            sendDialogDataToFragment(result, type = "category")
                        }
                        alertDialog.dismiss()
                    }
                }
            }
        }
        alertDialog.show()
    }

    private fun sendDialogDataToFragment(data: Map<String, Any>, type: String) {
        val name = data["name"]
        val price = data["price"]
        val image = data["image"]
        val id = data["id"]
        Log.d("img", "sendDialogDataToFragment: $id $image")
        when (type) {
            "size" -> {
                binding.rvSize.visibility = View.VISIBLE
                adapterSize = ManageDrinkInfoAdapter(listSize, null)
                adapterSize.isEditable = true
                binding.rvSize.adapter = adapterSize
                binding.rvSize.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                val insertPosition = adapterSize.listSize?.size
                (adapterSize.listSize as MutableList).add(
                    Size(
                        name.toString(),
                        price.toString().toLong()
                    )
                )
                adapterSize.notifyItemInserted(insertPosition!!)
            }

            "topping" -> {
                binding.rvTopping.visibility = View.VISIBLE
                adapterTopping = ManageDrinkInfoAdapter(null, listTopping)
                adapterTopping.isEditable = true
                binding.rvTopping.adapter = adapterTopping
                binding.rvTopping.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                val insertPosition = adapterTopping.listTopping?.size
                (adapterTopping.listTopping as MutableList).add(
                    Topping(
                        name.toString(),
                        price.toString().toLong()
                    )
                )
                adapterTopping.notifyItemInserted(insertPosition!!)
            }

            else -> {
                binding.imgCategory.visibility = View.VISIBLE
                Glide.with(requireContext()).load(image).into(binding.imgCategory)
                binding.spinnerCategory.text = name.toString()
                newCategory = Category(id.toString(), name.toString(), image.toString())
            }
        }

    }

    private val pickImageProduct = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                imageProductUri = result.data!!.data

                binding.btnAddImage.visibility = View.GONE
                binding.viewImageProduct.visibility = View.VISIBLE
                Glide.with(requireContext()).load(imageProductUri).into(binding.imgProduct)

                binding.iconDeleteImg.setOnClickListener {
                    imageProductUri = null
                    binding.viewImageProduct.visibility = View.GONE
                    binding.btnAddImage.visibility = View.VISIBLE
                    checkEditTexts()
                }
            }
            checkEditTexts()
        }
    }

    private val pickImageCategory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                imageCategoryUri = result.data!!.data
                Log.d("img", "pickImageCategory: $imageCategoryUri")

                viewAddImgCategory.visibility = View.GONE
                viewImgCategory.visibility = View.VISIBLE
                Glide.with(requireContext()).load(imageCategoryUri).into(imageViewInDialog)

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true

                clearImgCategory.setOnClickListener {
                    imageCategoryUri = null
                    viewImgCategory.visibility = View.GONE
                    viewAddImgCategory.visibility = View.VISIBLE

                }
            }
        }
    }

    private suspend fun initCategoryList(): List<Category> =
        suspendCoroutine { continuation ->
            productViewModel.getCategoryList.observe(viewLifecycleOwner) {
                continuation.resume(it)
            }
        }

}
