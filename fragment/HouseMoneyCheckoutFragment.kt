package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentCheckoutBinding
import com.nereus.craftbeer.model.payment.EMoneyPayment
import com.nereus.craftbeer.model.payment.PointPlusPaymentStrategy
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

/**
 * House money checkout fragment
 *
 * コンストラクタ  House money checkout fragment
 */
class HouseMoneyCheckoutFragment : BaseFragmentDialog<FragmentCheckoutBinding, FoodShopViewModel>() {

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
        return R.layout.fragment_checkout
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        setDialogSizeScale(0.65, 0.65)
        binding.viewModel = viewModel
        viewModel.setPaymentStrategy(PointPlusPaymentStrategy())

        /*Simulate Card check*/
        if (BuildConfig.SIMULATE_CARD) {
            viewModel.setPayment(
                EMoneyPayment(
                    pointPlusId = BuildConfig.SIMULATE_CARD_MEMBER_CODE,
                    repository = viewModel.pointPlusRepository,
                    cardAuthInfo = BuildConfig.SIMULATE_CARD_AUTH_INFO
                )
            )
        } else viewModel.startHandlerCheckout()
    }

    /**
     * On destroy view
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopHandlerCheckout()
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
//        binding.note.setOnLongClickListener {
//            val pointPlusId = "8900000011240335"
//            val cardAuthInfo = "s30000061789000000112403351110000000000020190000001952650000000000001"
//            viewModel.setPayment(EMoneyPayment(pointPlusId = pointPlusId, repository =  viewModel.pointPlusRepository, cardAuthInfo = cardAuthInfo))
//            true
//        }

        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.paymentMethodFragmentDialog)
        }
    }
}