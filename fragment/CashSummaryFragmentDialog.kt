package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentCashSummaryBinding
import com.nereus.craftbeer.model.payment.CashPayment
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class CashSummaryFragmentDialog :
    BaseFragmentDialog<FragmentCashSummaryBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_cash_summary
    }

    override fun afterBinding() {
        setDialogSizeScale(0.65, 0.65)
        binding.viewModel = viewModel
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.paymentMethodFragmentDialog)
        }

        binding.btnConfirm.setOnClickDebounce {
            viewModel.setPayment(CashPayment(viewModel.cashReceived.value!!))
        }
    }
}