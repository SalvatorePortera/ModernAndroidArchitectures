package com.nereus.craftbeer.fragment

import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.MESSAGE_BUNDLE_KEY
import com.nereus.craftbeer.databinding.FragmentNotificationBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.DefaultViewModel


class NotificationFragmentDialog :
    BaseFragmentDialog<FragmentNotificationBinding, DefaultViewModel>() {

    override val viewModel: DefaultViewModel by activityViewModels()
    override fun getLayout(): Int {
        return R.layout.fragment_notification
    }

    override fun afterBinding() {
        setDialogSizeScale(1.0, 0.125)

        dialog!!.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)

        setIsCancelable(true)

        binding.textView38.text = arguments?.getString(MESSAGE_BUNDLE_KEY)

        Handler().postDelayed(
            {
                dismiss()
            }, 3000
        )
    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            dismiss()
        }
    }
}