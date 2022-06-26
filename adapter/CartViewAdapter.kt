package com.nereus.craftbeer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.databinding.RecyclerviewSelectedItemLayoutBinding
import com.nereus.craftbeer.model.CombinationGoodsInfo
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

/**
 * Cart view adapter
 *
 * @property viewModel
 * @constructor
 *
 * @param context
 */
class CartViewAdapter(
    context: Context,
    val viewModel: FoodShopViewModel
) : ListAdapter<CombinationGoodsInfo, CartViewAdapter.ViewHolder>(FoodDiffCallBack()) {
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
     * Handle recycler item click
     * Click on RecyclerView Item.
     * @param recyclerView
     * @param itemView
     */
    private fun handleRecyclerItemClick(recyclerView: RecyclerView, itemView: View) {
        val itemPosition = recyclerView.getChildLayoutPosition(itemView)
        val food = getItem(itemPosition)
        displayToast(context, food.goodsName, Toast.LENGTH_LONG)
    }

    /**
     * View holder
     *
     * @property binding
     * @constructor Create empty View holder
     */
    class ViewHolder private constructor(val binding: RecyclerviewSelectedItemLayoutBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        /**
         * Bind
         *
         * @param item
         */
        fun bind(item: CombinationGoodsInfo) {
            binding.food = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(viewModel: FoodShopViewModel, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RecyclerviewSelectedItemLayoutBinding.inflate(layoutInflater, parent, false)
                binding.callback = object : CartClickCallback {
                    override fun onClickPayment() {
                        TODO("Not yet implemented")
                    }

                    override fun onRemove(food: CombinationGoodsInfo?) {
                        food?.let { viewModel.removeFood(it) }
                    }
                }
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Cart click callback
 *
 * @constructor Create empty Cart click callback
 */
internal interface CartClickCallback {
    fun onClickPayment()
    fun onRemove(food: CombinationGoodsInfo?)
}