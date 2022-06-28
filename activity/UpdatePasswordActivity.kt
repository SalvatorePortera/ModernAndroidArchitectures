package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivityUpdatePasswordBinding
import com.nereus.craftbeer.viewmodel.UpdatePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Update password activity
 *
 * コンストラクタ  UpdatePasswordActivity
 */
@AndroidEntryPoint
class UpdatePasswordActivity :
    BaseController<ActivityUpdatePasswordBinding, UpdatePasswordViewModel>() {
	/**
     * View model: UpdatePasswordViewModel をロードします
     */
    override val viewModel: UpdatePasswordViewModel by viewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_update_password
    }
}
