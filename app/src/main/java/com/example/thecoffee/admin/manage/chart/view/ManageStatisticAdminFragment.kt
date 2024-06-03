package com.example.thecoffee.admin.manage.chart.view

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.icu.util.Calendar
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.thecoffee.R
import com.example.thecoffee.base.MyViewModelFactory
import com.example.thecoffee.databinding.FragmentManageDrinkDetailAdminBinding
import com.example.thecoffee.databinding.FragmentManageStatisticAdminBinding
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.viewmodel.BillViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

class ManageStatisticAdminFragment : Fragment() {
    private lateinit var binding: FragmentManageStatisticAdminBinding
    private lateinit var billViewModel: BillViewModel
    private var bills = mutableListOf<Bill>()

    var statusCancel = 0  //-1
    var statusWaitConfirm = 0  //0
    var statusConfirmed = 0   //1
    var statusDelivering = 0  //2
    var statusDone = 0      //3
    var totalCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MyViewModelFactory(requireActivity().application)
        billViewModel = ViewModelProvider(this, viewModelFactory)[BillViewModel::class.java]

        billViewModel.getAllBills()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageStatisticAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        CoroutineScope(Dispatchers.Main).launch {
            bills = getBills() as MutableList<Bill>

            // pie chart
            val currentDate = getDateOnly()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            binding.tvDate.text = sdf.format(currentDate)
            filterDataPieChart(sdf.format(currentDate))

            binding.btnPickDate.setOnClickListener {
                val datePicker = DatePickerDialog(requireContext())
                datePicker.setOnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val formattedDay = String.format("%02d", dayOfMonth)
                    val formattedMonth = String.format("%02d", monthOfYear + 1)
                    val date = "$formattedDay/$formattedMonth/$year"
                    binding.tvDate.text = date

                    filterDataPieChart(date)
                }
                datePicker.show()
            }

