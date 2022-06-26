package com.nereus.craftbeer.viewmodel

import android.app.Application
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.UpdatePassRequest
import com.nereus.craftbeer.repository.AuthRepository
import com.nereus.craftbeer.repository.ErrorLogRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.util.concurrent.Callable

/**
 * Update password view model
 *
 * @property authRepository
 * @property savedStateHandle
 * @property _errorLogRepository
 * @constructor
 *
 * @param application
 */
class UpdatePasswordViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    @Assisted var savedStateHandle: SavedStateHandle,
    application: Application,
    override var _errorLogRepository: ErrorLogRepository
) : BaseViewModel(application) {
    private val _currentPassword = MutableLiveData<String>()
    private val _newPassword = MutableLiveData<String>()
    private val _rePassword = MutableLiveData<String>()
    private val _request = MutableLiveData<UpdatePassRequest>()
    private val _changePassResponse = MutableLiveData<ResponseBody>()

    init {
        _currentPassword.value = CommonConst.EMPTY_STRING
        _newPassword.value = CommonConst.EMPTY_STRING
        _rePassword.value = CommonConst.EMPTY_STRING
    }

    val changePassResponse: LiveData<ResponseBody>
        get() = this._changePassResponse

    fun changePassword() {
        _request.value?.let {
            viewModelScope.launch {
                setLoadingState(CommonConst.LOADING_VISIBLE)
                try {
                    _changePassResponse.value = authRepository.changeDevicePassword(it)
                } catch (ex: MessageException) {
                    ex.setCallback(Callable { changePassword() })
                    setException(ex)
                } catch (ex: Exception) {
                    setException(ex)
                } finally {
                    setLoadingState(CommonConst.LOADING_GONE)
                }
            }
        }
    }


    val request: MutableLiveData<UpdatePassRequest>
        get() = this._request

    fun setRequest(request: UpdatePassRequest) {
        _request.value = request
    }
}