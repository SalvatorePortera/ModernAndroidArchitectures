package com.nereus.craftbeer.adapter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.RecyclerviewBeersLayoutBinding
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.CombinationBeersInfo
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.realm.RealmApplication
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.viewmodel.BeerShopViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.synthetic.main.recyclerview_beers_layout.view.*
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt

var count: Int = 0

var countVideo: Int = 0

lateinit var uri: Uri

private var tabletId: String = ""

var colorList = ArrayList<String>()


class BeerViewAdapter(
    context: Context,
    val viewModel: BeerShopViewModel
) : ListAdapter<CombinationBeersInfo, BeerViewAdapter.ViewHolder>(BeerDiffCallBack()) {
    private val context: Context = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(viewModel, parent)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
        val sharedPreference =
            context.getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
        tabletId = sharedPreference.getString(PREF_DEVICE_CODE, EMPTY_STRING).toString()



        when (viewModel.beers.value!!.size) {
            2 -> {
                holder.itemView.cardViewBeer.layoutParams.width = 940
            }
            3 -> {
                holder.itemView.cardViewBeer.layoutParams.width = 619
            }
            1 -> {
                holder.itemView.cardViewBeer.layoutParams.width = 1894
            }
        }
    }

    class ViewHolder private constructor(val binding: RecyclerviewBeersLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CombinationBeersInfo, viewModel: BeerShopViewModel) {
            Timber.i("============= bind")
            Timber.i(item.toString())
            binding.beer = item


            val remainAmount = binding.beer?.remainingAmount
            val limitAmount = binding.beer?.outStandardDisplay

            val beerPercentRemain =
                (((binding.beer?.remainingAmount!!.toDouble()) / (binding.beer?.originalAmount!!.toDouble())) * 100).roundToInt()

            viewModel.setBeerPercentage(beerPercentRemain)

            binding.txtAmountRemain.text = viewModel.beerPercentage.value.toString() + "%"

            if (colorList.size == viewModel.beers.value!!.size) {
                if (colorList[countVideo] == "yellow") {
                    binding.videoView.setBackgroundResource(R.drawable.beer_bg_video)
                } else if (colorList[countVideo] == "red") {
                    binding.videoView.setBackgroundResource(R.drawable.red_bg)
                } else if (colorList[countVideo] == "black") {
                    binding.videoView.setBackgroundResource(R.drawable.black_bg)
                } else if (colorList[countVideo] == "white") {
                    binding.videoView.setBackgroundResource(R.drawable.white_bg)
                } else if (colorList[countVideo] == "orange") {
                    binding.videoView.setBackgroundResource(R.drawable.orange_bg)
                } else if (colorList[countVideo] == "sour") {
                    binding.videoView.setBackgroundResource(R.drawable.sour)
                } else if (colorList[countVideo] == "highball") {
                    binding.videoView.setBackgroundResource(R.drawable.highball)
                }
                if (countVideo < colorList.size - 1) {
                    countVideo += 1
                } else if (countVideo == colorList.size - 1) {
                    countVideo = 0
                }
            }

            if (binding.beer?.region.isNullOrBlank()) {
                binding.layoutRegion.visibility = View.GONE
            } else {
                binding.layoutRegion.visibility = View.VISIBLE
            }



            when (beerPercentRemain) {
                0 -> {
                    binding.imageView20.setImageResource(R.drawable.b0)
                }
                in 1..10 -> {
                    binding.imageView20.setImageResource(R.drawable.b10)
                }
                in 11..20 -> {
                    binding.imageView20.setImageResource(R.drawable.b20)
                }
                in 21..30 -> {
                    binding.imageView20.setImageResource(R.drawable.b30)
                }
                in 31..40 -> {
                    binding.imageView20.setImageResource(R.drawable.b40)
                }
                in 41..50 -> {
                    binding.imageView20.setImageResource(R.drawable.b50)
                }
                in 51..60 -> {
                    binding.imageView20.setImageResource(R.drawable.b60)
                }
                in 61..70 -> {
                    binding.imageView20.setImageResource(R.drawable.b70)
                }
                in 71..80 -> {
                    binding.imageView20.setImageResource(R.drawable.b80)
                }
                in 81..90 -> {
                    binding.imageView20.setImageResource(R.drawable.b90)
                }
                else -> {
                    binding.imageView20.setImageResource(R.drawable.b100)
                }
            }

            if (remainAmount != null) {
                if (remainAmount <= limitAmount!!) {
                    binding.btnSelectBeer.text = getStringResource(R.string.lessBeer)
                    binding.btnSelectBeer.setTextColor(Color.RED)
                    binding.imgSmallBeer.visibility = View.INVISIBLE
                    binding.txtBuy.visibility = View.INVISIBLE
                    binding.txtAmountRemain.setTextColor(Color.RED)
                    binding.btnSelectBeer.isEnabled = false
                    binding.txtMoreInfo.isEnabled = false
                    binding.txtMoreInfo.visibility = View.INVISIBLE
                    viewModel.setException(
                        MessageException(
                            MessagesModel(
                                ErrorLogCode.EB001,
                                obnizId = binding.beer?.obnizId,
                                coreMsgArgs = listOf(tabletId)
                            )
                        )
                    )
                } else {
                    binding.btnSelectBeer.text = ""
                    binding.txtBuy.visibility = View.VISIBLE
                    binding.imgSmallBeer.visibility = View.VISIBLE
                    binding.txtAmountRemain.setTextColor(Color.WHITE)
                    binding.btnSelectBeer.isEnabled = true
                    binding.txtMoreInfo.isEnabled = true
                    binding.txtMoreInfo.visibility = View.VISIBLE
                }
            }
            var isSelling = binding.beer?.maintainFlag

            if (isSelling == 1) {
                binding.btnSelectBeer.text = getStringResource(R.string.outofservice)
                binding.btnSelectBeer.background.setTint(Color.parseColor("#DCDCDC"))
                binding.btnSelectBeer.setTextColor(Color.RED)
                binding.imgSmallBeer.visibility = View.INVISIBLE
                binding.txtBuy.visibility = View.INVISIBLE
                binding.btnSelectBeer.isEnabled = false
                binding.txtMoreInfo.isEnabled = false
                binding.txtMoreInfo.visibility = View.INVISIBLE
            } else {
                binding.btnSelectBeer.background.setTint(Color.parseColor("#FFFFFF"))
                binding.btnSelectBeer.text = ""
                binding.txtBuy.visibility = View.VISIBLE
                binding.imgSmallBeer.visibility = View.VISIBLE
                binding.btnSelectBeer.isEnabled = true
                binding.txtMoreInfo.isEnabled = true
                binding.txtMoreInfo.visibility = View.VISIBLE
            }

            val color = binding.beer?.colour

            if(color.equals("sour")||color.equals("highball")){
                    binding.txtBrewery.setTextColor(Color.BLACK)
                    binding.txtBeerName.setTextColor(Color.BLACK)
                    binding.txtBreweryAndBeerName.setTextColor(Color.BLACK)
                    binding.txtSellingPrice.setTextColor(Color.BLACK)
                    binding.txtMoreInfo.setTextColor(Color.BLACK)
                    binding.txtMoreInfo.setBackgroundResource(R.drawable.custombutton_black)

            } else {
                binding.txtBrewery.setTextColor(Color.WHITE)
                binding.txtBeerName.setTextColor(Color.WHITE)
                binding.txtBreweryAndBeerName.setTextColor(Color.WHITE)
                binding.txtSellingPrice.setTextColor(Color.WHITE)
                binding.txtMoreInfo.setTextColor(Color.WHITE)
                binding.txtMoreInfo.setBackgroundResource(R.drawable.custombutton_white)
            }


            if (colorList.size < viewModel.beers.value!!.size) {
                colorList.add(color.toString())
            }
            // Map video as beer's colour
            val filename = File(
                RealmApplication.instance.getExternalFilesDir(null)?.absolutePath.toString() + "/DownloadedVideo/",
                "$color.mp4"
            )
            uri = Uri.fromFile(filename)


            binding.videoView.setVideoURI(uri)
            binding.videoView.start()
            // Make video looping infinity
            binding.videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.videoView.setBackgroundColor(Color.TRANSPARENT)
                }, 500)
            }


            // Load Images
            if (!item.flagUrl.isNullOrBlank()) {
                Picasso.get().load(item.flagUrl).into(binding.imgFlag)
            }
            if (!item.imageUrl.isNullOrBlank()) {
                Picasso.get().load(item.imageUrl).into(binding.imgBeerIcon)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(viewModel: BeerShopViewModel, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RecyclerviewBeersLayoutBinding.inflate(layoutInflater, parent, false)
                binding.callback = object : BeerClickCallBack {

                    override fun onClick(beer: CombinationBeersInfo) {

                        viewModel.setSelectedBeer(beer)



                        var count = 0
                        for (item in parent.children) {
                            when (colorList[count]) {
                                "yellow" -> {
                                    item.videoView.setBackgroundResource(R.drawable.beer_bg_video)
                                }
                                "red" -> {
                                    item.videoView.setBackgroundResource(R.drawable.red_bg)
                                }
                                "black" -> {
                                    item.videoView.setBackgroundResource(R.drawable.black_bg)
                                }
                                "white" -> {
                                    item.videoView.setBackgroundResource(R.drawable.white_bg)
                                }
                                "orange" -> {
                                    item.videoView.setBackgroundResource(R.drawable.orange_bg)
                                }
                                "sour" -> {
                                    item.videoView.setBackgroundResource(R.drawable.sour)
                                }
                                "highball" -> {
                                    item.videoView.setBackgroundResource(R.drawable.highball)
                                }
                            }
                            count += 1
                        }
                        parent.findNavController()
                            .navigate(R.id.beerPaymentFragment)
                    }

                    override fun onClickMoreInfo(beer: CombinationBeersInfo) {
                        viewModel.setSelectedBeer(beer)

                        var count = 0
                        for (item in parent.children) {
                            when (colorList[count]) {
                                "yellow" -> {
                                    item.videoView.setBackgroundResource(R.drawable.beer_bg_video)
                                }
                                ("red") -> {
                                    item.videoView.setBackgroundResource(R.drawable.red_bg)
                                }
                                ("black") -> {
                                    item.videoView.setBackgroundResource(R.drawable.black_bg)
                                }
                                ("white") -> {
                                    item.videoView.setBackgroundResource(R.drawable.white_bg)
                                }
                                ("orange") -> {
                                    item.videoView.setBackgroundResource(R.drawable.orange_bg)
                                }
                            }
                            count += 1
                        }


                        parent.findNavController()
                            .navigate(R.id.moreInfoFragment)

                    }

                    override fun onClickSecretBackIcon(beer: CombinationBeersInfo) {
                        viewModel.setSelectedBeer(beer)
                        count++
                        if (count == 5) {
                            val bundle =
                                bundleOf(SELECT_SCREEN_EXTRA_KEY to SELECT_SCREEN_EXTRA_SELECT_MODE)
                            parent.findNavController()
                                .navigate(R.id.passwordTabletActivity, bundle)
                            count = 0
                        }
                    }
                }
                return ViewHolder(binding)
            }
        }
    }
}

internal interface BeerClickCallBack {
    fun onClick(beer: CombinationBeersInfo)
    fun onClickMoreInfo(beer: CombinationBeersInfo)
    fun onClickSecretBackIcon(beer: CombinationBeersInfo)
}







