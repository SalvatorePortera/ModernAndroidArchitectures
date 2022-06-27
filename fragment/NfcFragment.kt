package com.nereus.craftbeer.fragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.abc.bluetoothTerminalfactory.AbCircleBluetoothTerminalManager
import com.abc.bluetoothTerminalfactory.BluetoothSmartCard
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.cardTerminal
import com.nereus.craftbeer.adapter.TerminalAdapter
import com.nereus.craftbeer.constant.SELECT_SCREEN_SPLASH_KEY
import com.nereus.craftbeer.databinding.ActivityReadCustomerCodeBinding
import com.nereus.craftbeer.util.BLE
import com.nereus.craftbeer.util.Hex
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.viewmodel.DefaultViewModel
import kotlinx.android.synthetic.main.activity_read_customer_code.*
import timber.log.Timber
import java.util.*
import javax.smartcardio.*
import kotlin.jvm.Throws


/**
 * Nfc fragment
 *
 * @constructor  Nfc fragment
 */
class NfcFragment :
    BaseFragment<ActivityReadCustomerCodeBinding, DefaultViewModel>() {

    /**
     * View model
     */
    override val viewModel: DefaultViewModel by activityViewModels()

    val TAG = "BLUETOOTH_MAIN_ACTIVITY"


    /**
     * Update terminal selector on change task
     */
    private var UpdateTerminalSelectorOnChangeTask: NfcFragment.UpdateTerminalSelectorOnChange? =
        null

    private var mManager: AbCircleBluetoothTerminalManager? = null


    /**
     * Buttons
     */
    private var btnRefreshTerminal: Button? = null
    private var btnReadCustomerCode: Button? = null
    private var btnDisconnect: Button? = null

    private var imgStatus: ImageView? = null

    /**
     * Spinner
     */
    private var spinnerTerminalSelector: Spinner? = null

    /**
     * SpinnerAdapter
     */
    private var mTerminalAdapter: TerminalAdapter? = null

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_read_customer_code
    }

    /**
     * On destroy
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        BluetoothSmartCard.getInstance(context).onStop()
    }


    /**
     * After binding
     *
     */
    override fun afterBinding() {
        // Get the Bluetooth terminal manager
        mManager = BluetoothSmartCard.getInstance(context).manager
        if (mManager == null) {
            displayToast(context, R.string.bluetooth_not_supported.toString())
            return
        }

        // Assign UI Handles
        InitializeUI()

        // Enable bluetooth and get location permission for bluetooth low energy
        enableBluetooth()

    }

    /**
     * Ui State
     *
     * @constructor  Ui State
     */
    internal enum class UI_STATE {
        DISCONNECTED, READYTOCONNECT, DIRECT, CONNECTED
    }

    /**
     * On resume
     *
     */
    override fun onResume() {
        enableBluetooth()
        refreshTerminalSelector()
        BluetoothSmartCard.getInstance(context).onResume()
        StartTerminalAutoRefresh()
        super.onResume()
        Timber.tag(TAG).i("onResume()")
    }


    /**
     * Initialize u i
     *
     */
    private fun InitializeUI() {

        btnRefreshTerminal =
            binding.activityMainButtonRefreshTerminal

        btnReadCustomerCode =
            binding.activityMainButtonConnect

        btnDisconnect =
            binding.btnDisconnect


        spinnerTerminalSelector =
            binding.activityMainSpinnerTerminal

        imgStatus = binding.imgStatusBl


        binding.btnBack.setOnClickListener {
            val b = requireActivity().intent.extras
            var value = -1
            if (b != null) {
                value = b.getInt(SELECT_SCREEN_SPLASH_KEY)
            }
            if (value == 0) {
                requireActivity().intent.removeExtra(SELECT_SCREEN_SPLASH_KEY)
                findNavController().navigate(R.id.selectModeFragment2)
            } else {
                findNavController().navigate(R.id.settingMasterActivity)
            }

        }


        btnRefreshTerminal?.setOnClickListener {
            refreshTerminalSelector()
            BluetoothSmartCard.getInstance(context).onResume()
            StartTerminalAutoRefresh()
            mManager?.ScanBluetooth(10000)

            binding.btnBack.visibility = View.GONE
            binding.prgBl.visibility = View.VISIBLE
            hideLoading()
        }

        /*
          Auto scan Terminal List
         */
        mManager?.ScanBluetooth(10000)


        btnDisconnect!!.setOnClickListener {
//            BluetoothSmartCard.getInstance(context).onStop()
            BluetoothSmartCard.terminals().a.Disconnect(cardTerminal)
            cardTerminal = null

        }

        /*
          SpinnerAdapter
         */
        mTerminalAdapter = TerminalAdapter(requireContext(), android.R.layout.simple_spinner_item)
        mTerminalAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        /*
          Setup Spinner
         */

        spinnerTerminalSelector!!.adapter = mTerminalAdapter
        spinnerTerminalSelector!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                cardTerminal = mTerminalAdapter!!.getTerminal(position)
//                displayToast(context, "Terminal Selected:" + cardTerminal.toString())
                imgStatus!!.setImageResource(R.drawable.checkok)
//                binding.txtBlStatus.text = getText(R.string.msg_connected)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.tag(TAG).i("No Terminal Selected")
                imgStatus!!.setImageResource(R.drawable.notconnect)
//                binding.txtBlStatus.text = getText(R.string.msg_not_connected)
            }
        }
    }


    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_FINE_LOCATION = 3

    /**
     * Enable bluetooth
     *
     */
    private fun enableBluetooth() {
        val mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBtAdapter == null) {
            displayToast(context, "Bluetooth is not available")
            return
        }

        /*
          Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
          fire an intent to display a dialog asking the user to grant permission to enable it.
         */
        if (!mBtAdapter.isEnabled) {
            Timber.tag(TAG).i("onResume - BT not enabled")
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(
                enableIntent,
                REQUEST_ENABLE_BT
            )
        }

        /*
          Request for Bluetooth Fine Location
         */
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION
            )
        }
    }

    /**
     * Power on Smart Card and obtain channel for communication
     *
     * @param mTerminal
     * @throws CardException
     */
    @Throws(CardException::class)
    private fun Connect(mTerminal: CardTerminal): Card {
        val card = mTerminal.connect("*")

        if (!BLE.isFeliCa(card!!.atr)) {
            card.disconnect(true);
            throw CardException("Invalid card type.")
        }

        Timber.tag(TAG).i("Card ATR: %s", Hex.toHexString(card.atr.bytes))
        Timber.tag(TAG).i(card.toString())

        return card
    }


    /**
     * Helper to parse and send an ascii hex APDU Command
     *
     * @param commandString
     * @param channel
     * @throws CardException
     */
    @Throws(CardException::class)
    private fun ExchangeAPDU(commandString: String, channel: CardChannel): ResponseAPDU {
        val commandAPDU = CommandAPDU(Hex.stringToBytes(commandString))
        Timber.tag(TAG).i("CommandAPDU: %s", Hex.bytesToHexString(commandAPDU.bytes))
        val responseAPDU = channel.transmit(commandAPDU)
        Timber.tag(TAG).i("ResponseAPDU: %s", Hex.bytesToHexString(responseAPDU.data))

        return responseAPDU;
    }


    private fun hideLoading() {
        Handler().postDelayed({
            prgBl.visibility = View.INVISIBLE
            binding.btnBack.isEnabled = true
            binding.btnBack.visibility = View.VISIBLE
        }, 6000)
    }

    /**
     * Refresh Terminal List
     */
    private fun refreshTerminalSelector() {
        try {
            if (mTerminalAdapter != null) {
                val cardTerminals: CardTerminals?
                cardTerminals = BluetoothSmartCard.terminals()
                val cardTerminalsList = cardTerminals.list()
                mTerminalAdapter!!.updateTerminals(cardTerminalsList)
                if (!cardTerminalsList.isEmpty()) {
                    cardTerminal = mTerminalAdapter!!.getTerminal(0)
                    binding.imgStatusBl.setImageResource(R.drawable.checkok)
                } else {
                    imgStatusBl.setImageResource(R.drawable.notconnect)
                }
            }
        } catch (e: CardException) {
            Timber.tag(TAG).e(e)
        }
    }

    /**
     * Start terminal auto refresh
     *
     */
    private fun StartTerminalAutoRefresh() {
        if (UpdateTerminalSelectorOnChangeTask == null) {
            UpdateTerminalSelectorOnChangeTask = UpdateTerminalSelectorOnChange()
            UpdateTerminalSelectorOnChangeTask!!.execute(BluetoothSmartCard.terminals())
        }
    }

    /**
     * This Async Task monitor for terminal attach detach, updates selector./
     */



    inner class UpdateTerminalSelectorOnChange : AsyncTask<CardTerminals?, String?, Void?>() {
        override fun onProgressUpdate(vararg values: String?) {
            Timber.tag(TAG).i(values[0])
        }

        override fun doInBackground(vararg cardTerminals: CardTerminals?): Void? {
            var cardTerminalsListOld: List<CardTerminal?> = ArrayList()
            try {
                while (!isCancelled) {
                    val cardTerminalsList: List<CardTerminal?> = cardTerminals.get(0)!!.list()
                    if (cardTerminalsListOld != cardTerminalsList) {
                        cardTerminalsListOld = cardTerminalsList
                        requireActivity().runOnUiThread { refreshTerminalSelector() }
                    }
                    // Don't over Tax UI
                    Thread.sleep(100)
                }
            } catch (e: Exception) {
                publishProgress(e.message)
            }
            return null
        }
    }
}