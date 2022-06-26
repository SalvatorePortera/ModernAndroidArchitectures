package com.nereus.craftbeer.fragment

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.PasswordTabletActivity
import com.nereus.craftbeer.activity.SettingMasterActivity
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.FragmentUpdateIdBinding
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.UpdateIdViewModel
import kotlinx.android.synthetic.main.fragment_update_id.*

class UpdateIdFragment :
    BaseFragment<FragmentUpdateIdBinding, UpdateIdViewModel>(),
    AdapterView.OnItemSelectedListener {

    override val viewModel: UpdateIdViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_update_id
    }

    override fun afterBinding() {
        binding.viewModel = viewModel
        viewModel.loadDevices()
    }

    override fun setAdditionalViewModelListener() {
        viewModel.listDevicesResponse.observe(this, Observer {
            setupList(it)
        })
    }

    private fun setupList(list: List<String>) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDevice.adapter = adapter
    }

    override fun setViewListener() {
        binding.btnOkUpdateId.setOnClickDebounce() {
            var sharedPreference =
                requireActivity().getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            val spDeviceId = spDevice.selectedItem?.toString()
            if (!spDeviceId.isNullOrBlank()) {
                editor.putString(PREF_DEVICE_CODE, spDevice.selectedItem.toString().trim())
                editor.putString(SHARED_PREF_COMPANY_ID, editCompanyCode.text.toString().trim())
                editor.putString(SHARED_PREF_SHOP_ID, editShopCode.text.toString().trim())
                editor.apply()
                editor.commit()
                val intent = Intent(requireActivity(), PasswordTabletActivity::class.java)
                intent.putExtra(SELECT_SCREEN_EXTRA_KEY, SELECT_SCREEN_EXTRA_NFC)
                startActivity(intent)
                requireActivity().finish()
            } else {
                displayToast(requireActivity(), getString(R.string.msg_get_dv_id_fail))
            }
        }
        binding.btnBack.setOnClickDebounce() {
            val intent = Intent(requireActivity(), SettingMasterActivity::class.java)
            startActivity(intent)
        }
        binding.editCompanyCode.apply {
            setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    onChangeCompanyCode()
                }
                false
            }
            setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    onChangeCompanyCode()
                }
            }
        }

        binding.editShopCode.apply {
            setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    onChangeShopCode()
                }
                false
            }
            setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    onChangeShopCode()
                }
            }
        }
    }

    private fun onChangeShopCode() {
        viewModel.setShopCode(binding.editShopCode.text.toString())
        viewModel.loadDevices()
    }

    private fun onChangeCompanyCode() {
        viewModel.setCompanyCode(binding.editCompanyCode.text.toString())
        viewModel.loadDevices()
    }

    override fun onItemSelected(
        arg0: AdapterView<*>?,
        arg1: View?,
        position: Int,
        id: Long
    ) {
        viewModel.listDevicesResponse.value?.get(position)?.let {
            displayToast(requireContext(), "Selected Device: $it")
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        hideNavigationBar()
    }
}