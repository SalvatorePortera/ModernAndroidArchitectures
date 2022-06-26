package com.nereus.craftbeer.fragment

import android.widget.TableRow
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentTopupBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.keyboard.KeyPad
import com.nereus.craftbeer.util.keyboard.getKeyPadValue
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import java.math.BigInteger

class TopupInputFragment : BaseFragmentDialog<FragmentTopupBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()
    override fun getLayout(): Int {
        return R.layout.fragment_topup
    }

    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
        binding.viewModel = viewModel
        // Reset top-up amount
        viewModel.inputTopUpAmount(KeyPad.KeyPadValue.DELETE_ALL)
    }

    override fun setViewListener() {
        binding.btnCharge.setOnClickDebounce {
//            viewModel.setTopupAmount()
            val price = viewModel.topUpAmount.value!!
            // validate inputs
            if (price == 0) {
                binding.txtInputTopUp.error = getString(R.string.msg_no_price_input)
                this.viewModel.setException(MessageException(MessagesModel(R.string.msg_no_amount_input)))
                return@setOnClickDebounce
            }
            findNavController().navigate(R.id.cashTopUpFragmentDialog)
        }

        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)

        }

        setupKeypadViewListener()
    }

    private fun setupKeypadViewListener() {
        binding.tableNumpad.children.forEach { row ->
            (row as TableRow).children.forEach { cell ->
                cell.setOnClickListener {
                    viewModel.inputTopUpAmount(getKeyPadValue(it.id))
                }
            }
        }

        binding.btnDel.setOnLongClickListener {
            viewModel.inputTopUpAmount(KeyPad.KeyPadValue.DELETE_ALL)
            true
        }
    }
}