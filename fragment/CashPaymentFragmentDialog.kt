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

/**
 * Cash payment fragment dialog
 *
 * コンストラクタ  Cash payment fragment dialog
 */
class CashPaymentFragmentDialog :
    BaseFragmentDialog<FragmentCashPaymentDialogBinding, FoodShopViewModel>() {

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
        return R.layout.fragment_cash_payment_dialog
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
        binding.viewModel = viewModel
        viewModel.setPaymentStrategy(CashPaymentStrategy())
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.paymentMethodFragmentDialog)
        }

        binding.btnFinish.setOnClickDebounce {
            findNavController().navigate(R.id.cashSummaryFragmentDialog)
        }

        setupKeypadViewListener()
    }

    /**
     * Setup keypad view listener
     *
     */
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