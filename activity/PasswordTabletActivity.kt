package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivityPasswordTabletBinding
import com.nereus.craftbeer.viewmodel.PasswordTabletViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Password tablet activity
 *
 * @constructor Create empty Password tablet activity
 */
@AndroidEntryPoint
class PasswordTabletActivity :
    BaseController<ActivityPasswordTabletBinding, PasswordTabletViewModel>() {
    override val viewModel: PasswordTabletViewModel by viewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_password_tablet
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