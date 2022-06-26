package com.nereus.craftbeer.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.nereus.craftbeer.repository.ErrorLogRepository

class DefaultViewModel @ViewModelInject constructor(
    application: Application,
    override var _errorLogRepository: ErrorLogRepository
) : BaseViewModel(application) {

}
