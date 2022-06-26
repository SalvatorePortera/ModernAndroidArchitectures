package com.nereus.craftbeer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.databinding.RecyclerviewPrinterLogItemLayoutBinding
import com.nereus.craftbeer.model.printer.Receipt
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class PrinterLogViewAdapter(
    context: Context,
    val viewModel: FoodShopViewModel
) : ListAdapter<Receipt, PrinterLogViewAdapter.ViewHolder>(PrinterLogDiffCallBack()) {
    private val context: Context = context

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
     * On view recycled
     *
     * @param holder
     */
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
    }

    /**
     * View holder
     *
     * @property binding
     * @constructor Create empty View holder
     */
    class ViewHolder private constructor(val binding: RecyclerviewPrinterLogItemLayoutBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: Receipt) {
            binding.receipt = item
            binding.cbSelected.isChecked = item.isSelected
            binding.executePendingBindings()
        }

        companion object {
            fun from(viewModel: FoodShopViewModel, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RecyclerviewPrinterLogItemLayoutBinding.inflate(layoutInflater, parent, false)
                binding.callback = object : PrinterLogClickCallback {
                    override fun onClickPayment() {
                        TODO("Not yet implemented")
                    }

                    override fun onSelect(receipt: Receipt?) {
                        receipt?.let {
                            it.isSelected = !it.isSelected
                            viewModel.selectReceiptHistory(receipt)
                        }

                    }

                    override fun onRemove(receipt: Receipt?) {
//                        receipt?.let { viewModel.removeFood(it) }
                    }
                }
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Printer log click callback
 *
 * @constructor Create empty Printer log click callback
 */
internal interface PrinterLogClickCallback {
    fun onClickPayment()
    fun onSelect(receipt: Receipt?)
    fun onRemove(receipt: Receipt?)
}

/**
 * Printer log diff call back
 *
 * @constructor Create empty Printer log diff call back
 */
class PrinterLogDiffCallBack : DiffUtil.ItemCallback<Receipt>() {
    override fun areItemsTheSame(
        oldItem: Receipt,
        newItem: Receipt
    ): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Are contents the same
     *
     * @param oldItem
     * @param newItem
     * @return
     */
    override fun areContentsTheSame(
        oldItem: Receipt,
        newItem: Receipt
    ): Boolean {
        return oldItem == newItem
    }
}