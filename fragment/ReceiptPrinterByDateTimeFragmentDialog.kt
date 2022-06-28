package com.nereus.craftbeer.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.PrinterLogViewAdapter
import com.nereus.craftbeer.databinding.FragmentReceiptPrinterByDateTimeDialogBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.printer.hasAnySelected
import com.nereus.craftbeer.util.*
import com.nereus.craftbeer.util.livedata.EventObserver
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import java.util.*

/**
 * Receipt printer by date time fragment dialog
 *
 * コンストラクタ  Receipt printer by date time fragment dialog
 */
class ReceiptPrinterByDateTimeFragmentDialog :
    BaseFragmentDialog<FragmentReceiptPrinterByDateTimeDialogBinding, FoodShopViewModel>() {
    override val viewModel: FoodShopViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_receipt_printer_by_date_time_dialog
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
        configRecyclerView()
        viewModel.initPrinterByDateTimeDialog()
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.btnPrintReceipt.setOnClickDebounce {
            if (viewModel.receipts.value.hasAnySelected()) {
                viewModel.printReceipts()
            } else {
                viewModel.setException(MessageException(MessagesModel(R.string.msg_no_receipt_selected)))
            }
        }

//        binding.btnIssueReceipt.setOnClickDebounce {
//            if (viewModel.receipts.value.hasAnySelected()) {
//                viewModel.issueReceipts()
//            } else {
//                viewModel.setException(MessageException(MessagesModel(R.string.msg_no_receipt_selected)))
//            }
//        }

        binding.editDateTimeFrom.setOnClickListener {
            binding.editDateTimeFrom.showDateTimePicker(viewModel::setDateFrom)
        }

        binding.editDateTimeTo.setOnClickListener {
            binding.editDateTimeTo.showDateTimePicker(viewModel::setDateTo)
        }
    }

    /**
     * Set additional view model listener
     *
     */
    override fun setAdditionalViewModelListener() {
        this.viewModel.receipts.observe(binding.lifecycleOwner!!, { receipts ->
            binding.recyclerViewPrinterLogList.visibility = receipts.getRecyclerViewVisibility()
            binding.emptyView.visibility = receipts.getEmptyViewVisibility()

            (binding.recyclerViewPrinterLogList.adapter as PrinterLogViewAdapter).submitList(
                receipts
            )
        })

        this.viewModel.printReceiptByDateTimeModel.observe(viewLifecycleOwner, EventObserver {
            this.viewModel.searchSaleLogs(it)
        })
    }

    /**
     * Config recycler view
     *
     */
    private fun configRecyclerView() {
        binding.recyclerViewPrinterLogList.adapter =
            PrinterLogViewAdapter(this.requireContext(), viewModel)
        val layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewPrinterLogList.layoutManager = layoutManager

        // Can not implement pagination and lazy load because data is combined between 2 APIs /salelogs and /topup
//        val scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
//            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                // viewModel.loadNextPage(viewModel.printReceiptByDateTimeModel.value)
//                Timber.i("==== on load more")
//            }
//        }
//        binding.recyclerViewPrinterLogList.addOnScrollListener(scrollListener)
    }


    /**
     * Custom date and time picker
     * We can set previous date for datetime picker
     * We can change 24 hour format
     * We can set min and max selection date
     * and also can set max and nin selection time
     *
     */
    private fun EditText.showDateTimePicker(setCalendar: (m: Calendar) -> Unit) {
        CustomDateTimePicker(
            requireActivity(),
            object : CustomDateTimePicker.ICustomDateTimeListener {
                @SuppressLint("BinaryOperationInTimber")
                override fun onSet(
                    dialog: Dialog,
                    calendarSelected: Calendar,
                    dateSelected: Date,
                    year: Int,
                    monthFullName: String,
                    monthShortName: String,
                    monthNumber: Int,
                    day: Int,
                    weekDayFullName: String,
                    weekDayShortName: String,
                    hour24: Int,
                    hour12: Int,
                    min: Int,
                    sec: Int,
                    AM_PM: String
                ) {
                    text = calendarSelected.toDisplayedDateTimeString().toEditable()
                    setCalendar(calendarSelected)
                }

                override fun onCancel() {

                }
            }).apply {
            set24HourFormat(true)
//            setMaxMinDisplayDate(
//                minDate = Calendar.getInstance().apply { add(Calendar.MINUTE, 5) }.timeInMillis,
//                maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }.timeInMillis
//            )
//            setMaxMinDisplayedTime(5)
//            setDate(if (cb_set_previous_selected_date.isChecked) selectedDateAndTime else Calendar.getInstance())
            showDialog()
        }
    }


}