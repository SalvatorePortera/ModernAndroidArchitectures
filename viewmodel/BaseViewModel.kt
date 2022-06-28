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

/**
 * ベースモデル
 *
 * @constructor
 *
 * @param application
 */
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

    /**
     * 読み込み状態を設定する
     *
     * @param state
     */
    fun setLoadingState(state: Int) {
        _loadingState.value = state
    }

    /**
     * 例外を設定する
     *
     * @param ex
     */
    fun setException(ex: Exception?) {
        _exception.value = ex
    }

    /**
     * 成功メッセージを設定する
     *
     * @param resId
     * @param formatArgs
     */
    fun setSuccessMessage(@StringRes resId: Int, vararg formatArgs: String) {
        _successMessages.postValue(MessagesModel(resId, *formatArgs))
    }

    /**
     * エラーログを送信する
     *
     * @param messageModel
     */
    fun sendErrorLog(messageModel: MessagesModel) {
        viewModelScope.launch(Dispatchers.IO) {
            errorLogRepository.sendErrorLog(messageModel)
        }
    }

    /**
     * メッセージログを送信する
     *
     * @param messageModel
     */
    fun sendMessageLog(messageModel: MessagesModel) {
        viewModelScope.launch(Dispatchers.IO) {
            errorLogRepository.sendMessageLog(messageModel)
        }
    }
}