package com.nereus.craftbeer.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.R

/**
 * Cart view holder
 *
 * コンストラクタ
 *
 * @param itemView
 */
class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var foodNameView: TextView
    var priceView: TextView


    init {
        foodNameView =
            itemView.findViewById<View>(R.id.txtCartFoodName) as TextView
        priceView =
            itemView.findViewById<View>(R.id.txtCartPrice) as TextView
    }
}