package com.nereus.craftbeer.fragment

import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.uri
import com.nereus.craftbeer.chart.RadarMarkerView
import com.nereus.craftbeer.databinding.ActivityMoreInfoFragmentBinding
import com.nereus.craftbeer.realm.RealmApplication
import com.nereus.craftbeer.util.loadImage
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.BeerShopViewModel
import java.io.File

/**
 * More info fragment
 *
 * コンストラクタ  More info fragment
 */
class MoreInfoFragment : BaseFragment<ActivityMoreInfoFragmentBinding, BeerShopViewModel>() {

    /**
     * View model
     */
    override val viewModel: BeerShopViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_more_info_fragment
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        binding.viewModel = viewModel
        viewModel.selected_beer.value?.let {
            binding.imgInfoFlag.loadImage(it.flagUrl)
            binding.imgInfoBeerIcon.loadImage(it.imageUrl)
        }

        if(viewModel.selected_beer.value?.colour.equals("sour")||viewModel.selected_beer.value?.colour.equals("highball")){
            binding.txtInfoBrewery.setTextColor(Color.BLACK)
            binding.txtInfoBeerJpName.setTextColor(Color.BLACK)
            binding.txtInfoBrewAndName.setTextColor(Color.BLACK)
        } else {
            binding.txtInfoBrewery.setTextColor(Color.WHITE)
            binding.txtInfoBeerJpName.setTextColor(Color.WHITE)
            binding.txtInfoBrewAndName.setTextColor(Color.WHITE)
        }

        if(binding.txtInfoRegion.text.isNullOrBlank()){
            binding.txtInfoRegion.visibility = View.GONE
        } else {
            binding.txtInfoRegion.visibility = View.VISIBLE
        }

        setupVideo()
        setupBeerPercentage()
        setUpChart()
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnBack.setOnClickDebounce {
            findNavController().navigate(R.id.beerShopFragment)
        }
        binding.btnInfoSelect.setOnClickDebounce {
            findNavController().navigate(R.id.beerPaymentFragment)
        }
    }


    /**
     * Set up chart
     *
     */
    private fun setUpChart() {

        binding.chart1.apply {

            setBackgroundColor((Color.TRANSPARENT))
            description.isEnabled = false
            isRotationEnabled = false
            webLineWidth = 1f
            webColor = Color.BLACK
            webLineWidthInner = 2f
            webColorInner = Color.LTGRAY
            webAlpha = 100

            val mv: MarkerView = RadarMarkerView(activity, R.layout.radar_markerview)
            mv.chartView = this

            marker = mv

            data = viewModel.getChartData()
            invalidate()

            animateXY(1400, 1400, Easing.EaseInOutQuad)

            xAxis.apply {
                textSize = 15f
                yOffset = 1f
                xOffset = 1f
                valueFormatter = object : ValueFormatter() {
                    private val mActivities =
                        arrayOf("香り-Flavor", "酸味-Sour", "苦味-Bitter", "甘味-Sweet", "重さ-Body")

                    override fun getFormattedValue(value: Float): String {
                        return mActivities[value.toInt() % mActivities.size]
                    }
                }
                textColor = Color.BLACK
            }

            yAxis.apply {
                labelCount = 5
                textSize = 2f
                axisMinimum = 0f
                axisMaximum = 40f
                setDrawLabels(false)
            }

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                xEntrySpace = 7f
                yEntrySpace = 5f
                formSize = 0f
                textColor = Color.BLACK
            }
        }

    }

    /**
     * Setup beer percentage
     *
     */
    private fun setupBeerPercentage() {
        val beerPercentage = viewModel.beerPercentage.value
        if (beerPercentage != null) {
            when (beerPercentage) {
                0 -> {
                    binding.imageView8.setImageResource(R.drawable.b0)
                }
                in 1..10 -> {
                    binding.imageView8.setImageResource(R.drawable.b10)
                }
                in 11..20 -> {
                    binding.imageView8.setImageResource(R.drawable.b20)
                }
                in 21..30 -> {
                    binding.imageView8.setImageResource(R.drawable.b30)
                }
                in 31..40 -> {
                    binding.imageView8.setImageResource(R.drawable.b40)
                }
                in 41..50 -> {
                    binding.imageView8.setImageResource(R.drawable.b50)
                }
                in 51..60 -> {
                    binding.imageView8.setImageResource(R.drawable.b60)
                }
                in 61..70 -> {
                    binding.imageView8.setImageResource(R.drawable.b70)
                }
                in 71..80 -> {
                    binding.imageView8.setImageResource(R.drawable.b80)
                }
                in 81..90 -> {
                    binding.imageView8.setImageResource(R.drawable.b90)
                }
                else -> {
                    binding.imageView8.setImageResource(R.drawable.b100)
                }
            }
        }
    }

    /**
     * Setup video
     *
     */
    private fun setupVideo() {
        val color = viewModel.selected_beer.value?.colour
        val filename = File(
            RealmApplication.instance.getExternalFilesDir(null)?.absolutePath.toString() + "/DownloadedVideo/",
            "$color.mp4"
        )
        uri = Uri.fromFile(filename)
        binding.videoView3.setVideoURI(uri)
        binding.videoView3.start()
        // Make video looping infinity
        binding.videoView3.setOnPreparedListener { mp ->
            mp.isLooping = true
            Handler(Looper.getMainLooper()).postDelayed({
                binding.videoView3.setBackgroundColor(Color.TRANSPARENT)
            }, 500)
        }
    }
}
