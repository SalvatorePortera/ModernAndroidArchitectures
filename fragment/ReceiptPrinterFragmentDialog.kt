package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.PrinterLogViewAdapter
import com.nereus.craftbeer.databinding.FragmentReceiptPrinterDialogBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.printer.hasAnySelected
import com.nereus.craftbeer.util.getEmptyViewVisibility
import com.nereus.craftbeer.util.getRecyclerViewVisibility
import com.nereus.craftbeer.util.livedata.EventObserver
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel


class ReceiptPrinterFragmentDialog :
    BaseFragmentDialog<FragmentReceiptPrinterDialogBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_receipt_printer_dialog
    }

    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
        configRecyclerView()
        viewModel.initPrinterDialog()
        viewModel.startHandlerSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopHandlerSearch()
    }

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
    }

    override fun setAdditionalViewModelListener() {
        this.viewModel.receipts.observe(binding.lifecycleOwner!!, Observer { receipts ->
            binding.recyclerViewPrinterLogList.visibility = receipts.getRecyclerViewVisibility()
            binding.emptyView.visibility = receipts.getEmptyViewVisibility()

            if (receipts.isNotEmpty()) {
                binding.header.text = getString(R.string.header_search_receipt_complete)
            }

            (binding.recyclerViewPrinterLogList.adapter as PrinterLogViewAdapter).submitList(
                receipts
            )
        })
        this.viewModel.printReceiptHistoryModel.observe(viewLifecycleOwner, EventObserver {
            this.viewModel.searchSaleLogs(it.pointPlusId)
        })
    }

    private fun configRecyclerView() {
        binding.recyclerViewPrinterLogList.adapter =
            PrinterLogViewAdapter(this.requireContext(), viewModel)
        binding.recyclerViewPrinterLogList.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }
}