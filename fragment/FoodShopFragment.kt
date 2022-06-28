package com.nereus.craftbeer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.SelectModeActivity
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.FoodShopFragmentBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel


/**
 * Food shop fragment
 *
 * コンストラクタ  Food shop fragment
 */
class FoodShopFragment : BaseFragment<FoodShopFragmentBinding, FoodShopViewModel>() {

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
        return R.layout.food_shop_fragment
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        binding.viewModel = viewModel
    }

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(R.id.frCartLayout, CartFragment())
                replace(R.id.goods_list_container, FoodFragment())
                setReorderingAllowed(true)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.apply {
            btnManualInput.setOnClickDebounce {
                findNavController().navigate(R.id.goodsInputFragment)
            }
            btnPrinter.setOnClickDebounce {
                findNavController().navigate(R.id.receiptPrinterSelectModeFragmentDialog)
            }
            btnTopUp.setOnClickDebounce {
                findNavController().navigate(R.id.topupFragment)
            }
            btnBalanceInquiry.setOnClickDebounce {
                findNavController().navigate(R.id.checkBalanceFragmentDialog)
            }
            btnBack.setOnClickDebounce {
                startActivity(Intent(requireContext(), SelectModeActivity::class.java))
                // Connect to socket that holds the tablet status
//                connectAliveSocket()
                requireActivity().finish()
            }

        }

    }
}