package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentCheckoutCompleteBinding
import com.nereus.craftbeer.databinding.FragmentTopUpCompleteBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class TopUpCompleteFragment :
    BaseFragmentDialog<FragmentTopUpCompleteBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_top_up_complete
    }

    override fun afterBinding() {
        setDialogSizeScale(0.5, 0.5)
    }
    override fun setViewListener() {
        binding.btnBack.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }
    }
}