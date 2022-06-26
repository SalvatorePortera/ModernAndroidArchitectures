package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.ActivityCartFragmentBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import timber.log.Timber

class CartFragment : BaseFragment<ActivityCartFragmentBinding, FoodShopViewModel>() {

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
        return R.layout.activity_cart_fragment
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        binding.viewModel = viewModel
//        configRecyclerView()
        setFragmentResultListener(ConfirmFragmentDialog.REQUEST_KEY) { key, bundle ->
            Timber.i("key %s", key)
            Timber.i("value %s", bundle.getBoolean(ConfirmFragmentDialog.CONFIRMATION_RESULT).toString())

            if (bundle.getBoolean(key)) {
                viewModel.clearCart()
            }
        }
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnCheckout.setOnClickDebounce {
            Timber.i("--------------aaaaaaaaaaaaaaaaa")
            findNavController().navigate(R.id.paymentMethodFragmentDialog)
        }
        binding.btnCustomerBase.setOnClickDebounce {
            findNavController().navigate(R.id.customerAttributeFragmentdialog)
        }
        binding.btnTakeOutSelect.setOnClickDebounce {
            findNavController().navigate(R.id.takeOutSelectFragmentdialog)
        }
        binding.clear.setOnClickDebounce {
//            setFragmentResultListener(ConfirmFragmentDialog.REQUEST_KEY) { key, bundle ->
//                Timber.i("key %s", key)
//                Timber.i("value %s", bundle.getBoolean(ConfirmFragmentDialog.CONFIRMATION_RESULT).toString())
//
//                if (bundle.getBoolean(key)) {
//                    viewModel.clearCart()
//                }
//            }
            viewModel.clearCart()
            // TODO Confirmation Dialog
//            findNavController().navigate(R.id.confirmFragmentDialog)
        }

        binding.pause.setOnClickDebounce {
            viewModel.holdCart()
        }
    }

    /**
     * Set view model listener
     *
     */
    override fun setViewModelListener() {

        this.viewModel.foods.observe(binding.lifecycleOwner!!, Observer { foods ->
//            binding.recyclerView.visibility = foods.getRecyclerViewVisibility()
//            binding.emptyView.visibility = foods.getEmptyViewVisibility()
//
//            (binding.recyclerView.adapter as CartViewAdapter).submitList(foods)
            binding.btnCheckout.isEnabled = foods.isNotEmpty()
        })
//        this.viewModel.processingItemPosition.observe(binding.lifecycleOwner!!, Observer {
//            binding.recyclerView.smoothScrollToPosition(it)
//        })
    }
//
//    private fun configRecyclerView() {
//        binding.recyclerView.adapter = CartViewAdapter(this.requireContext(), viewModel)
//        binding.recyclerView.layoutManager =
//            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
//    }
}