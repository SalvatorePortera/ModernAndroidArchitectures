package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentCashSummaryBinding
import com.nereus.craftbeer.model.payment.CashPayment
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

/**
 * Cash summary fragment dialog
 *
 * @constructor Create empty Cash summary fragment dialog
 */
class CashSummaryFragmentDialog :
    BaseFragmentDialog<FragmentCashSummaryBinding, FoodShopViewModel>() {

    /**
     * View model
     */
    override val viewModel: FoodShopViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_cash_summary
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        setDialogSizeScale(0.65, 0.65)
        binding.viewModel = viewModel
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.paymentMethodFragmentDialog)
        }

        binding.btnConfirm.setOnClickDebounce {
            viewModel.setPayment(CashPayment(viewModel.cashReceived.value!!))
        }
    }
}