package com.nereus.craftbeer.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.nereus.craftbeer.R;
import com.nereus.craftbeer.adapter.DeviceListArrayAdapter;
import com.nereus.craftbeer.adapter.DeviceListItem;
import com.seikoinstruments.sdk.thermalprinter.PrinterEvent;
import com.seikoinstruments.sdk.thermalprinter.PrinterException;
import com.seikoinstruments.sdk.thermalprinter.PrinterInfo;
import com.seikoinstruments.sdk.thermalprinter.PrinterListener;
import com.seikoinstruments.sdk.thermalprinter.PrinterManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Device discovery activity.
 */
@Deprecated
public class DeviceActivity extends Activity implements PrinterListener {

    /**
     * Argument of putExtra().
     */
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String MODEL = "model";

    public static final String DEFAULT_MODEL = "model";

    /**
     * Retry count and timeout value.
     */
    private static final int PRINTER_DISCOVERY_RETRY = 1;
    private static final int PRINTER_DISCOVERY_TIMEOUT = 10 * 1000;

    /**
     * PrinterManager and port type value.
     */
    private PrinterManager mManager;
    private int mPortType;

    /**
     * Handler for finish event of device discovery.
     */
    private Handler mHandler = new Handler();

    /**
     * ArrayAdapter of the device list.
     */
    private DeviceListArrayAdapter mDeviceListAdapter;

    /**
     * Progress dialog
     */
    private ProgressDialog mProgressDialog;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);

        // [Devices]List
        ListView lstPairedDevices = (ListView) findViewById(R.id.list_paired_devices);
        List<DeviceListItem> deviceList = new ArrayList<DeviceListItem>();
        mDeviceListAdapter = new DeviceListArrayAdapter(getApplicationContext(), deviceList);
        lstPairedDevices.setAdapter(mDeviceListAdapter);
        lstPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                final DeviceListItem item = (DeviceListItem) listView.getItemAtPosition(position);

                Intent result = new Intent();
                result.putExtra(NAME, item.getName());
                if (mPortType == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
                    result.putExtra(ADDRESS, item.getMacAddress());
                } else if (mPortType == PrinterManager.PRINTER_TYPE_TCP) {
                    result.putExtra(ADDRESS, item.getIpAddress());
                } else {
                    result.putExtra(ADDRESS, item.getMacAddress());
                }

                setResult(RESULT_OK, result);

                finish();
            }
        });

        // [Scan devices]Button
        Button btnScanDevices = (Button) findViewById(R.id.button_scan_devices);
        btnScanDevices.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                mDeviceListAdapter.clear();
                discoverDevices();
            }
        });

        mManager = new PrinterManager(getApplicationContext());

        final Intent intent = this.getIntent();
        mPortType = intent.getIntExtra(TYPE, PrinterManager.PRINTER_TYPE_BLUETOOTH);
    }


    @Override
    public void onStart() {
        super.onStart();
        discoverDevices();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mManager.cancelDiscoveryPrinter();
    }


    private void discoverDevices() {
        if (mPortType == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                return;
            }
        }
        mDeviceListAdapter.clear();
        createProgressDialogScanDevices().show();

        try {
            if (mPortType == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
                mManager.startDiscoveryPrinter(this);
            } else if (mPortType == PrinterManager.PRINTER_TYPE_TCP) {
                mManager.startDiscoveryPrinter(this, PRINTER_DISCOVERY_RETRY, PRINTER_DISCOVERY_TIMEOUT);
            } else {
                mManager.startDiscoveryPrinter(this, PrinterManager.PRINTER_TYPE_USB);
            }

        } catch (PrinterException e) {
            closeDialog();
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private Dialog createProgressDialogScanDevices() {
        mProgressDialog = new ProgressDialog(this);
        if (mPortType == PrinterManager.PRINTER_TYPE_TCP) {
            mProgressDialog.setTitle(R.string.tcpip);
        } else if (mPortType == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
            mProgressDialog.setTitle(R.string.bluetooth);
        } else {
            mProgressDialog.setTitle(R.string.usb);
        }
        mProgressDialog.setMessage(getString(R.string.scanning_devices));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                mManager.cancelDiscoveryPrinter();
            }
        });
        return mProgressDialog;
    }


    private void closeDialog() {
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
        }
    }


    /**
     * Device discovery finish or cancel event.
     *
     * @param event PrinterEvent.
     */
    public void finishEvent(PrinterEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case PrinterEvent.EVENT_FINISHED_DISCOVERY:
            case PrinterEvent.EVENT_CANCELED_DISCOVERY:
                mHandler.post(new Runnable() {
                    public void run() {
                        ArrayList<PrinterInfo> list = mManager.getFoundPrinter();
                        list.addAll(mManager.getFoundDevice());
                        int count = list.size();
                        for (int index = 0; index < count; index++) {
                            PrinterInfo info = list.get(index);
                            DeviceListItem item;
                            if (mPortType == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
                                item = new DeviceListItem(info.getPrinterModelName(), info.getBluetoothAddress(), getString(info.getIsBonded() ? R.string.paired : R.string.unpaired));
                            } else if (mPortType == PrinterManager.PRINTER_TYPE_TCP) {
                                item = new DeviceListItem(info.getPrinterModelName(), info.getMacAddress(), info.getIpAddress());
                            } else {
                                int printerModel = info.getPrinterModel();
                                String[] modelValueList = getResources().getStringArray(R.array.printer_model_values_list);
                                int listIndex = 0;
                                for (int index2 = 0; index2 < modelValueList.length; index2++) {
                                    String printerModelString = String.valueOf(printerModel);
                                    if (modelValueList[index2].equals(printerModelString)) {
                                        listIndex = index2;
                                        break;
                                    }
                                }
                                String[] modelList = getResources().getStringArray(R.array.printer_model_list);
                                item = new DeviceListItem(modelList[listIndex], info.getDevicePath());
                            }
                            mDeviceListAdapter.add(item);
                        }
                        closeDialog();
                    }
                });
                break;
        }
    }

}
