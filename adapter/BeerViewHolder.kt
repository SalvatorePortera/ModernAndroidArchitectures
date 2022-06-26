package com.nereus.craftbeer.adapter

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.R

class BeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var flagView: ImageView
    var beerIconView: ImageView
    var breweryNameView: TextView
    var priceView: TextView
    var selectButtonView: Button
    var moreInfoButtonView: TextView
    var beerNameView: TextView
    var breweryAndBeerNameView: TextView
    var alcView: TextView
    var ibuView: TextView
    var styleView: TextView
    var countryCodeView: TextView
    var beerStyleView: TextView
    var beerRegionView: TextView
    var beerSelectButtonView: Button
    var videoPlayerView : VideoView

    init {
        flagView =
            itemView.findViewById<View>(R.id.imgFlag) as ImageView
        beerIconView =
            itemView.findViewById<View>(R.id.imgBeerIcon) as ImageView
        breweryNameView =
            itemView.findViewById<View>(R.id.txtBrewery) as TextView
        priceView =
            itemView.findViewById<View>(R.id.txtSellingPrice) as TextView
        selectButtonView =
            itemView.findViewById<View>(R.id.btnSelectBeer) as Button
        moreInfoButtonView =
            itemView.findViewById<View>(R.id.txtMoreInfo) as TextView
        beerNameView =
            itemView.findViewById<View>(R.id.txtBeerName) as TextView
        breweryAndBeerNameView =
            itemView.findViewById<View>(R.id.txtBreweryAndBeerName) as TextView
        alcView =
            itemView.findViewById<View>(R.id.txtAlc) as TextView
        ibuView =
            itemView.findViewById<View>(R.id.txtIbu) as TextView
        styleView =
            itemView.findViewById<View>(R.id.txtStyle) as TextView
        countryCodeView =
            itemView.findViewById<View>(R.id.txtCountryCode) as TextView
        beerRegionView =
            itemView.findViewById<View>(R.id.txtRegion) as TextView
        beerStyleView =
            itemView.findViewById<View>(R.id.txtStyle) as TextView
        beerSelectButtonView =
            itemView.findViewById<View>(R.id.btnSelectBeer) as Button
        videoPlayerView =
            itemView.findViewById<View>(R.id.videoView) as VideoView

    }
}