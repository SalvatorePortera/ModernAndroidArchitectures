package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivitySplashBinding
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseController<ActivitySplashBinding, DefaultViewModel>() {
    override val viewModel: DefaultViewModel by viewModels()
    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun isTokenRequired(): Boolean {
        return false
    }
}