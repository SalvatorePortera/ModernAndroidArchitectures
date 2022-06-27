package com.nereus.craftbeer.fragment

import android.widget.ToggleButton
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.FragmentPaymentMethodBinding
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import timber.log.Timber

/**
 * Payment method fragment dialog
 *
 * @constructor  Payment method fragment dialog
 */
class PaymentMethodFragmentDialog :
    BaseFragmentDialog<FragmentPaymentMethodBinding, FoodShopViewModel>() {

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
        return R.layout.fragment_payment_method
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        binding.viewModel = viewModel
        //Reset payment method
        viewModel.setPaymentMethod(null)
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.radioGroupPaymentMethod.children.filter { it is ToggleButton }.forEach {
            when (it) {
                is ToggleButton -> it.setOnCheckedChangeListener { view, isChecked ->
                    if (isChecked) {
                        viewModel.setPaymentMethod(null)
                        binding.radioGroupPaymentMethod.children.filter { it != view && it is ToggleButton }
                            .forEach {
                                (it as ToggleButton).isChecked = false
                            }
                        when (view.id) {
                            R.id.radio_house_money -> viewModel.setPaymentMethod(PAYMENT_HOUSE_MONEY)
                            R.id.radio_cash -> viewModel.setPaymentMethod(PAYMENT_CASH)
                            R.id.radio_emoney -> viewModel.setPaymentMethod(PAYMENT_ELECTRONIC_MONEY)
                            R.id.radio_credit_card -> viewModel.setPaymentMethod(PAYMENT_CREDIR_CARD)
                            R.id.radio_qr -> viewModel.setPaymentMethod(PAYMENT_QR)
                            R.id.radio_other -> viewModel.setPaymentMethod(PAYMENT_OTHERS)
                        }
                        checkout()
                    } else if (viewModel.customerAttribute.value!!.paymentMethod != null) {
                        view.isChecked = true
                        viewModel.setException(MessageException(MessagesModel(R.string.msg_required_field)))
                    }
                }
            }
        }
    }

    /**
     * Checkout
     *
     */
    private fun checkout() {
        val paymentMethod = viewModel.customerAttribute.value!!.paymentMethod
        Timber.i("--------paymentMethod %d", paymentMethod)
        if (paymentMethod == null) {
            viewModel.setException(MessageException(MessagesModel(R.string.msg_no_payment_method)))
            return
        }
        navigate(PaymentMethod.getByValue(paymentMethod)!!)
    }

    /**
     * Navigate
     *
     * @param paymentMethod
     */
    private fun navigate(paymentMethod: PaymentMethod) {
        when (paymentMethod) {
            PaymentMethod.PAYMENT_HOUSE_MONEY -> findNavController().navigate(R.id.houseMoneyCheckoutFragment)
            else -> findNavController().navigate(R.id.cashPaymentFragmentDialog)
        }
    }
}