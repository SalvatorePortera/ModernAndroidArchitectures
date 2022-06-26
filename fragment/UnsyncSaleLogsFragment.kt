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

class UnsyncSaleLogsFragment :
    BaseFragment<FragmentUnsyncSaleLogsBinding, SettingMasterViewModel>() {

    override val viewModel: SettingMasterViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_unsync_sale_logs
    }

    override fun afterBinding() {
        binding.viewModel = viewModel
        configRecyclerView()
        viewModel.loadSaleLogs()
    }

    override fun setViewListener() {
        binding.btnBack.setOnClickDebounce {
            findNavController().navigate(R.id.settingMasterMenuFragment)
        }
        binding.btnSend.setOnClickDebounce {
            viewModel.sendSaleLogs()
        }
    }

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

    private fun configRecyclerView() {
        binding.recyclerUnsyncSaleLogList.adapter =
            UnsyncSaleLogViewAdapter(this.requireContext(), viewModel)
        binding.recyclerUnsyncSaleLogList.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }
}