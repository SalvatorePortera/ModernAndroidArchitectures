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
import com.nereus.craftbeer.repository.ErrorLogRepository
import com.nereus.craftbeer.repository.ShopRepository
import kotlinx.coroutines.launch
import java.util.concurrent.Callable

/**
 * Update id view model
 *
 * @property shopRepository
 * @property savedStateHandle
 * @property _errorLogRepository
 * コンストラクタ
 *
 * @param application
 */
class UpdateIdViewModel @ViewModelInject constructor(
    private val shopRepository: ShopRepository,
    @Assisted var savedStateHandle: SavedStateHandle,
    application: Application,
    override var _errorLogRepository: ErrorLogRepository
) : BaseViewModel(application) {

    private val _listDevicesResponse = MutableLiveData<List<String>>()

    private val _shopCode = MutableLiveData<String>()

    private val _companyCode = MutableLiveData<String>()

    val listDevicesResponse: LiveData<List<String>>
        get() = this._listDevicesResponse

    val shopId: LiveData<String>
        get() = this._shopCode

    val companyId: LiveData<String>
        get() = this._companyCode

    fun setShopCode(shopCode: String) {
        _shopCode.value = shopCode
    }

    fun setCompanyCode(companyCode: String) {
        _companyCode.value = companyCode
    }

    fun loadDevices() {
        if (_companyCode.value.isNullOrBlank() || _shopCode.value.isNullOrBlank()) {
            return
        }

        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                _listDevicesResponse.value = shopRepository.getDevices(_companyCode.value!!, _shopCode.value!!).map {
                    it.hardwareCode
                }
            } catch (ex: MessageException) {
                ex.setCallback(Callable { loadDevices() })
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }
}