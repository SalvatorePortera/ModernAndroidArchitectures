package com.nereus.craftbeer.fragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.UnsyncSaleLogViewAdapter
import com.nereus.craftbeer.databinding.FragmentUnsyncSaleLogsBinding
import com.nereus.craftbeer.util.getEmptyViewVisibility
import com.nereus.craftbeer.util.getRecyclerViewVisibility
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.BaseViewModel
import com.nereus.craftbeer.viewmodel.SettingMasterViewModel

/**
 * Unsync sale logs fragment
 *
 * @constructor Create empty Unsync sale logs fragment
 */
class UnsyncSaleLogsFragment :
    BaseFragment<FragmentUnsyncSaleLogsBinding, SettingMasterViewModel>() {

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
        return R.layout.fragment_unsync_sale_logs
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        binding.viewModel = viewModel
        configRecyclerView()
        viewModel.loadSaleLogs()
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnBack.setOnClickDebounce {
            findNavController().navigate(R.id.settingMasterMenuFragment)
        }
        binding.btnSend.setOnClickDebounce {
            viewModel.sendSaleLogs()
        }
    }

    /**
     * Set view model listener
     *
     */
    override fun setViewModelListener() {
        this.viewModel.unsyncSaleLogs.observe(binding.lifecycleOwner!!, Observer { saleLogs ->
            binding.recyclerUnsyncSaleLogList.visibility = saleLogs.getRecyclerViewVisibility()
            binding.emptyView.visibility = saleLogs.getEmptyViewVisibility()

            (binding.recyclerUnsyncSaleLogList.adapter as UnsyncSaleLogViewAdapter).submitList(
                ArrayList(
                    saleLogs
                )
            )
        })
    }

    /**
     * Config recycler view
     *
     */
    private fun configRecyclerView() {
        binding.recyclerUnsyncSaleLogList.adapter =
            UnsyncSaleLogViewAdapter(this.requireContext(), viewModel)
        binding.recyclerUnsyncSaleLogList.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }
}