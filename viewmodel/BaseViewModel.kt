package com.nereus.craftbeer.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.repository.ErrorLogRepository
import com.nereus.craftbeer.util.TLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val _loadingState = MutableLiveData<Int>()
    private val _successMessages = MutableLiveData<MessagesModel>()
    private val _exception = MutableLiveData<Exception>()
    protected open lateinit var _errorLogRepository: ErrorLogRepository

    init {
        TLogger.writeln("%s::init".format(this.javaClass.name))
        _loadingState.value = CommonConst.LOADING_GONE
        _successMessages.value = MessagesModel()
    }

    val errorLogRepository: ErrorLogRepository
        get() = this._errorLogRepository

    val loadingState: LiveData<Int>
        get() = this._loadingState

    val exception: LiveData<Exception>
        get() = this._exception

    val successMessages: LiveData<MessagesModel>
        get() = this._successMessages

    fun setLoadingState(state: Int) {
        _loadingState.value = state
    }

    fun setException(ex: Exception?) {
        _exception.value = ex
    }

    fun setSuccessMessage(@StringRes resId: Int, vararg formatArgs: String) {
        _successMessages.postValue(MessagesModel(resId, *formatArgs))
    }

    fun sendErrorLog(messageModel: MessagesModel) {
        viewModelScope.launch(Dispatchers.IO) {
            errorLogRepository.sendErrorLog(messageModel)
        }
    }

    fun sendMessageLog(messageModel: MessagesModel) {
        viewModelScope.launch(Dispatchers.IO) {
            errorLogRepository.sendMessageLog(messageModel)
        }
    }
}