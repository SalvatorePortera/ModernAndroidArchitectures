package com.nereus.craftbeer.fragment

import android.widget.TableRow
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentCashPaymentDialogBinding
import com.nereus.craftbeer.model.keyboard.KeyPad
import com.nereus.craftbeer.model.payment.CashPayment
import com.nereus.craftbeer.model.payment.CashPaymentStrategy
import com.nereus.craftbeer.util.keyboard.getKeyPadValue
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class CashPaymentFragmentDialog :
    BaseFragmentDialog<FragmentCashPaymentDialogBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_cash_payment_dialog
    }

    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
        binding.viewModel = viewModel
        viewModel.setPaymentStrategy(CashPaymentStrategy())
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.paymentMethodFragmentDialog)
        }

        binding.btnFinish.setOnClickDebounce {
            findNavController().navigate(R.id.cashSummaryFragmentDialog)
        }

        setupKeypadViewListener()
    }

    private fun setupKeypadViewListener() {
        binding.tableNumpad.children.forEach { row ->
            (row as TableRow).children.forEach { cell ->
                cell.setOnClickListener {
                    viewModel.inputPaymentDeposit(getKeyPadValue(it.id))
                }
            }
        }

        binding.btnDel.setOnLongClickListener {
            viewModel.inputPaymentDeposit(KeyPad.KeyPadValue.DELETE_ALL)
            true
        }
    }
}