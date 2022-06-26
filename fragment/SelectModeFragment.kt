package com.nereus.craftbeer.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.BeerShopActivity
import com.nereus.craftbeer.activity.FoodShopActivity
import com.nereus.craftbeer.activity.PasswordTabletActivity
import com.nereus.craftbeer.adapter.colorList
import com.nereus.craftbeer.adapter.countVideo
import com.nereus.craftbeer.constant.SELECT_SCREEN_EXTRA_FOOD_SHOP
import com.nereus.craftbeer.constant.SELECT_SCREEN_EXTRA_KEY
import com.nereus.craftbeer.constant.SELECT_SCREEN_EXTRA_MASTER_SETTING
import com.nereus.craftbeer.databinding.FragmentSelectModeBinding
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import com.nereus.craftbeer.worker.UpdateBeerWorker
import java.lang.Exception


/**
 * Select mode fragment
 *
 * @constructor Create empty Select mode fragment
 */
class SelectModeFragment :
    BaseFragment<FragmentSelectModeBinding, DefaultViewModel>() {

    /**
     * View model
     */
    override val viewModel: DefaultViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_select_mode
    }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * On resume
     *
     */
    override fun onResume() {
        super.onResume()
        colorList.clear()
        countVideo = 0
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {

        binding.btnBeer.setOnClickDebounce {
            val intentBeer = Intent(requireActivity(), BeerShopActivity::class.java)
            if (!UpdateBeerWorker.checkFile()) {
                displayToast(requireActivity(), "データのダウンロードが完了するまでお待ちください")
            } else {
                startActivity(intentBeer)
            }
        }
        binding.btnFoodShop.setOnClickDebounce {
            if (checkToken()) {
                val intentFood = Intent(requireActivity(), FoodShopActivity::class.java)
                startActivity(intentFood)
            } else {
                val intent = Intent(requireActivity(), PasswordTabletActivity::class.java)
                val b = Bundle()
                b.putInt(SELECT_SCREEN_EXTRA_KEY, SELECT_SCREEN_EXTRA_FOOD_SHOP)
                intent.putExtras(b)
                startActivity(intent)
            }
        }
        binding.btnSetting.setOnClickDebounce {
            val intentSetting = Intent(requireActivity(), PasswordTabletActivity::class.java)
            val b = Bundle()
            b.putInt(SELECT_SCREEN_EXTRA_KEY, SELECT_SCREEN_EXTRA_MASTER_SETTING)
            intentSetting.putExtras(b)
            startActivity(intentSetting)
        }
        binding.btnWeb.setOnClickDebounce {
            goToCoreSystemWebAdmin()
        }
    }

    /**
     * Go to core system web admin
     *
     */
    private fun goToCoreSystemWebAdmin() {
        val url = BuildConfig.CORE_ADMIN_URL
        val i = Intent(Intent.ACTION_VIEW)
        try {
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e : Exception){
            //TODO
        }
    }
}