package com.nereus.craftbeer.fragment

import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.MESSAGE_BUNDLE_KEY
import com.nereus.craftbeer.databinding.FragmentNotificationBinding
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.DefaultViewModel


/**
 * Notification fragment dialog
 *
 * コンストラクタ  Notification fragment dialog
 */
class NotificationFragmentDialog :
    BaseFragmentDialog<FragmentNotificationBinding, DefaultViewModel>() {

    /**
     * View model
     */
    override val viewModel: DefaultViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_notification
    }

    /**
     * After binding
     *
     */
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

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            dismiss()
        }
    }
}