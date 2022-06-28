package com.nereus.craftbeer.controller

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.PasswordTabletActivity
import com.nereus.craftbeer.activity.UpdateIdActivity
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.Company
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.ShopInfo
import com.nereus.craftbeer.util.displayError
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.viewmodel.BaseViewModel
import timber.log.Timber

/**
 * Base controller
 *
 * @param TBinding
 * @param TViewModel
 * コンストラクタ BaseController
 */
abstract class BaseController<TBinding : ViewDataBinding, TViewModel : BaseViewModel>() :
    AppCompatActivity() {
    private val REQUEST_PRINTER = 1

    private var mLoadingdialog: AlertDialog? = null

    protected lateinit var binding: TBinding

    protected abstract val viewModel: TViewModel

    abstract fun getLayout(): Int

    open fun afterBinding() {}

    open fun setViewListener() {}

    open fun isTokenRequired(): Boolean {
        return true
    }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(
            "========= savedInstanceState?.get(example) %s",
            savedInstanceState?.get("example")
        )
        Timber.i("========= savedInstanceState? %s", savedInstanceState?.toString())

        super.onCreate(savedInstanceState)
        checkToken()
        hideActionBar()
        setupBinding()
        hideNavigationBar()
        setViewListener()
        setViewModelListener()
        afterBinding()
    }

    /**
     * チェックトークン
     * 
     */
    private fun checkToken() {
        if (!isTokenRequired()) {
            return
        }

        if (!Company.isExisted() || !ShopInfo.isExisted()) {
            startActivity(Intent(this, UpdateIdActivity::class.java))
            finish()
            return
        }

        val sharedPreference = getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
        if (sharedPreference!!.getString(ACCESS_TOKEN, EMPTY_STRING)!!.isBlank()) {
            startActivity(Intent(this, PasswordTabletActivity::class.java))
            finish()
        }
    }

    /**
     * Setup binding
     *
     */
    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, getLayout())
        binding.lifecycleOwner = this
    }

    /**
     * On resume
     *
     */
    public override fun onResume() {
        super.onResume()
        hideNavigationBar()
    }

    /**
     * On back pressed
     *
     */
    override fun onBackPressed() {
        displayToast(
            applicationContext,
            "You Are Not Allowed to Exit the App",
            Toast.LENGTH_SHORT
        )
    }

    /**
     * On window focus changed
     *
     * @param hasFocus
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideNavigationBar()
    }

    /**
     * Set additional view model listener
     *
     */
    open fun setAdditionalViewModelListener() {}

    /**
     * Set view model listener
     *
     */
    fun setViewModelListener() {
        // Handle Loading dialog
        viewModel.loadingState.observe(this, Observer { loadingState ->
            handleLoadingDialog(loadingState, this)
        })

        viewModel.exception.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            // stop loading
            viewModel.setLoadingState(CommonConst.LOADING_GONE)
            // display Error to user
            when (it) {
                is MessageException -> {
                    it.getMessageModel().let { messageModel ->
                        if (messageModel.isFromErrorLog()) {
                            handleErrorLogMessage(messageModel)
                        } else if (messageModel.isFromMessageLog()){
                            handleLogMessage(messageModel, it)
                        }
                        else{
                            /*Handle old style messages (not from ErrorLog)*/
                            // Build ESCOMMON Message Model
                            val rebuildMessagesModel = MessagesModel(
                                ErrorLogCode.ESCOMMON,
                                coreMsgArgs = listOf(messageModel.getMessages().firstOrNull()?: EMPTY_STRING, it.cause?.stackTraceToString()?: EMPTY_STRING)
                            )

                            handleErrorLogMessage(rebuildMessagesModel)
                        }
                    }
                }

                /* Unhandled Messages*/
                else -> {
                    // Build ESCOMMON Message Model
                    val rebuildMessagesModel = MessagesModel(
                        ErrorLogCode.ESCOMMON,
                        coreMsgArgs = listOf(getString(R.string.msg_internal_exception), it.cause?.stackTraceToString()?: EMPTY_STRING)
                    )
                    handleErrorLogMessage(rebuildMessagesModel)
                }
            }
            Timber.e(it)
            // reset exception
            viewModel.setException(null)
        })

        viewModel.successMessages.observe(this, Observer { msgModel ->
            msgModel.getMessages().forEach {
                displayToast(this, it)
            }
        })

        setAdditionalViewModelListener()
    }

    /**
     * Handle error log message
     *
     * @param messageModel
     */
    private fun handleErrorLogMessage(
        messageModel: MessagesModel
    ) {
        /*Tablet UI Message*/
        if (messageModel.getErrorLogCode()!!
                .hasTabletMessage()
        ) {
            this.displayError(
                messageModel.getMessages()
            )
        }

        /*Send Message To CMS*/
        if (messageModel.isFromErrorLog() && messageModel.getErrorLogCode()!!
                .hasCoreMessage()
        ) {
            viewModel.sendErrorLog(messageModel)
        }
    }

    /**
     * Handle log message
     *
     * @param messageModel
     * @param it
     */
    private fun handleLogMessage(
        messageModel: MessagesModel,
        it: MessageException
    ) {
        /*Tablet UI Message*/
        if (messageModel.getMessageLogCode()!!
                .hasTabletMessage()
        ) {
            this.displayError(
                messageModel.getMessages(),
                callback = it.getCallback()
            )
        }

        /*Send Message To CMS*/
        if (messageModel.isFromMessageLog() && messageModel.getMessageLogCode()!!
                .hasCoreMessage()
        ) {
            viewModel.sendMessageLog(messageModel)
        }
    }

    /**
     * Hide navigation bar
     *
     */
    private fun hideNavigationBar() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    /**
     * Hide action bar
     *
     */
    private fun hideActionBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
    }

    /**
     * Set on done keyboard listener
     *
     * @param textView
     */
    fun setOnDoneKeyboardListener(textView: TextView) {
        textView.setOnEditorActionListener(onEditorActionListener())
    }

    /**
     * On editor action listener
     *
     */
    fun onEditorActionListener() =
        TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                hideNavigationBar()
            }
            false
        }

    /**
     * Notify message general
     *
     * @param message
     * @param activity
     */
    fun notifyMessageGeneral(message: String?, activity: Activity? = null) {
        val builder2 = AlertDialog.Builder(this)
        builder2.setMessage(message).setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            if (activity != null) {
                val i = Intent(this, activity::class.java)
                startActivity(i)
            }
            hideNavigationBar()
        }
            .setIcon(android.R.drawable.ic_dialog_alert)
        val dialog2: AlertDialog = builder2.create()
        dialog2.setCancelable(false)
        dialog2.show()
    }

    /**
     * On destroy
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mLoadingdialog != null) {
            mLoadingdialog!!.dismiss()
            mLoadingdialog = null
        }
    }

    /**
     * Handle loading dialog
     *
     * @param loadingState
     * @param baseController
     */
    private fun handleLoadingDialog(
        loadingState: Int?,
        baseController: BaseController<TBinding, TViewModel>
    ) {
        if (mLoadingdialog == null) {
            mLoadingdialog = createCustomLoadingDialog()
        }
        if (!baseController.isFinishing) {
            if (loadingState == CommonConst.LOADING_VISIBLE) {
                mLoadingdialog!!.show()
            } else {
                mLoadingdialog!!.hide()
            }
        }
    }

    /**
     * Create custom loading dialog
     *
     * @return
     */
    private fun createCustomLoadingDialog(): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = this.layoutInflater
        val view: View = inflater.inflate(R.layout.loading_state_dialog, null)
        builder.setCustomTitle(view)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.5f).toInt()
        val dialog: AlertDialog = builder.create()
//        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.window?.setLayout(dialogWindowWidth, dialogWindowHeight)
        dialog.setCancelable(false)
        return dialog
    }
}