package com.nereus.craftbeer.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.viewmodel.BaseViewModel
import timber.log.Timber

abstract class BaseFragmentDialog<TBinding : ViewDataBinding, TViewModel : BaseViewModel> :
    DialogFragment() {
    private var mDialogWidthScale: Double = 0.75
    private var mDialogHeightScale: Double = 0.75
    private val REQUEST_PRINTER = 1

    protected lateinit var binding: TBinding
    protected abstract val viewModel: TViewModel

    abstract fun getLayout(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBinding(inflater, container)
        afterBinding()
        setIsCancelable()
//        hideNavigationBar()
        setViewListener()
        setViewModelListener()
//        dialog?.window?.let {
////            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
////            it.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
////            hideNavigationBar()
////            it.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        }
        return binding.root
    }

    open fun afterBinding() {}

    private fun setupBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            getLayout(), container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
    }

    open fun setViewListener() {}
    open fun setAdditionalViewModelListener() {}

    private fun setViewModelListener() {
        (requireActivity() as BaseController<*, *>).setViewModelListener()
        setAdditionalViewModelListener()
    }

    open fun setIsCancelable(isCancelable:  Boolean = false) {
        this.isCancelable = isCancelable
        dialog?.setCanceledOnTouchOutside(isCancelable)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var viewParent = view
        while (viewParent is View) {
            viewParent.fitsSystemWindows = false
            viewParent.setOnApplyWindowInsetsListener { _, insets -> insets }
            viewParent = viewParent.parent as View?
        }
    }

    //TODO: this fix can hide status bar cand navigation bar for dialog but input can not be focused
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style)
//        dialog?.window?.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    open fun setDialogSizeScale(widthScale: Double = 0.75, heightScale: Double = 0.75) {
        mDialogWidthScale = widthScale
        mDialogHeightScale = heightScale
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val widthDp = resources.displayMetrics.run { widthPixels } * mDialogWidthScale
            val heightDp = resources.displayMetrics.run { heightPixels } * mDialogHeightScale
//            val width = ViewGroup.LayoutParams.MATCH_PARENT
//            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(widthDp.toInt(), heightDp.toInt())
        }
    }

}