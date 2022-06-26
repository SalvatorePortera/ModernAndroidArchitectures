package com.nereus.craftbeer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.database.entity.SaleLog
import com.nereus.craftbeer.databinding.RecyclerviewSaleLogItemLayoutBinding
import com.nereus.craftbeer.model.UnsyncLog
import com.nereus.craftbeer.viewmodel.SettingMasterViewModel

class UnsyncSaleLogViewAdapter(
    context: Context,
    val viewModel: SettingMasterViewModel
) : ListAdapter<UnsyncLog, UnsyncSaleLogViewAdapter.ViewHolder>(SaLeLogDiffCallBack()) {
    private val context: Context

    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(viewModel, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder private constructor(val binding: RecyclerviewSaleLogItemLayoutBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: UnsyncLog) {
            binding.unsyncLog = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(viewModel: SettingMasterViewModel, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RecyclerviewSaleLogItemLayoutBinding.inflate(layoutInflater, parent, false)
                binding.callback = object : UnsyncLogClickCallback {
                    override fun onClick(unsyncLog: UnsyncLog?) {
                        TODO("Not yet implemented")
                    }
                }
                return ViewHolder(binding)
            }
        }
    }
}

class SaLeLogDiffCallBack : DiffUtil.ItemCallback<UnsyncLog>() {
    override fun areItemsTheSame(
        oldItem: UnsyncLog,
        newItem: UnsyncLog
    ): Boolean {
        return oldItem.receiptCode == newItem.receiptCode
    }

    override fun areContentsTheSame(
        oldItem: UnsyncLog,
        newItem: UnsyncLog
    ): Boolean {
        return oldItem == newItem
    }
}

internal interface UnsyncLogClickCallback {
    fun onClick(saleLog: UnsyncLog?)
}