package com.nereus.craftbeer.fragment

import android.content.Context
import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.FoodShopActivity
import com.nereus.craftbeer.activity.ReadCustomerCodeActivity
import com.nereus.craftbeer.activity.SelectModeActivity
import com.nereus.craftbeer.activity.SettingMasterActivity
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.FragmentPasswordTabletBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.LoginRequest
import com.nereus.craftbeer.model.LoginResponse
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.hideKeyboard
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.PasswordTabletViewModel
import kotlinx.coroutines.runBlocking

/**
 * Password tablet fragment
 *
 * コンストラクタ  Password tablet fragment
 */
class PasswordTabletFragment :
    BaseFragment<FragmentPasswordTabletBinding, PasswordTabletViewModel>() {

    /**
     * View model
     */
    override val viewModel: PasswordTabletViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_password_tablet
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.apply {
            txtPassword.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    txtPassword.hideKeyboard(requireActivity())
                    updatePassword()
                }
                false
            }

            btnSubmitPassword.setOnClickDebounce {
                btnSubmitPassword.hideKeyboard(requireActivity())
                updatePassword()
            }
        }
        binding.btnBack.setOnClickDebounce {
            requireActivity().finish()
        }
    }

    /**
     * Set additional view model listener
     *
     */
    override fun setAdditionalViewModelListener() {
        viewModel.password.observe(this, Observer { newPassword ->
            if (newPassword != CommonConst.EMPTY_STRING) {
                viewModel.setLoadingState(CommonConst.LOADING_VISIBLE)
                checkLogin(newPassword)
            }
        })

        viewModel.loginRequest.observe(this, Observer {
            viewModel.login()
        })


        val b = requireActivity().intent.extras
        var value = -1

        value = if (b != null) {
            b.getInt(SELECT_SCREEN_EXTRA_KEY)
        } else {
            arguments?.getInt(SELECT_SCREEN_EXTRA_KEY) ?: 0
        }

        this.viewModel.loginResponse.observe(this, Observer { response ->
            try {
                displayToast(
                    requireContext(),
                    getString(R.string.msg_login_successfully, response.hardwareName)
                )
                storeToken(response)
                var intent: Intent
                if (value == SELECT_SCREEN_EXTRA_MASTER_SETTING) {
                    requireActivity().intent.removeExtra(SELECT_SCREEN_EXTRA_KEY)
                    intent = Intent(requireContext(), SettingMasterActivity::class.java)
                } else if (value == SELECT_SCREEN_EXTRA_FOOD_SHOP) {
                    requireActivity().intent.removeExtra(SELECT_SCREEN_EXTRA_KEY)
                    intent = Intent(requireContext(), FoodShopActivity::class.java)
                } else if (value == SELECT_SCREEN_EXTRA_SELECT_MODE) {
                    requireActivity().intent.removeExtra(SELECT_SCREEN_EXTRA_KEY)
                    intent = Intent(requireContext(), SelectModeActivity::class.java)
                } else {
                    // Connect to socket that holds the tablet status
                    runBlocking {
                        connectAliveSocket()
                    }
                    intent = Intent(requireContext(), ReadCustomerCodeActivity::class.java)


                }
                this.viewModel.setupRecurringWork(response)
                startActivity(intent)
                requireActivity().finish()
            } catch (ex: Exception) {
                viewModel.setException(
                    MessageException(
                        MessagesModel(R.string.msg_internal_exception),
                        cause = ex
                    )
                )
            } finally {
                dismissKeyboard(binding.btnSubmitPassword.windowToken)
            }
        })
    }

    private fun storeToken(
        response: LoginResponse
    ) {
        val editor = requireActivity().getSharedPreferences(TOKEN, Context.MODE_PRIVATE).edit()
        editor.putString(ACCESS_TOKEN, BEARER + response.accessToken)
        val sharedPreference =
            requireActivity().getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE).edit()
        sharedPreference.putString(PREF_DEVICE_ID, response.id)
        editor.apply()
        editor.commit()
        sharedPreference.apply()
        sharedPreference.commit()
    }

    private fun updatePassword() {
        viewModel.setPassword(binding.txtPassword.text.toString())
    }

    private fun checkLogin(password: String) {
        val sharedPreference =
            requireActivity().getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
        val deviceID = sharedPreference.getString(PREF_DEVICE_CODE, EMPTY_STRING)
        viewModel.setLoginRequest(LoginRequest(deviceID!!, password.trim()))
    }
}