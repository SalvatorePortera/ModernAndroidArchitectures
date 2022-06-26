﻿package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.PrinterLogViewAdapter
import com.nereus.craftbeer.databinding.FragmentCheckBalanceDialogBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.printer.hasAnySelected
import com.nereus.craftbeer.util.getEmptyViewVisibility
import com.nereus.craftbeer.util.getRecyclerViewVisibility
import com.nereus.craftbeer.util.livedata.EventObserver
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class CheckBalanceFragmentDialog :
    BaseFragmentDialog<FragmentCheckBalanceDialogBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()
    override fun getLayout(): Int {
        return R.layout.fragment_check_balance_dialog
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }
    }

}