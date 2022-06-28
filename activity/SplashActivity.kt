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
 * コンストラクタ  SplashActivity
 */
@AndroidEntryPoint
class SplashActivity : BaseController<ActivitySplashBinding, DefaultViewModel>() {

    /**
     * View model: DefaultViewModel をロードします
     */
    override val viewModel: DefaultViewModel by viewModels()

    /**
     * Get layout activity_splash
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    /**
     * トークンが必要なチェック
     * 
     * @return
     */
    override fun isTokenRequired(): Boolean {
        return false
    }
}