            // bar chart
            // spinner year
            val years = ArrayList<String>()
            val thisYear = Calendar.getInstance().get(Calendar.YEAR)
            for (i in 2022..thisYear) {
                years.add(i.toString())
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
            binding.spinnerYear.adapter = adapter
            binding.spinnerYear.setSelection(adapter.count - 1)

            binding.spinnerYear.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString().toInt()
                    getYearFromDate(selectedItem)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }

    }

    private suspend fun getBills(): List<Bill> =
        suspendCoroutine { continuation ->
            billViewModel.getBills.observe(viewLifecycleOwner) {
                continuation.resume(it)
            }
        }

    private fun getDateOnly(): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = Date()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun getYearFromDate(yearFind: Int){
        val dates  = arrayListOf("22/04/2023", "15/04/2023", "07/05/2023", "19/12/2023", "03/06/2024", "12/05/2024");
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val monthCounts = mutableMapOf<Pair<Int, Int>, Int>()
        bills .forEach { bill   ->
            val dateOnly = bill.time?.substring(9)
            val date = dateOnly?.let { format.parse(it) }
//            val date = format.parse(dateString)
            date?.let {
                val calendar = Calendar.getInstance()
                calendar.time = it
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val yearMonthKey = Pair(year, month)
                monthCounts[yearMonthKey] = (monthCounts[yearMonthKey] ?: 0) + 1
            }
        }
        val years = monthCounts.keys.map { it.first }.toSet()
        for (year in years) {
            for (month in 1..12) {
                val yearMonthKey = Pair(year, month)
                monthCounts.putIfAbsent(yearMonthKey, 0)
            }
        }
        val list: ArrayList<BarEntry> = ArrayList()
        var x = 100
        monthCounts.toSortedMap(compareBy({ it.first }, { it.second })).forEach { (yearMonth, count) ->
            if(yearMonth.first == yearFind){
                list.add(BarEntry(x.toFloat(), count.toFloat()))
                x++
            }
        }
        showListDataBarChart(list)
    }

    private fun showListDataBarChart(list: ArrayList<BarEntry>) {
        val defaultNoOfBarsInViewport = 5f // so luong cot muon hien thi moi khi cuon
        val dataSets = mutableListOf<IBarDataSet>()
        val labels =
            arrayOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12")
        val colorsSet = mutableListOf<Int>()
        colorsSet.add(resources.getColor(R.color.red_100, null))
        colorsSet.add(resources.getColor(R.color.purple_A100, null))
        colorsSet.add(resources.getColor(R.color.pink_800, null))
        colorsSet.add(resources.getColor(R.color.indigo_300, null))
        colorsSet.add(resources.getColor(R.color.cyan_300, null))
        colorsSet.add(resources.getColor(R.color.orange_500, null))
        colorsSet.add(resources.getColor(R.color.deep_purple_500, null))
        colorsSet.add(resources.getColor(R.color.green_100, null))
        colorsSet.add(resources.getColor(R.color.blue_300, null))
        colorsSet.add(resources.getColor(R.color.brown_300, null))
        colorsSet.add(resources.getColor(R.color.lime_100, null))
        colorsSet.add(resources.getColor(R.color.blue_gray_500, null))

        list.forEachIndexed { index, barEntry ->
            val entryList = mutableListOf(barEntry)
            val set = BarDataSet(entryList, labels[index])
            set.color = colorsSet[index]
            dataSets.add(set)
        }

        val barData = BarData(dataSets)
        binding.barChart.apply {
            setFitBars(true)
            setData(barData)
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.isEnabled = true
            legend.textSize = 12f
            legend.xEntrySpace = 5f // Khoảng cách giữa chú thích
            legend.setDrawInside(false)
            legend.form = Legend.LegendForm.DEFAULT // Hình dạng của chú thích
            description.isEnabled =
                false // mo ta bieu do, mac dinh o goc duoi cung ben phai bieu do
            isDragEnabled = true
            isScaleYEnabled = false // chi cuon ngang khong cuon doc
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            setVisibleXRangeMaximum(defaultNoOfBarsInViewport)
            setExtraOffsets(0f, 0f, 0f, 10f)
            animateY(2000)
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun showFilterDataPieChart(
        statusCancel: Int,
        statusWaitConfirm: Int,
        statusConfirmed: Int,
        statusDelivering: Int,
        statusDone: Int,
        totalCount: Int
    ) {
        binding.pieChart.apply {
            setUsePercentValues(true) // hien thi gia tri tre bieu do duoi dang %
            description.isEnabled =
                false // mo ta bieu do, mac dinh o goc duoi cung ben phai bieu do
            setExtraOffsets(
                5f,
                10f,
                5f,
                5f
            ) // thiet lap kcach phan ngoai cung cua bieu do vs vien ViewParent cua no
            dragDecelerationFrictionCoef =
                0.95f // toc do giam khi keo bieu do, gtri nay cang gan 1 se giam cham hon
            isDrawHoleEnabled = true // ve 1 lo o bieu do, dang bieu do  hinh tron
            setHoleColor(
                resources.getColor(
                    R.color.white_900,
                    null
                )
            ) // mau cho phan lo o giua bieu do
            setTransparentCircleColor(
                resources.getColor(
                    R.color.white_900,
                    null
                )
            )  // mau cho vong tron trong suot ben ngoai lo
            setTransparentCircleAlpha(300) // do trong suot cho vong tron ben ngoai bieu do
            holeRadius = 40f // ban kinh lo o giua
            transparentCircleRadius =
                0f // kich thuoc ban kinh cua vong tron trong suot ben ngoai lo
            setDrawCenterText(true) // hien thi van ban o giua bieu do
            centerText = "$totalCount đơn hàng"
            setCenterTextSize(15f)
            rotationAngle = 0f // goc bdau cua bieu do (0 do o phan top)
            isRotationEnabled = true // cho phep bieu do co the xoay
            isHighlightPerTapEnabled =
                true // khi tap vao 1 phan bieu do, phan no se dc lam noi bat
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = true // chu thich cua bieu do
            legend.orientation = Legend.LegendOrientation.VERTICAL // kieu chu thich bieu do
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.textSize = 12f // kich thuoc chu thich bieu do
            legend.setDrawInside(false) // chu thich nam ben ngoai bieu do
            setEntryLabelColor(R.color.white_900) // mau chu cho nhan cua tung ptu trong bieu do
            setEntryLabelTextSize(12f) // text cho nhan cua tung phan tu trong bieu do
            setDrawEntryLabels(false)
        }

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(statusWaitConfirm.toFloat(), "Chờ xác nhận"))
        entries.add(PieEntry(statusConfirmed.toFloat(), "Đã xác nhận"))
        entries.add(PieEntry(statusDelivering.toFloat(), "Đang giao"))
        entries.add(PieEntry(statusDone.toFloat(), "Đã giao"))
        entries.add(PieEntry(statusCancel.toFloat(), "Đã hủy"))

        val dataSet = PieDataSet(entries, "Trạng thái đơn hàng")
        dataSet.setDrawIcons(true) // hien thi icon cho moi phan cua bieu do, voi dieu kien la da set icon cho moi PieEntry
        dataSet.sliceSpace = 3f // k.cach giua cac phan cua bieu do
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 10f // khi bieu do duoc tap, muc do nhô ra của biều đồ

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.grey_500, null))
        colors.add(resources.getColor(R.color.light_blue_100, null))
        colors.add(resources.getColor(R.color.yellow_300, null))
        colors.add(resources.getColor(R.color.green_200, null))
        colors.add(resources.getColor(R.color.red_400, null))

        dataSet.colors = colors
        val data = PieData(dataSet)

        data.setValueFormatter(PercentFormatter()) // dinh dang gia tri hien thi tren bieu do duoi dang phan tram
        data.setValueTextSize(15f) // kich thuoc gia tri hien thi tren cac phan tu bieu do
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(R.color.white_900)
        binding.pieChart.apply {
            setData(data)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) ""
                    else "${String.format("%.2f", value)}%"
                }
            })
            highlightValue(null) // bo chon lat bieu do nao dang duoc hightlight neu co
            notifyDataSetChanged()
            invalidate() // yeu cau bieu do ve lai voi du lieu moi duoc cung cap
        }

    }

    private fun filterDataPieChart(date: String) {
        statusCancel = 0  //-1
        statusWaitConfirm = 0  //0
        statusConfirmed = 0   //1
        statusDelivering = 0  //2
        statusDone = 0      //3
        totalCount = 0
        bills.forEach { bill ->
            val dateOnly = bill.time?.substring(9)
            if (dateOnly == date) {
                when (bill.status) {
                    -1L -> {
                        // huy
                        statusCancel++
                    }

                    0L -> {
                        // dang cho xac nhan
                        statusWaitConfirm++
                    }

                    1L -> {
                        // da xac nhan
                        statusConfirmed++
                    }

                    2L -> {
                        statusDelivering++
                    }

                    3L -> {
                        // giao hang thanh cong
                        statusDone++
                    }
                }
            }
        }
        totalCount =
            statusCancel + statusWaitConfirm + statusConfirmed + statusDelivering + statusDone
        showFilterDataPieChart(statusCancel, statusWaitConfirm, statusConfirmed, statusDelivering, statusDone, totalCount)
    }
}