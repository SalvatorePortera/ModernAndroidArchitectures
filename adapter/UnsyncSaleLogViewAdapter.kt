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

/**
 * Unsync sale log view adapter
 *
 * @property viewModel
 * コンストラクタ
 *
 * @param context
 */
class UnsyncSaleLogViewAdapter(
    context: Context,
    val viewModel: SettingMasterViewModel
) : ListAdapter<UnsyncLog, UnsyncSaleLogViewAdapter.ViewHolder>(SaLeLogDiffCallBack()) {

    /**
     * Context
     */
    private val context: Context

    init {
        this.context = context
    }

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(viewModel, parent)
    }

    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * View holder
     *
     * @property binding
     * コンストラクタ  View holder
     */
    class ViewHolder private constructor(val binding: RecyclerviewSaleLogItemLayoutBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        /**
         * Bind
         *
         * @param item
         */
        fun bind(item: UnsyncLog) {
            binding.unsyncLog = item
            binding.executePendingBindings()
        }

        companion object {

            /**
             * From
             *
             * @param viewModel
             * @param parent
             * @return
             */
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

/**
 * Sa le log diff call back
 *
 * コンストラクタ  Sa le log diff call back
 */
class SaLeLogDiffCallBack : DiffUtil.ItemCallback<UnsyncLog>() {

    /**
     * Are items the same
     *
     * @param oldItem
     * @param newItem
     * @return
     */
    override fun areItemsTheSame(
        oldItem: UnsyncLog,
        newItem: UnsyncLog
    ): Boolean {
        return oldItem.receiptCode == newItem.receiptCode
    }

    /**
     * Are contents the same
     *
     * @param oldItem
     * @param newItem
     * @return
     */
    override fun areContentsTheSame(
        oldItem: UnsyncLog,
        newItem: UnsyncLog
    ): Boolean {
        return oldItem == newItem
    }
}

/**
 * Unsync log click callback
 *
 * コンストラクタ  Unsync log click callback
 */
internal interface UnsyncLogClickCallback {
    fun onClick(saleLog: UnsyncLog?)
}