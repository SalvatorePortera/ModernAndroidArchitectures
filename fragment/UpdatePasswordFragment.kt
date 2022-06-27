package com.nereus.craftbeer.fragment

import android.content.Context
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.SettingMasterActivity
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.PREF_DEVICE_CODE
import com.nereus.craftbeer.constant.PREF_DEVICE_FILE
import com.nereus.craftbeer.databinding.FragmentUpdatePasswordBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.UpdatePassRequest
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.UpdatePasswordViewModel

/**
 * Update password fragment
 *
 * @constructor  Update password fragment
 */
class UpdatePasswordFragment :
    BaseFragment<FragmentUpdatePasswordBinding, UpdatePasswordViewModel>() {

    /**
     * View model
     */
    override val viewModel: UpdatePasswordViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_update_password
    }


    /**
     * Set additional view model listener
     *
     */
    override fun setAdditionalViewModelListener() {
        viewModel.request.observe(this, Observer {
            viewModel.changePassword()
        })
        viewModel.changePassResponse.observe(this, Observer {
            backToMenu()
            displayToast(requireActivity(), getString(R.string.msg_change_password_successfully))
        })
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        val sharedPreference =
            requireActivity().getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
        val hardwareCode = sharedPreference.getString(PREF_DEVICE_CODE, EMPTY_STRING)

        binding.btnConfirmPasswordChg.setOnClickDebounce {
            val newPass = binding.txtNewPassword.text.trim().toString()
            val rePass = binding.txtRePassword.text.trim().toString()
            if (newPass == rePass) {
                this.viewModel.setRequest(
                    UpdatePassRequest(
                        hardwareCode!!,
                        newPass,
                        binding.txtCurrentPassword.text.trim().toString()
                    )
                )
            } else {
                viewModel.setException(MessageException(MessagesModel(R.string.msg_change_password_not_match)))
            }
        }
        binding.apply {
            txtCurrentPassword.setOnEditorActionListener(onEditorActionListener())
            txtNewPassword.setOnEditorActionListener(onEditorActionListener())
            txtRePassword.setOnEditorActionListener(onEditorActionListener())

        }

        binding.btnBack.setOnClickDebounce {
            backToMenu()
        }
    }

    /**
     * Back to menu
     *
     */
    private fun backToMenu() {
        val intent = Intent(requireActivity(), SettingMasterActivity::class.java)
        startActivity(intent)
    }
}
