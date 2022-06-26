package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentCheckoutCompleteBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class CheckoutCompleteFragment :
    BaseFragmentDialog<FragmentCheckoutCompleteBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_checkout_complete
    }

    override fun afterBinding() {
        setDialogSizeScale(0.5, 0.5)
    }
    override fun setViewListener() {
        binding.btnConfirm.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }
    }
}