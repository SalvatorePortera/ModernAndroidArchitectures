package com.nereus.craftbeer.fragment

import android.widget.TableRow
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentFreePriceInputDialogBinding
import com.nereus.craftbeer.model.keyboard.KeyPad
import com.nereus.craftbeer.model.payment.CashPayment
import com.nereus.craftbeer.model.payment.CashPaymentStrategy
import com.nereus.craftbeer.model.totalPriceWithoutTax
import com.nereus.craftbeer.model.totalTax
import com.nereus.craftbeer.model.updatePrice
import com.nereus.craftbeer.util.keyboard.getKeyPadValue
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import kotlin.math.roundToInt

class FreePriceInputFragmentDialog :
    BaseFragmentDialog<FragmentFreePriceInputDialogBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_free_price_input_dialog
    }

    override fun afterBinding() {
        setDialogSizeScale(0.9, 0.9)
        binding.viewModel = viewModel
        // viewModel.setPaymentStrategy(CashPaymentStrategy())
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            viewModel.resetFreeAmount()
            findNavController().navigate(R.id.goodsInputFragment)
        }

        binding.btnFinish.setOnClickDebounce {
            // このタイミングでリストを取得し、金額をセットする？
            // それかアイテム側にセットする？
            val newList = viewModel.foods.value?.toMutableList()
            newList?.updatePrice( viewModel.freeAmount.value!!.toDouble() )
            viewModel.holdCart()
            viewModel.holdCart()
            //if (it.goodsCode == "VARIOUS") {
            //    it.updateSellingPrice(_freeAmount.value!!.toDouble())
            //}
            viewModel.resetFreeAmount()
            findNavController().navigate(R.id.foodShopFragment)
        }

        setupKeypadViewListener()
    }

    private fun setupKeypadViewListener() {

        binding.tableNumpad.children.forEach { row ->
            (row as TableRow).children.forEach { cell ->
                cell.setOnClickListener {
                    viewModel.inputFreeAmount(getKeyPadValue(it.id))
                }
            }
        }

        binding.btnDel.setOnLongClickListener {
            viewModel.inputFreeAmount(KeyPad.KeyPadValue.DELETE_ALL)
            true
        }
    }

}