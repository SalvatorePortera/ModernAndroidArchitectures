package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivitySplashBinding
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Splash activity
 *
 * @constructor Create empty Splash activity
 */
@AndroidEntryPoint
class SplashActivity : BaseController<ActivitySplashBinding, DefaultViewModel>() {

    /**
     * View model
     */
    override val viewModel: DefaultViewModel by viewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    /**
     * Is token required
     *
     * @return
     */
    override fun isTokenRequired(): Boolean {
        return false
    }
}