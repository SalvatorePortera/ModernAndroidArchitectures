package com.nereus.craftbeer.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.work.WorkManager
import com.nereus.craftbeer.R
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivityNfcEmptyBinding
import com.nereus.craftbeer.util.setupUpdateBeerDataRecurringWork
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import com.nereus.craftbeer.worker.UpdateBeerWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.smartcardio.CardTerminal

var cardTerminal: CardTerminal? = null

@AndroidEntryPoint
class ReadCustomerCodeActivity :
    BaseController<ActivityNfcEmptyBinding, DefaultViewModel>() {

    override val viewModel: DefaultViewModel by viewModels()

    override fun getLayout(): Int {
        return R.layout.activity_nfc_empty
    }

    override fun onResume() {
        super.onResume()
        if (!UpdateBeerWorker.checkFile()) {
            setupUpdatingWork()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onCreate(savedInstanceState)
            if (!UpdateBeerWorker.checkFile()) {
                setupUpdatingWork()
            }
        }
    }

    private fun setupUpdatingWork() {
        UpdateBeerWorker.countDownloadFile = 0
        val workManager = WorkManager.getInstance(applicationContext)
        setupUpdateBeerDataRecurringWork(workManager)
    }

}