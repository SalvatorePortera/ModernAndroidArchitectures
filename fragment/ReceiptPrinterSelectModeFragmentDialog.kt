package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentReceiptPrinterSelectModeDialogBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class ReceiptPrinterSelectModeFragmentDialog :
    BaseFragmentDialog<FragmentReceiptPrinterSelectModeDialogBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_receipt_printer_select_mode_dialog
    }

    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.btnSearchHouseMoney.setOnClickDebounce {
            findNavController().navigate(R.id.receiptPrinterFragmentDialog)
        }

        binding.btnSearchDateTime.setOnClickDebounce {
            findNavController().navigate(R.id.receiptPrinterByDateTimeFragmentDialog)
        }
    }
}