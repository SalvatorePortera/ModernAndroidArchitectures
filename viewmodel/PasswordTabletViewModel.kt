package com.nereus.craftbeer.viewmodel

import android.app.Application
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.nereus.craftbeer.constant.BEARER
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.LoginRequest
import com.nereus.craftbeer.model.LoginResponse
import com.nereus.craftbeer.repository.AuthRepository
import com.nereus.craftbeer.repository.ErrorLogRepository
import com.nereus.craftbeer.util.setupSyncErrorLogsRecurringWork
import com.nereus.craftbeer.util.setupSyncGoodsRecurringWork
import com.nereus.craftbeer.util.setupSyncSaleLogsRecurringWork
import com.nereus.craftbeer.util.setupSyncShopInfoRecurringWork
import kotlinx.coroutines.launch
import java.util.concurrent.Callable

/**
 * Password tablet view model
 *
 * @property authRepository
 * @property savedStateHandle
 * @property _errorLogRepository
 * @constructor
 *
 * @param application
 */
class PasswordTabletViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    @Assisted var savedStateHandle: SavedStateHandle,
    application: Application,
    override var _errorLogRepository: ErrorLogRepository
) : BaseViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    private val _password = MutableLiveData<String>()

    private val _loginRequest = MutableLiveData<LoginRequest>()

    private val _loginResponse = MutableLiveData<LoginResponse>()


    init {
        _password.value = CommonConst.EMPTY_STRING
    }

    val loginRequest: LiveData<LoginRequest>
        get() = this._loginRequest

    /**
     * Login
     *
     */
    fun login() {
        _loginRequest.value?.let {
            viewModelScope.launch {
                setLoadingState(CommonConst.LOADING_VISIBLE)
                try {
                    _loginResponse.value = authRepository.checkLogin(it)
                } catch (ex: MessageException) {
                    ex.setCallback(Callable { login() })
                    setException(ex)
                } catch (ex: Exception) {
                    setException(ex)
                } finally {
                    setLoadingState(CommonConst.LOADING_GONE)
                }
            }
        }
    }

    val loginResponse: LiveData<LoginResponse>
        get() = this._loginResponse

    val password: LiveData<String>
        get() = this._password

    /**
     * Set password
     *
     * @param value
     */
    fun setPassword(value: String) {
        _password.value = value
    }

    /**
     * Set login request
     *
     * @param request
     */
    fun setLoginRequest(request: LoginRequest) {
        _loginRequest.value = request
    }

    /**
     * Setup WorkManager background job to 'fetch' new network data periodically.
     */
    fun setupRecurringWork(login: LoginResponse) {
        val token = BEARER + login.accessToken
        setupSyncGoodsRecurringWork(token, workManager)
        setupSyncSaleLogsRecurringWork(token, workManager)
        setupSyncErrorLogsRecurringWork(token, workManager)
        setupSyncShopInfoRecurringWork(token, workManager, login.companyId, login.shopId)
    }
}