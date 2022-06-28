package com.nereus.craftbeer.fragment

import android.widget.TableRow
import android.widget.ToggleButton
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.FragmentCashTopUpDialogBinding
import com.nereus.craftbeer.model.keyboard.KeyPad
import com.nereus.craftbeer.model.payment.CashPaymentStrategy
import com.nereus.craftbeer.util.keyboard.getKeyPadValue
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import timber.log.Timber

/**
 * Cash top up fragment dialog
 *
 * コンストラクタ  Cash top up fragment dialog
 */
class CashTopUpFragmentDialog :
    BaseFragmentDialog<FragmentCashTopUpDialogBinding, FoodShopViewModel>() {

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
        return R.layout.fragment_cash_top_up_dialog
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
        binding.viewModel = viewModel
        viewModel.setPaymentStrategy(CashPaymentStrategy())
        // Reset deposit
        viewModel.inputTopUpDeposit(KeyPad.KeyPadValue.DELETE_ALL)
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.btnFinish.setOnClickDebounce {
            findNavController().navigate(R.id.topupProcessFragment)
        }

        setupKeypadViewListener()

        val radios = binding.topUpPaymentRadioGroup.children.flatMap { row ->
            (row as TableRow).children
        }
        radios.forEach { cell ->
            (cell as ToggleButton).setOnCheckedChangeListener { view, isChecked ->
                Timber.i(isChecked.toString())
                if (isChecked) {
                    radios.filter { it != view }.forEach {
                        (it as ToggleButton).isChecked = false
                    }
                    Timber.i("cashTopUpFragmentDialog payment method: %d", view.id)
                    when (view.id) {
                        R.id.radio_house_money -> viewModel.setTopupMethod(PAYMENT_HOUSE_MONEY)
                        R.id.radio_emoney -> viewModel.setTopupMethod(PAYMENT_ELECTRONIC_MONEY)
                        R.id.radio_cash -> viewModel.setTopupMethod(PAYMENT_CASH)
                        R.id.radio_credit_card -> viewModel.setTopupMethod(PAYMENT_CREDIR_CARD)
                        R.id.radio_qr -> viewModel.setTopupMethod(PAYMENT_QR)
                        R.id.radio_other -> viewModel.setTopupMethod(PAYMENT_OTHERS)
                    }
                } else {
                    viewModel.setTopupMethod(null)
                }
            }
        }
    }
//        forEach { row ->
//            (row as TableRow).children.forEach { cell ->
//                (cell as ToggleButton).setOnCheckedChangeListener { view, isChecked ->
//                    Timber.i(isChecked.toString())
//                    if (isChecked) {
//                        row.children.filter { it != view }.forEach {
//                            (it as ToggleButton).isChecked = false
//                        }
//                        when (view.id) {
//                            R.id.radio_emoney -> viewModel.setTopupMethod(TOP_UP_HOUSE_MONEY)
//                            R.id.radio_cash -> viewModel.setTopupMethod(TOP_UP_CASH)
//                            R.id.radio_credit_card -> viewModel.setTopupMethod(TOP_UP_CREDIR_CARD)
//                            R.id.radio_qr -> viewModel.setTopupMethod(TOP_UP_QR)
//                            R.id.radio_account_transfer -> viewModel.setTopupMethod(TOP_UP_ACCOUNT)
//                        }
//                    } else {
//                        viewModel.setTopupMethod(null)
//                    }
//                }
//            }
//        }
//    }

    /**
     * Setup keypad view listener
     *
     */
    private fun setupKeypadViewListener() {
        binding.tableNumpad.children.forEach { row ->
            (row as TableRow).children.forEach { cell ->
                cell.setOnClickListener {
                    viewModel.inputTopUpDeposit(getKeyPadValue(it.id))
                }
            }
        }

        binding.btnDel.setOnLongClickListener {
            viewModel.inputTopUpDeposit(KeyPad.KeyPadValue.DELETE_ALL)
            true
        }
    }

}