package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivitySettingMasterBinding
import com.nereus.craftbeer.viewmodel.SettingMasterViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Setting master activity
 *
 * @constructor Create empty Setting master activity
 */
@AndroidEntryPoint
class SettingMasterActivity :
    BaseController<ActivitySettingMasterBinding, SettingMasterViewModel>() {
    override val viewModel: SettingMasterViewModel by viewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_setting_master
    }
}