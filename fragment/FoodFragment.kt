package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.FoodViewAdapter
import com.nereus.craftbeer.databinding.ActivityFoodFragmentBinding
import com.nereus.craftbeer.util.getEmptyViewVisibility
import com.nereus.craftbeer.util.getRecyclerViewVisibility
import com.nereus.craftbeer.viewmodel.BaseViewModel
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class FoodFragment : BaseFragment<ActivityFoodFragmentBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.activity_food_fragment
    }

    override fun afterBinding() {
        binding.viewModel = viewModel
        configRecyclerView()
    }

    override fun setViewModelListener() {
        this.viewModel.foods.observe(binding.lifecycleOwner!!, Observer { foods ->
            binding.recyclerViewFoodList.visibility = foods.getRecyclerViewVisibility()
            binding.emptyView.visibility = foods.getEmptyViewVisibility()

            (binding.recyclerViewFoodList.adapter as FoodViewAdapter).submitList(
                ArrayList(
                    foods
                )
            )
        })
        this.viewModel.processingItemPosition.observe(binding.lifecycleOwner!!, Observer {
            binding.recyclerViewFoodList.smoothScrollToPosition(it)
        })
    }

    private fun configRecyclerView() {
        binding.recyclerViewFoodList.adapter =
            FoodViewAdapter(this.requireContext(), viewModel)
        binding.recyclerViewFoodList.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }
}