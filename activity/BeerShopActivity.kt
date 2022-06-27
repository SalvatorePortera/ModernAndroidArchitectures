package com.nereus.craftbeer.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivityBeerShopBinding
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.model.CombinationBeersInfo
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.socket.SocketIO
import com.nereus.craftbeer.util.TLogger
import com.nereus.craftbeer.viewmodel.BeerShopViewModel
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlinx.coroutines.withContext

/**
 * Beer shop activity
 *
 * @constructor  Beer shop activity
 */
@AndroidEntryPoint
class BeerShopActivity : BaseController<ActivityBeerShopBinding, BeerShopViewModel>() {

    override val viewModel: BeerShopViewModel by viewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_beer_shop
    }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val beerObserver = Observer<List<CombinationBeersInfo>> { beers ->
            // Update the UI, in this case, a TextView.
            beers.map {
                // Timber.i("BeerShop 変更通知: %s", it.obnizId)
                viewModel.forceStopOnOutOfService()
            }
        }

        viewModel.beers.observe(this, beerObserver)
    }

}