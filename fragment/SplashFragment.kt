package com.nereus.craftbeer.fragment

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.ReadCustomerCodeActivity
import com.nereus.craftbeer.activity.UpdateIdActivity
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.FragmentSplashBinding
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import com.nereus.craftbeer.worker.deleteInternalStorageVideo
import kotlinx.coroutines.runBlocking

class SplashFragment :
    BaseFragment<FragmentSplashBinding, DefaultViewModel>() {

    override val viewModel: DefaultViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_splash
    }

    override fun afterBinding() {
        if (!isNetworkAvailable()) {
            notifyMessageForNetwork(getString(R.string.msg_network_not_available))
            Handler().postDelayed(
                {
                    requireActivity().finish()
                }, 2000
            )
        } else {
            deleteInternalStorageVideo()
            checkTabletId()
        }
    }

    private fun checkTabletId() {
        val sharedPreference = getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
        val tabletId = sharedPreference?.getString(PREF_DEVICE_CODE, EMPTY_STRING)
        val tokenPref = getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
        val tabletToken = tokenPref?.getString(ACCESS_TOKEN, null)
        val printerPref = getSharedPreferences(PRINTER, AppCompatActivity.MODE_PRIVATE)!!.all
        if (tabletId.isNullOrBlank() || tabletToken.isNullOrBlank()) {
//            notifyMessageGeneral("[登録済タブレットIDが無効化されています]", UpdateIdActivity())
            Handler().postDelayed(
                {
                    startActivity(Intent(requireActivity(), UpdateIdActivity::class.java))
                    requireActivity().finish()
                }, 2000
            )
        }
        // Use printer agent instead
//        else if (!(application as RealmApplication).getPrinterStatus() && !isPrinterSkipped) {
//            // Go to printer setting screen only when not set up yet and not skipped by user
//            startActivity(Intent(this, SetupPrinterActivity::class.java))
//        }
        else {
            val intent = Intent(requireActivity(), ReadCustomerCodeActivity::class.java)
            intent.putExtra("value", 0)
            startActivity(intent)
            // Connect to socket that holds the tablet status
            runBlocking {
                connectAliveSocket()
            }
            requireActivity().finish()
        }
    }
}