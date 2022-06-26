package com.nereus.craftbeer.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.nereus.craftbeer.constant.ACCESS_TOKEN
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.TOKEN
import com.nereus.craftbeer.controller.BaseController
import com.nereus.craftbeer.socket.SocketIO
import com.nereus.craftbeer.viewmodel.BaseViewModel
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseFragment<TBinding : ViewDataBinding, TViewModel : BaseViewModel> :
    Fragment() {

    protected lateinit var binding: TBinding
    protected abstract val viewModel: TViewModel

    abstract fun getLayout(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBinding(inflater, container)
        afterBinding()
        hideNavigationBar()
        setViewListener()
        setViewModelListener()
        return binding.root
    }

    open fun afterBinding() {}

    private fun setupBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            getLayout(), container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
    }

    open fun setViewListener() {}
    open fun setAdditionalViewModelListener() {}

    open fun setViewModelListener() {
        (requireActivity() as BaseController<*, *>).setViewModelListener()
        setAdditionalViewModelListener()
    }

    fun hideNavigationBar() {
        val decorView = activity?.window?.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView?.systemUiVisibility = uiOptions
    }

    fun hideActionBar() {
        (activity as BaseController<*, *>)?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            supportActionBar?.hide()
        }
    }

    fun onEditorActionListener() =
        TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                hideNavigationBar()
            }
            false
        }


    suspend fun connectAliveSocket() {
        val sharedPreference = requireActivity().getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
        val token = sharedPreference.getString(ACCESS_TOKEN, EMPTY_STRING)?.split(" ")?.get(1)
        var mSocket = SocketIO.getAliveSocket(token.toString())!!
        withContext(Dispatchers.IO) {
            mSocket.connect()
        }
    }

    fun dismissKeyboard(windowToken: IBinder) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireActivity().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    fun notifyMessageForNetwork(message: String?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message).setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            requireActivity().finish()
        }
            .setIcon(android.R.drawable.ic_dialog_alert)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun getSharedPreferences(name: String?, mode: Int): SharedPreferences? {
        return requireActivity().getSharedPreferences(name, mode)
    }

    fun checkToken(): Boolean {
        val sharedPreference = getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
        return sharedPreference!!.getString(ACCESS_TOKEN, EMPTY_STRING)!!.isNotBlank()
    }
}