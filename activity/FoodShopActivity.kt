package com.nereus.craftbeer.activity

import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.databinding.ActivityFoodShopBinding
import com.nereus.craftbeer.model.printer.PrinterResponse
import com.nereus.craftbeer.util.extractReceiptIdFromPrinterPath
import com.nereus.craftbeer.util.livedata.EventObserver
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Food shop activity
 *
 * コンストラクタ  FoodShopActivity
 */
@AndroidEntryPoint
class FoodShopActivity : BaseController<ActivityFoodShopBinding, FoodShopViewModel>() {
    override val viewModel: FoodShopViewModel by viewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_food_shop
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        viewModel.context = this
    }

    /**
     * Set additional view model listener
     *
     */
    override fun setAdditionalViewModelListener() {
        this.viewModel.currentBarcode.observe(this, EventObserver {
            viewModel.handleBarcode(it)
        })

        this.viewModel.foodShopFlowHandler.observe(this, EventObserver {
            if (it.isPaymentSuccess) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.checkoutCompleteFragment)
                this.viewModel.handlePostPayment()
            }

            if (it.isTopUpSuccess) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.topUpCompleteFragment)
                this.viewModel.handlePostTopUp(it.topUpResult)
            }
        })

        this.viewModel.payment.observe(this, EventObserver {
            this.viewModel.pay()
        })

        this.viewModel.printerResponse.observe(this, EventObserver {
            viewModel.printNext(it)
        })
    }

    /**
     * Barcode
     */
    var barcode = EMPTY_STRING

    /**
     * Dispatch key event
     *
     * @param e
     * @return
     */
    override fun dispatchKeyEvent(e: KeyEvent): Boolean {
        if (e.action == KeyEvent.ACTION_DOWN) {
            Timber.i("dispatchKeyEvent: $e")
            val pressedKey = e.unicodeChar.toChar()
            barcode += pressedKey
        }
        barcode = barcode.trim()
        if (e.action == KeyEvent.ACTION_DOWN && e.keyCode == KeyEvent.KEYCODE_ENTER) {
            viewModel.setCurrentBarcode(barcode)
            barcode = EMPTY_STRING
        }
        return true
    }

    /**
     * On new intent
     *
     * @param intent
     */
    override fun onNewIntent(intent: Intent) {
        setUpPrinterListener(intent)
        super.onNewIntent(intent)
    }

    /**
     * Set up printer listener
     *
     * @param intent
     */
    private fun setUpPrinterListener(intent: Intent) {
        val isActivityLaunchedFromActionView = intent.action == Intent.ACTION_VIEW
        if (isActivityLaunchedFromActionView && isFromPrinter(intent.data)) {
            intent.data?.let {
                val isMultiple = it.path!!.contains(PRINTER_VIEW_PATH_PRINT_MULTIPLE)
                val isIssued = it.path!!.contains(PRINTER_VIEW_PATH_ISSUE)
                val isTopUp = it.path!!.contains(PRINTER_VIEW_PATH_TOP_UP)
                val code =
                    intent.data?.getQueryParameter(PRINTER_QUERY_CODE) ?: PRINTER_SUCCESS_CODE
                val message =
                    intent.data?.getQueryParameter(PRINTER_QUERY_MESSAGE) ?: EMPTY_STRING
                var receiptId = extractReceiptIdFromPrinterPath(it.path!!)

                viewModel.setPrinterResponse(
                    PrinterResponse(
                        code = code,
                        isMultiplePrinting = isMultiple,
                        isIssued = isIssued,
                        message = message,
                        receiptId = receiptId,
                        isTopUp = isTopUp
                    )
                )
            }
        }
    }

    /**
     * Is from printer
     *
     * @param data
     * @return
     */
    private fun isFromPrinter(data: Uri?): Boolean {
        val uriString = data?.let {
            "${it.scheme}://${it.host}"
        }
        return uriString?.equals(PRINTER_VIEW_BASE_URI) ?: false
    }
}
