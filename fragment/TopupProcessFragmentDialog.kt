package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentTopupProcessBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class TopupProcessFragmentDialog :
    BaseFragmentDialog<FragmentTopupProcessBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_topup_process
    }

    override fun afterBinding() {
        binding.viewModel = viewModel
        viewModel.startHandlerTopUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopHandlerTopUp()
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.topupFragment)
        }
    }
}