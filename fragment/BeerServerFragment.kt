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


class BeerServerFragment : BaseFragment<ActivityBeerServerFragmentBinding, SettingMasterViewModel>() {

    override val viewModel: SettingMasterViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.activity_beer_server_fragment
    }

    override fun afterBinding() {
        configRecyclerView()
        viewModel.loadBeers()
    }

    override fun setViewListener() {
        binding.btnBack.setOnClickDebounce {
            findNavController().navigate(R.id.settingMasterMenuFragment)
        }
    }
    override fun setViewModelListener() {
        viewModel.beers.observe(viewLifecycleOwner, Observer {beers ->
            binding.recyclerViewBeerServer.visibility = beers.getRecyclerViewVisibility()
            binding.emptyView.visibility = beers.getEmptyViewVisibility()
            (binding.recyclerViewBeerServer.adapter as BeerServerViewAdapter).submitList(beers)
        })
    }

    private fun configRecyclerView() {
        binding.recyclerViewBeerServer.adapter = BeerServerViewAdapter(this.requireContext(), viewModel)
        binding.recyclerViewBeerServer.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }
}