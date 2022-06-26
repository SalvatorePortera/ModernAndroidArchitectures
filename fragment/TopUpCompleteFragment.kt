package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentCheckoutCompleteBinding
import com.nereus.craftbeer.databinding.FragmentTopUpCompleteBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

/**
 * Top up complete fragment
 *
 * @constructor Create empty Top up complete fragment
 */
class TopUpCompleteFragment :
    BaseFragmentDialog<FragmentTopUpCompleteBinding, FoodShopViewModel>() {

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
        return R.layout.fragment_top_up_complete
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        setDialogSizeScale(0.5, 0.5)
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnBack.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }
    }
}