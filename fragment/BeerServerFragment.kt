package com.nereus.craftbeer.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.BeerServerViewAdapter
import com.nereus.craftbeer.databinding.ActivityBeerServerFragmentBinding
import com.nereus.craftbeer.util.getEmptyViewVisibility
import com.nereus.craftbeer.util.getRecyclerViewVisibility
import com.nereus.craftbeer.viewmodel.SettingMasterViewModel
import com.nereus.craftbeer.util.setOnClickDebounce


/**
 * Beer server fragment
 *
 * @constructor  Beer server fragment
 */
class BeerServerFragment : BaseFragment<ActivityBeerServerFragmentBinding, SettingMasterViewModel>() {

    /**
     * View model
     */
    override val viewModel: SettingMasterViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_beer_server_fragment
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        configRecyclerView()
        viewModel.loadBeers()
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnBack.setOnClickDebounce {
            findNavController().navigate(R.id.settingMasterMenuFragment)
        }
    }

    /**
     * Set view model listener
     *
     */
    override fun setViewModelListener() {
        viewModel.beers.observe(viewLifecycleOwner, Observer {beers ->
            binding.recyclerViewBeerServer.visibility = beers.getRecyclerViewVisibility()
            binding.emptyView.visibility = beers.getEmptyViewVisibility()
            (binding.recyclerViewBeerServer.adapter as BeerServerViewAdapter).submitList(beers)
        })
    }

    /**
     * Config recycler view
     *
     */
    private fun configRecyclerView() {
        binding.recyclerViewBeerServer.adapter = BeerServerViewAdapter(this.requireContext(), viewModel)
        binding.recyclerViewBeerServer.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }
}