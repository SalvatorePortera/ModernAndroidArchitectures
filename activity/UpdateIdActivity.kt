package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivityUpdateIdBinding
import com.nereus.craftbeer.viewmodel.UpdateIdViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Update id activity
 *
 * @constructor Create empty Update id activity
 */
@AndroidEntryPoint
class UpdateIdActivity : BaseController<ActivityUpdateIdBinding, UpdateIdViewModel>() {
    override val viewModel: UpdateIdViewModel by viewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_update_id
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