package com.nereus.craftbeer.fragment

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.MESSAGE_BUNDLE_KEY
import com.nereus.craftbeer.databinding.FragmentConfirmDialogBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.DefaultViewModel

class ConfirmFragmentDialog :
    BaseFragmentDialog<FragmentConfirmDialogBinding, DefaultViewModel>() {

    companion object {
        const val REQUEST_KEY = "CONFIRMATION_REQUEST_KEY"
        const val CONFIRMATION_RESULT = "isConfirmed"
    }

    override val viewModel: DefaultViewModel by activityViewModels()
    override fun getLayout(): Int {
        return R.layout.fragment_confirm_dialog
    }

    override fun afterBinding() {
        setDialogSizeScale(0.5, 0.5)
        binding.content.text = arguments?.getString(MESSAGE_BUNDLE_KEY)
    }

    override fun setViewListener() {
        binding.btnCancel.setOnClickDebounce {
            setFragmentResult(REQUEST_KEY, bundleOf(CONFIRMATION_RESULT to false))
            dismiss()
        }
        binding.btnOk.setOnClickDebounce {
            setFragmentResult(REQUEST_KEY, bundleOf(CONFIRMATION_RESULT to true))
            dismiss()
        }
    }
}