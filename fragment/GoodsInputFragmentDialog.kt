package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentGoodsInputBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel


/**
 * Goods input fragment
 *
 * コンストラクタ  Goods input fragment
 */
class GoodsInputFragment : BaseFragmentDialog<FragmentGoodsInputBinding, FoodShopViewModel>() {

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
        return R.layout.fragment_goods_input
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        binding.viewModel = viewModel
        this.viewModel.resetGoodsInput()
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            viewModel.resetGoodsInput()
            // dismiss()
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.btnAdd.setOnClickDebounce {
//                setBarcode(binding.txtBarcode.text.toString())
//                setProductCd(binding.txtProductCd.text.toString())
            val barcode = binding.txtBarcode.text.toString().trim()
            val productCode = binding.txtProductCd.text.toString().trim()
//            try {

            if (barcode.isNotBlank()) {
                viewModel.handleBarcode(barcode)
            } else if (productCode.isNotBlank()) {
                viewModel.handleProductCode(productCode)
            }
//            } catch ()

            // Should delay for smooth scroll to new added item
//            Handler().postDelayed({
//                this.viewModel.addNewFood()
//            }, 200L)

            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.btnFree.setOnClickDebounce {
            val barcode = "0000000000000"

            viewModel.handleBarcode(barcode)

            findNavController().navigate(R.id.freePriceInputFragmentDialog)
        }
    }
}