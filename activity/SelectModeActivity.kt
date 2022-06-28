package com.nereus.craftbeer.activity

import androidx.activity.viewModels
import androidx.work.WorkManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivitySelectModeBinding
import com.nereus.craftbeer.util.setupUpdateBeerDataRecurringWork
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import com.nereus.craftbeer.worker.UpdateBeerWorker
import com.nereus.craftbeer.worker.UpdateBeerWorker.Companion.countDownloadFile
import dagger.hilt.android.AndroidEntryPoint

/**
 * Select mode activity
 *
 * コンストラクタ  SelectModeActivity
 */
@AndroidEntryPoint
class SelectModeActivity : BaseController<ActivitySelectModeBinding, DefaultViewModel>() {
    override val viewModel: DefaultViewModel by viewModels()
    override fun getLayout(): Int {
        return R.layout.activity_select_mode
    }

    /**
     * On resume
     *
     */
    override fun onResume() {
        super.onResume()
        if (!UpdateBeerWorker.checkFile()) {
            setupUpdatingWork()
        }
    }

    /**
     * Setup updating work
     *
     */
    private fun setupUpdatingWork() {
        countDownloadFile = 0
        val workManager = WorkManager.getInstance(applicationContext)
        setupUpdateBeerDataRecurringWork(workManager)
    }
}