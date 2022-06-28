package com.nereus.craftbeer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.databinding.RecyclerviewItemLayoutBinding
import com.nereus.craftbeer.model.CombinationGoodsInfo
import com.nereus.craftbeer.model.CustomerAttribute
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.loadImage
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

/**
 * Food view adapter
 *
 * @property viewModel
 * コンストラクタ
 *
 * @param context
 */
class FoodViewAdapter(
    context: Context,
    val viewModel: FoodShopViewModel
) : ListAdapter<CombinationGoodsInfo, FoodViewAdapter.ViewHolder>(FoodDiffCallBack()) {
    private val context: Context

    init {
        this.context = context
    }

    /**
     * Submit list
     *
     * @param list
     */
    override fun submitList(list: MutableList<CombinationGoodsInfo>?) {
        super.submitList(list)
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
        holder.binding.imageViewFlag.loadImage(item.imageUrl)
        holder.bind(item, viewModel.customerAttribute.value!!)
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
     * コンストラクタ  View holder
     */
    class ViewHolder private constructor(val binding: RecyclerviewItemLayoutBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        /**
         * Bind
         *
         * @param item
         * @param customerAttribute
         */
        fun bind(item: CombinationGoodsInfo, customerAttribute: CustomerAttribute) {
            binding.food = item
            binding.customerAttribute = customerAttribute
            binding.executePendingBindings()
        }

        companion object {

            fun from(viewModel: FoodShopViewModel, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewItemLayoutBinding.inflate(layoutInflater, parent, false)
                binding.callback = object : FoodClickCallback {

                    override fun onClick(food: CombinationGoodsInfo?) {
                        println("[APP LOG] - onclick")
                    }

                    override fun onRemove(food: CombinationGoodsInfo?) {
                        food?.let { viewModel.removeFood(it) }
                    }

                    override fun onCountUp(food: CombinationGoodsInfo?) {
                        food?.let {
                            println("[APP LOG] onCountUp")
                            viewModel.updateFood(food.copy(quantity = food.quantity + 1))
                        }
                    }

                    override fun onCountDown(food: CombinationGoodsInfo?) {
                        println("[APP LOG] onCountDown")

                        food?.apply {
                            if (quantity > 1) {
                                viewModel.updateFood(food.copy(quantity = food.quantity - 1))
                            } else {
                                viewModel.removeFood(food)
                            }
                        }

                    }
                }
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Food diff call back
 *
 * コンストラクタ  Food diff call back
 */
class FoodDiffCallBack : DiffUtil.ItemCallback<CombinationGoodsInfo>() {
    override fun areItemsTheSame(
        oldItem: CombinationGoodsInfo,
        newItem: CombinationGoodsInfo
    ): Boolean {
        return oldItem.janCode == newItem.janCode
    }

    override fun areContentsTheSame(
        oldItem: CombinationGoodsInfo,
        newItem: CombinationGoodsInfo
    ): Boolean {
        return oldItem == newItem
    }
}

/**
 * Food click callback
 *
 * コンストラクタ  Food click callback
 */
internal interface FoodClickCallback {
    /**
     * On click
     *
     * @param food
     */
    fun onClick(food: CombinationGoodsInfo?)

    /**
     * On remove
     *
     * @param food
     */
    fun onRemove(food: CombinationGoodsInfo?)

    /**
     * On count up
     *
     * @param food
     */
    fun onCountUp(food: CombinationGoodsInfo?)

    /**
     * On count down
     *
     * @param food
     */
    fun onCountDown(food: CombinationGoodsInfo?)
}