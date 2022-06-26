package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivityUpdatePasswordBinding
import com.nereus.craftbeer.viewmodel.UpdatePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordActivity :
    BaseController<ActivityUpdatePasswordBinding, UpdatePasswordViewModel>() {
    override val viewModel: UpdatePasswordViewModel by viewModels()

    override fun getLayout(): Int {
        return R.layout.activity_update_password
    }
}
