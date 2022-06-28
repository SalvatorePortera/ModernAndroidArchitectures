package com.nereus.craftbeer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.*
import com.nereus.craftbeer.databinding.FragmentSettingMasterMenuBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.SettingMasterViewModel
import timber.log.Timber

/**
 * Setting master menu fragment
 *
 * コンストラクタ  Setting master menu fragment
 */
class SettingMasterMenuFragment :
    BaseFragment<FragmentSettingMasterMenuBinding, SettingMasterViewModel>() {

    /**
     * View model
     */
    override val viewModel: SettingMasterViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_setting_master_menu
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.apply {
            txtUpdatePassword.setOnClickDebounce {
                val intentUpdatePass = Intent(requireContext(), UpdatePasswordActivity::class.java)
                startActivity(intentUpdatePass)
            }

            txtUpdateTabletId.setOnClickDebounce {
                val intentUpdateId = Intent(requireContext(), UpdateIdActivity::class.java)
                startActivity(intentUpdateId)
            }

            txtBeerServer.setOnClickDebounce {
                findNavController().navigate(R.id.beerServerFragment)
            }
            txtSaleLogs.setOnClickDebounce {
                findNavController().navigate(R.id.unsyncSaleLogsFragment)
            }
            btnBack.setOnClickDebounce() {
                val intentSelectMode = Intent(requireContext(), SelectModeActivity::class.java)
                startActivity(intentSelectMode)
            }

            txtNfc.setOnClickDebounce() {
                val intentReadNFC = Intent(requireContext(), ReadCustomerCodeActivity::class.java)
                startActivity(intentReadNFC)
            }
        }
    }
}