package com.nereus.craftbeer.activity;

import android.Manifest;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nereus.craftbeer.R;
import com.nereus.craftbeer.fragment.AlertDialogFragment;
import com.nereus.craftbeer.model.printer.PrintModel;
import com.nereus.craftbeer.realm.RealmApplication;
import com.nereus.craftbeer.util.CastUtilKt;
import com.nereus.craftbeer.util.printer.PrinterUtil;
import com.seikoinstruments.sdk.thermalprinter.BarcodeScannerListener;
import com.seikoinstruments.sdk.thermalprinter.CallbackFunctionListener;
import com.seikoinstruments.sdk.thermalprinter.PrinterException;
import com.seikoinstruments.sdk.thermalprinter.PrinterManager;
import com.seikoinstruments.sdk.thermalprinter.printerenum.BarcodeSymbol;
import com.seikoinstruments.sdk.thermalprinter.printerenum.BuzzerPattern;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterBold;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterFont;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterReverse;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterScale;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterUnderline;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CuttingMethod;
import com.seikoinstruments.sdk.thermalprinter.printerenum.DataMatrixModule;
import com.seikoinstruments.sdk.thermalprinter.printerenum.Direction;
import com.seikoinstruments.sdk.thermalprinter.printerenum.Dithering;
import com.seikoinstruments.sdk.thermalprinter.printerenum.DrawerNum;
import com.seikoinstruments.sdk.thermalprinter.printerenum.ErrorCorrection;
import com.seikoinstruments.sdk.thermalprinter.printerenum.HriPosition;
import com.seikoinstruments.sdk.thermalprinter.printerenum.LineStyle;
import com.seikoinstruments.sdk.thermalprinter.printerenum.MacroRegistrationFunction;
import com.seikoinstruments.sdk.thermalprinter.printerenum.MaxiCodeMode;
import com.seikoinstruments.sdk.thermalprinter.printerenum.ModuleSize;
import com.seikoinstruments.sdk.thermalprinter.printerenum.NwRatio;
import com.seikoinstruments.sdk.thermalprinter.printerenum.Pdf417Symbol;
import com.seikoinstruments.sdk.thermalprinter.printerenum.PrintAlignment;
import com.seikoinstruments.sdk.thermalprinter.printerenum.PulseWidth;
import com.seikoinstruments.sdk.thermalprinter.printerenum.QrDataMode;
import com.seikoinstruments.sdk.thermalprinter.printerenum.QrModel;
import com.seikoinstruments.sdk.thermalprinter.printerenum.QrQuietZone;
import com.seikoinstruments.sdk.thermalprinter.printerenum.RegisteredFont;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

import static com.nereus.craftbeer.constant.Constants.DIALOG_BLUETOOTH_NO_SUPPORT;
import static com.nereus.craftbeer.constant.Constants.DIALOG_ENABLE_WIFI;
import static com.nereus.craftbeer.constant.Constants.DIALOG_FINISH_APP;
import static com.nereus.craftbeer.constant.Constants.PRINTER;
import static com.nereus.craftbeer.constant.Constants.PRINTER_ADDRESS;
import static com.nereus.craftbeer.constant.Constants.PRINTER_MODEL;
import static com.nereus.craftbeer.constant.Constants.PRINTER_TYPE;
import static com.nereus.craftbeer.constant.Constants.RESPONSE_REQUEST_CODE;


/**
 * Main Activity
 */
@Deprecated
@AndroidEntryPoint
public class SetupPrinterActivity extends FragmentActivity
        implements CallbackFunctionListener, BarcodeScannerListener {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_SELECT_DEVICE = 2;
    private static final int REQUEST_SETTING_PROPERTY = 3;
    private static final int REQUEST_LOCATION_SETTING_RESOLUTION = 4;
    private static final int REQUEST_CODE_WIFI = 5;

    private static final int REQUEST_CODE_BLUETOOTH = 0;
    private static final int REQUEST_CODE_SEND_DATA_FILE = 1;
    private static final int REQUEST_CODE_REGISTER_LOGO = 2;
    private static final int REQUEST_CODE_REGISTER_STYLESHEET = 3;
    private static final int REQUEST_CODE_PRINT_PAGE_MODE = 4;
    private static final int REQUEST_CODE_REGISTER_TEMPLATE = 5;
    private static final int REQUEST_CODE_REGISTER_IMAGE_DATA = 6;
    private static final int REQUEST_CODE_REGISTER_SLIDE_DATA = 7;
    private static final int REQUEST_CODE_REGISTER_USER_DEFINED_CHARACTER = 8;
    private static final int REQUEST_CODE_REGISTER_OPTION_FONT = 9;

    // Dialog ID
    /**
     * connect
     */
    public static final int DIALOG_SELECT_PRINTER_MODEL = 1;
    /**
     * sendText
     */
    public static final int DIALOG_INPUT_TEXT = 2;
    /**
     * sendBinary
     */
    public static final int DIALOG_INPUT_BINARY = 3;
    /**
     * getPrinterResponse
     */
    public static final int DIALOG_SELECT_PRINTER_RESPONSE = 4;
    /**
     * registerLogo(mobile)
     */
    public static final int DIALOG_REGISTER_LOGO_ID1 = 5;
    /**
     * registerLogo(pos)
     */
    public static final int DIALOG_REGISTER_LOGO_ID2 = 6;
    /**
     * unregisterLogo(mobile)
     */
    public static final int DIALOG_UNREGISTER_LOGO_ID1 = 7;
    /**
     * unregisterLogo(pos)
     */
    public static final int DIALOG_UNREGISTER_LOGO_ID2 = 8;
    /**
     * registerStyleSheet
     */
    public static final int DIALOG_REGISTER_STYLE_SHEET_NO = 9;
    /**
     * unregisterStyleSheet
     */
    public static final int DIALOG_UNREGISTER_STYLE_SHEET_NO = 10;
    /**
     * sendTextEx
     */
    public static final int DIALOG_INPUT_TEXT_EX = 11;
    /**
     * printBarcode
     */
    public static final int DIALOG_PRINT_BARCODE = 12;
    /**
     * printPDF417
     */
    public static final int DIALOG_PRINT_PDF417 = 13;
    /**
     * printQRcode
     */
    public static final int DIALOG_PRINT_QRCODE = 14;
    /**
     * printLogo
     */
    public static final int DIALOG_CUT_PAPER = 15;
    /**
     * printLogo
     */
    public static final int DIALOG_OPEN_DRAWER = 16;
    /**
     * buzzer
     */
    public static final int DIALOG_BUZZER = 17;
    /**
     * externalBuzzer
     */
    public static final int DIALOG_EXTERNAL_BUZZER = 18;
    /**
     * printLogo(mobile)
     */
    public static final int DIALOG_PRINT_LOGO1 = 19;
    /**
     * printLogo(pos)
     */
    public static final int DIALOG_PRINT_LOGO2 = 20;
    /**
     * sendDataFile
     */
    public static final int DIALOG_SEND_DATA_FILE = 21;
    /**
     * printDataMatrix
     */
    public static final int DIALOG_PRINT_DATAMATRIX = 22;
    /**
     * printMaxiCode
     */
    public static final int DIALOG_PRINT_MAXICODE = 23;
    /**
     * printGS1DatabarStacked
     */
    public static final int DIALOG_PRINT_GS1STACKED = 24;
    /**
     * printGS1DatabarStackedOmnidirectional
     */
    public static final int DIALOG_PRINT_GS1STACKEDOMNIDIRECTIONAL = 25;
    /**
     * printGS1DatabarExpandedStacked
     */
    public static final int DIALOG_PRINT_GS1EXPANDEDSTACKED = 26;
    /**
     * showTemplate
     */
    public static final int DIALOG_SHOW_TEMPLATE = 27;
    /**
     * showSlide
     */
    public static final int DIALOG_SHOW_SLIDE = 28;
    /**
     * executeMacro
     */
    public static final int DIALOG_EXECUTE_MACRO = 29;
    /**
     * turnOnScreen
     */
    public static final int DIALOG_TURN_ON_SCREEN = 30;
    /**
     * selectTemplate
     */
    public static final int DIALOG_SELECT_TEMPLATE = 31;
    /**
     * setTemplateImageData
     */
    public static final int DIALOG_SET_TEMPLATE_IMAGE_DATA = 32;
    /**
     * selectTemplateTextObject
     */
    public static final int DIALOG_SELECT_TEMPLATE_TEXT_OBJECT = 33;
    /**
     * setTemplateTextAlignment
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_ALIGNMENT = 34;
    /**
     * setTemplateTextData
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_DATA = 35;
    /**
     * setTemplateTextLeftMargin
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_LEFT_MARGIN = 36;
    /**
     * setTemplateTextLineSpacing
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_LINE_SPACING = 37;
    /**
     * setTemplateTextBold
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_BOLD = 38;
    /**
     * setTemplateTextUnderline
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_UNDERLINE = 39;
    /**
     * setTemplateTextSize
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_SIZE = 40;
    /**
     * setTemplateTextFont
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_FONT = 41;
    /**
     * setTemplateTextRegisteredFont
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_REGISTERED_FONT = 42;
    /**
     * setTemplateTextRightSpacing
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_RIGHT_SPACING = 43;
    /**
     * setTemplateTextColor
     */
    public static final int DIALOG_SET_TEMPLATE_TEXT_COLOR = 44;
    /**
     * setTemplateBarcodeData
     */
    public static final int DIALOG_SET_TEMPLATE_BARCODE_DATA = 45;
    /**
     * setTemplateQrcodeData
     */
    public static final int DIALOG_SET_TEMPLATE_QRCODE_DATA = 46;
    /**
     * registerTemplate
     */
    public static final int DIALOG_REGISTER_TEMPLATE = 47;
    /**
     * unregisterTemplate
     */
    public static final int DIALOG_UNREGISTER_TEMPLATE = 48;
    /**
     * registerImageData
     */
    public static final int DIALOG_REGISTER_IMAGE_DATA = 49;
    /**
     * unregisterImageData
     */
    public static final int DIALOG_UNREGISTER_IMAGE_DATA = 50;
    /**
     * registerSlideData
     */
    public static final int DIALOG_REGISTER_SLIDE_DATA = 51;
    /**
     * unregisterSlideData
     */
    public static final int DIALOG_UNREGISTER_SLIDE_DATA = 52;
    /**
     * registerUserDefinedCharacter
     */
    public static final int DIALOG_REGISTER_USER_DEFINED_CHARACTER = 53;
    /**
     * registerOptionFont
     */
    public static final int DIALOG_REGISTER_OPTION_FONT = 54;
    /**
     * controlMacroRegistration
     */
    public static final int DIALOG_CONTROL_MACRO_REGISTRATION = 55;
    /**
     * getDisplayResponse
     */
    public static final int DIALOG_GET_DISPLAY_RESPONSE = 56;
    /**
     * setBarcodeScannerListener
     */
    public static final int DIALOG_SET_BARCODE_SCANNER_LISTENER = 57;

    /**
     * Template text color code
     */
    public static final int TEMPLATE_TEXT_COLOR_BLACK = 0x000000;
    public static final int TEMPLATE_TEXT_COLOR_BLUE = 0x0000FF;
    public static final int TEMPLATE_TEXT_COLOR_GREEN = 0x00FF00;
    public static final int TEMPLATE_TEXT_COLOR_CYAN = 0x00FFFF;
    public static final int TEMPLATE_TEXT_COLOR_RED = 0xFF0000;
    public static final int TEMPLATE_TEXT_COLOR_MAGENTA = 0xFF00FF;
    public static final int TEMPLATE_TEXT_COLOR_YELLOW = 0xFFFF00;
    public static final int TEMPLATE_TEXT_COLOR_WHITE = 0xFFFFFF;

    // Log message setting
    /**
     * write "in" on log
     */
    protected static final boolean WRITE_LOG_IN = true;
    /**
     * write "out" on log
     */
    protected static final boolean WRITE_LOG_OUT = false;

    /**
     * PrinterManager SDK
     */
    @Inject
    PrinterManager mPrinterManager;

    /**
     * Select port
     */
    private int mSelectPort = PrinterManager.PRINTER_TYPE_BLUETOOTH;

    /**
     * Select file
     */
    private String mSelectPath = "";

    /**
     * Set listener
     */
    private static CallbackFunctionListener mCallbackFunctionListener = null;
    private static BarcodeScannerListener mBarcodeScannerListener = null;

    /**
     * Barcode data display method
     */
    private boolean mIsStringDisplay = false;

    /**
     * Handler to write on log
     */
    private static final Handler mHandler = new Handler();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_setup_printer);

        final EditText edtDeviceAddress = (EditText) findViewById(R.id.edittext_device_address);
        final Button btnDeviceList = (Button) findViewById(R.id.button_device_list);

        Object savedObject = getLastCustomNonConfigurationInstance();

        if (savedObject != null
                && savedObject instanceof ArrayList
                && ((ArrayList) savedObject).get(0) instanceof PrinterManager) {
            mPrinterManager = (PrinterManager) (((ArrayList) savedObject).get(0));
            edtDeviceAddress.setEnabled(true);
            btnDeviceList.setEnabled(true);
        } else {
            if (mPrinterManager == null) {
                mPrinterManager = new PrinterManager(getApplicationContext());
            }
            RealmApplication application = (RealmApplication) this.getApplication();
            application.setPrinterManager(mPrinterManager);

            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            Editor editor = pref.edit();
            if (Locale.JAPAN.equals(Locale.getDefault())) {
                editor.putString(getString(R.string.key_international_character), getString(R.string.international_character_default_jp_values));
                editor.putString(getString(R.string.key_code_page), getString(R.string.code_page_default_jp_values));
            } else {
                editor.putString(getString(R.string.key_international_character), getString(R.string.international_character_default_en_values));
                editor.putString(getString(R.string.key_code_page), getString(R.string.code_page_default_en_values));
            }
            editor.commit();

        }

        // [Connection type]Radio Button
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                String text = radioButton.getText().toString();

                if (text.equals(getString(R.string.usb))) {
                    mSelectPort = PrinterManager.PRINTER_TYPE_USB;
                } else if (text.equals(getString(R.string.tcpip))
                        || text.equals(getString(R.string.tcp))) {
                    mSelectPort = PrinterManager.PRINTER_TYPE_TCP;
                } else {
                    mSelectPort = PrinterManager.PRINTER_TYPE_BLUETOOTH;
                }
            }
        });

        // [List]Button
        if (btnDeviceList != null) {
            btnDeviceList.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(SetupPrinterActivity.this, DeviceActivity.class);
                    intent.putExtra(DeviceActivity.TYPE, mSelectPort);
                    if (mSelectPort == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
                        if (!checkBluetooth()) {
                            return;
                        }
                        if (PermissionChecker.checkSelfPermission(SetupPrinterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SetupPrinterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_BLUETOOTH);
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            checkLocationRequest();
                            return;
                        }
                    } else if (mSelectPort == PrinterManager.PRINTER_TYPE_TCP) {
                        if (!checkWifi()) {
                            return;
                        }
                    }
                    startActivityForResult(intent, REQUEST_SELECT_DEVICE);
                }
            });
        }

        // [connect]Button
        Button btnConnect = (Button) findViewById(R.id.button_connect);
        if (btnConnect != null) {
            btnConnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        showAlertDialog(DIALOG_SELECT_PRINTER_MODEL);
                    }
                }
            });
        }

        // [disconnect]Button
        Button btnDisconnect = (Button) findViewById(R.id.button_disconnect);
        if (btnDisconnect != null) {
            btnDisconnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    writeLog(getString(R.string.disconnect), WRITE_LOG_IN);

                    int ret = 0;
                    String msg;
                    try {
                        mPrinterManager.disconnect();
                        msg = getString(R.string.disconnect) + getString(R.string.msg_ok);
                    } catch (PrinterException e) {
                        ret = e.getErrorCode();
                        msg = getString(R.string.disconnect) + getString(R.string.msg_ng, ret);
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    writeLog(getString(R.string.disconnect), WRITE_LOG_OUT, ret);
                }
            });
        }

        // [cotrolTransaction]Button
        Button btnControlTransaction = (Button) findViewById(R.id.button_control_transaction);
        if (btnControlTransaction != null) {
            btnControlTransaction.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final String receiptTitle = " Receipt Sample \n\n";
                    final String receiptTime = "No.123456789   03/04/11 12:49 PM\n";
                    final String receiptContent =
                            "--------------------------------\n" +
                                    "GRILLED CHICKEN BREAST   $ 18.50\n" +
                                    "SIRLOIN STEAK            $ 32.00\n" +
                                    "ROAST LAMB               $ 20.00\n" +
                                    "SALAD                    $ 10.00\n" +
                                    "COKE                     $  3.50\n" +
                                    "COKE                     $  3.50\n" +
                                    "ICE CREAM                $  5.00\n" +
                                    "CHINESE NOODLE           $ 15.00\n" +
                                    "SUKIYAKI                 $ 30.00\n" +
                                    "SANDWICH                 $ 10.00\n" +
                                    "PIZZA                    $ 20.00\n" +
                                    "TEA                      $  3.50\n" +
                                    "COFFEE                   $  3.50\n\n" +
                                    "--------------------------------\n" +
                                    "        SUBTOTAL        $ 174.50\n" +
                                    "        SALES TAX       $   8.73\n" +
                                    "        TOTAL           $ 183.23\n\n" +
                                    "  Thank you and see you again!  \n";

                    PrintModel model = new PrintModel();
                    model.setContent(receiptContent);
                    PrinterUtil.printReceipt(mPrinterManager, Arrays.asList(model));
                }
            });
        }
        // [finish]Button
        Button btnFinish = (Button) findViewById(R.id.button_finish);
        Intent splashIntent = new Intent(this, SplashActivity.class);
        if (btnFinish != null) {
            btnFinish.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    writeLog(getString(R.string.finish), WRITE_LOG_IN);
                    startActivity(splashIntent);
                    finish();
                }
            });
        }
        // [skip]Button
        Button btnSkip = (Button) findViewById(R.id.button_skip);
        if (btnSkip != null) {
            btnSkip.setOnClickListener(view -> {
                writeLog(getString(R.string.skip), WRITE_LOG_IN);
                Editor printerPref = getSharedPreferences(PRINTER, MODE_PRIVATE).edit();
                printerPref.apply();
                printerPref.commit();
                startActivity(splashIntent);
                finish();
            });
        }

        checkSavedPrinter();
    }

    private void checkSavedPrinter() {
        Map<String, ?> printerPref = getSharedPreferences(PRINTER, MODE_PRIVATE).getAll();
        Integer printerModel = CastUtilKt.castToT(printerPref.get(PRINTER_MODEL), null);
        Integer printerType = CastUtilKt.castToT(printerPref.get(PRINTER_TYPE), null);
        String printerAddress = CastUtilKt.castToT(printerPref.get(PRINTER_ADDRESS), null);
        if (printerModel != null && printerType != null && printerAddress != null) {
            Timber.i("Connecting to saved printer...");
            mSelectPort = printerType;
            doConnectModel(printerModel, printerAddress);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onRestart() {
        super.onRestart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (checkBluetooth()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    checkLocationRequest();
                } else {
                    Intent intent = new Intent(SetupPrinterActivity.this, DeviceActivity.class);
                    intent.putExtra(DeviceActivity.TYPE, mSelectPort);
                    startActivityForResult(intent, REQUEST_SELECT_DEVICE);
                }
            }
        } else if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CODE_SEND_DATA_FILE:
                    showSelectFileDialog(DIALOG_SEND_DATA_FILE);
                    break;
                case REQUEST_CODE_REGISTER_LOGO:
                    int printerModel = mPrinterManager.getPrinterModel();
                    if ((printerModel == PrinterManager.PRINTER_MODEL_DPU_S245)
                            || (printerModel == PrinterManager.PRINTER_MODEL_DPU_S445)) {
                        showSelectFileDialog(DIALOG_REGISTER_LOGO_ID1);
                    } else {
                        showSelectFileDialog(DIALOG_REGISTER_LOGO_ID2);
                    }
                    break;
                case REQUEST_CODE_REGISTER_STYLESHEET:
                    showSelectFileDialog(DIALOG_REGISTER_STYLE_SHEET_NO);
                    break;
                case REQUEST_CODE_PRINT_PAGE_MODE:
                    doPrintPageMode();
                    break;
                case REQUEST_CODE_REGISTER_TEMPLATE:
                    showSelectFileDialog(DIALOG_REGISTER_TEMPLATE);
                    break;
                case REQUEST_CODE_REGISTER_IMAGE_DATA:
                    showSelectFileDialog(DIALOG_REGISTER_IMAGE_DATA);
                    break;
                case REQUEST_CODE_REGISTER_SLIDE_DATA:
                    showSelectFileDialog(DIALOG_REGISTER_SLIDE_DATA);
                    break;
                case REQUEST_CODE_REGISTER_USER_DEFINED_CHARACTER:
                    showSelectFileDialog(DIALOG_REGISTER_USER_DEFINED_CHARACTER);
                    break;
                case REQUEST_CODE_REGISTER_OPTION_FONT:
                    showSelectFileDialog(DIALOG_REGISTER_OPTION_FONT);
                    break;
                default:

            }
        }

    }

    @Override
    public void finish() {
        showAlertDialog(DIALOG_FINISH_APP);
    }


    public void finishApp() {
        try {
            mPrinterManager.disconnect();
        } catch (PrinterException e) {
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            super.finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item_settings:
//                Intent intent = new Intent(SetupPrinterActivity.this, SettingsActivity.class);
//                startActivityForResult(intent, REQUEST_SETTING_PROPERTY);
//                return true;
//        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    EditText edtDeviceAddress = (EditText) findViewById(R.id.edittext_device_address);
                    edtDeviceAddress.setText(data.getStringExtra(DeviceActivity.ADDRESS));
                }
                break;

            case REQUEST_SETTING_PROPERTY:
                setProperty();
                break;

            case REQUEST_LOCATION_SETTING_RESOLUTION:
                Intent intent = new Intent(SetupPrinterActivity.this, DeviceActivity.class);
                intent.putExtra(DeviceActivity.TYPE, mSelectPort);
                startActivityForResult(intent, REQUEST_SELECT_DEVICE);
                break;
        }
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return (new ArrayList<PrinterManager>()).add(mPrinterManager);
    }


    @Override
    public void onStatusChanged(int status) {
        final int currentStatus = status;
        //statusChanged
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        writeLog(getString(R.string.on_status_changed), WRITE_LOG_IN, getString(R.string.msg_status, currentStatus));
                        writeLog(getString(R.string.on_status_changed), WRITE_LOG_OUT);
                    }
                });
            }
        }).start();
    }


    @Override
    public void onBarcodeScannerReadData(byte[] data) {
        String barcodeData;
        if (mIsStringDisplay) {
            //String notation
            try {
                barcodeData = new String(data, "SJIS");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                barcodeData = "Encoding failed!!";
            }
        } else {
            //Hexadecimal notation
            StringBuilder sb = new StringBuilder(2 * data.length);
            for (byte b : data) {
                sb.append(String.format("%02x", b & 0xff));
            }
            barcodeData = sb.toString();
        }

        //BarcodeScanerReadData
        final String msg_barcode = barcodeData;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        writeLog(getString(R.string.on_barcode_scanner_read_data), WRITE_LOG_IN, msg_barcode);
                        writeLog(getString(R.string.on_barcode_scanner_read_data), WRITE_LOG_OUT);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBarcodeScannerChangedOnline() {
        //BarcodeScanerChangedOnline
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        writeLog(getString(R.string.on_barcode_scanner_changed_online), WRITE_LOG_IN, getString(R.string.msg_online));
                        writeLog(getString(R.string.on_barcode_scanner_changed_online), WRITE_LOG_OUT);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBarcodeScannerChangedOffline() {
        //BarcodeScanerChangedOffline
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        writeLog(getString(R.string.on_barcode_scanner_changed_offline), WRITE_LOG_IN, getString(R.string.msg_offline));
                        writeLog(getString(R.string.on_barcode_scanner_changed_offline), WRITE_LOG_OUT);
                    }
                });
            }
        }).start();
    }

    /**
     * Implementation when pressing the [connect] button.
     */
    public void doConnectModel(int model, String printerAddress) {
        switch (mSelectPort) {
            case PrinterManager.PRINTER_TYPE_TCP:
                if (!checkWifi()) {
                    return;
                }
                break;

            default:
                if (!checkBluetooth()) {
                    return;
                }
        }

        writeLog(getString(R.string.connect), WRITE_LOG_IN, printerAddress);

        int ret = 0;
        String msg;
        try {
            setProperty();
            switch (mSelectPort) {
                case PrinterManager.PRINTER_TYPE_USB:
                    mPrinterManager.connect(model, printerAddress);
                    break;

                case PrinterManager.PRINTER_TYPE_TCP:
                    mPrinterManager.connect(model, printerAddress);
                    break;

                default:
                    final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SetupPrinterActivity.this);
                    final boolean secure = pref.getBoolean(getString(R.string.key_secure_connection), true);
                    mPrinterManager.connect(model, printerAddress, secure);
            }

            Editor printerPref = getSharedPreferences(PRINTER, Context.MODE_PRIVATE).edit();
            printerPref.putInt(PRINTER_MODEL, model);
            printerPref.putInt(PRINTER_TYPE, mSelectPort);
            printerPref.putString(PRINTER_ADDRESS, printerAddress);
            printerPref.apply();
            printerPref.commit();

            msg = getString(R.string.connect) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.connect) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.connect), WRITE_LOG_OUT, ret);
    }

    public void doConnect(int which) {
        String[] printerModels = getResources().getStringArray(R.array.printer_model_values_list);
        int model = Integer.parseInt(printerModels[which]);
        EditText edtDeviceAddress = (EditText) findViewById(R.id.edittext_device_address);
        doConnectModel(model, edtDeviceAddress.getText().toString());
    }

    /**
     * Implementation when pressing the [sendText] button.
     *
     * @param text Text data sent to a printer
     */
    public void doSendText(String text) {
        writeLog(getString(R.string.send_text), WRITE_LOG_IN, text);

        text += getString(R.string.line_feed);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.sendText(text);

            msg = getString(R.string.send_text) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.send_text) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.send_text), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [sendTextEx] button.
     *
     * @param text      Text data to send to a printer
     * @param bold      Bold print
     * @param underline Underline
     * @param reverse   Reverse print
     * @param font      Font
     * @param scale     Character scale
     * @param alignment Alignment
     */
    public void doSendTextEx(String text, CharacterBold bold, CharacterUnderline underline, CharacterReverse reverse, CharacterFont font, CharacterScale scale, PrintAlignment alignment) {
        writeLog(getString(R.string.send_text_ex), WRITE_LOG_IN, text);

        text += getString(R.string.line_feed);

        int ret = 0;
        String msg;
        try {
            int printerModel = mPrinterManager.getPrinterModel();
            if ((printerModel == PrinterManager.PRINTER_MODEL_DPU_S245)
                    || (printerModel == PrinterManager.PRINTER_MODEL_DPU_S445)) {
                mPrinterManager.sendTextEx(
                        text,
                        bold,
                        underline,
                        font,
                        scale);
            } else {
                mPrinterManager.sendTextEx(
                        text,
                        bold,
                        underline,
                        reverse,
                        font,
                        scale,
                        alignment);
            }

            msg = getString(R.string.send_text_ex) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.send_text_ex) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.send_text_ex), WRITE_LOG_OUT, ret);
    }

    /**
     * Implementation when pressing the [printBarcode] button.
     *
     * @param barcodeSymbol Barcode symbol
     * @param data          Barcode data to send to the printer
     * @param moduleSize    Barcode width
     * @param moduleHeight  Barcode height
     * @param hriPosition   HRI character print position
     * @param hriFont       HRI character font
     * @param alignment     Alignment
     * @param nwRatio       N:W ratio
     */
    public void doPrintBarcode(BarcodeSymbol barcodeSymbol, String data, ModuleSize moduleSize, int moduleHeight, HriPosition hriPosition, CharacterFont hriFont, PrintAlignment alignment, NwRatio nwRatio) {
        writeLog(getString(R.string.print_barcode), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            switch (barcodeSymbol) {
                case BARCODE_SYMBOL_UPC_A:
                case BARCODE_SYMBOL_UPC_E:
                case BARCODE_SYMBOL_EAN13:
                case BARCODE_SYMBOL_JAN13:
                case BARCODE_SYMBOL_EAN8:
                case BARCODE_SYMBOL_JAN8:
                case BARCODE_SYMBOL_EAN13_ADDON:
                case BARCODE_SYMBOL_JAN13_ADDON:
                case BARCODE_SYMBOL_GS1_OMNI_DIRECTIONAL:
                case BARCODE_SYMBOL_GS1_TRUNCATED:
                case BARCODE_SYMBOL_GS1_LIMITED:
                case BARCODE_SYMBOL_GS1_EXPANDED:
                    mPrinterManager.printBarcode(
                            barcodeSymbol,
                            data,
                            moduleSize,
                            moduleHeight,
                            hriPosition,
                            hriFont,
                            alignment
                    );
                    break;

                case BARCODE_SYMBOL_CODE39:
                case BARCODE_SYMBOL_ITF:
                case BARCODE_SYMBOL_CODABAR:
                    mPrinterManager.printBarcode(
                            barcodeSymbol,
                            data,
                            moduleSize,
                            moduleHeight,
                            hriPosition,
                            hriFont,
                            alignment,
                            nwRatio
                    );
                    break;
                case BARCODE_SYMBOL_CODE93:
                case BARCODE_SYMBOL_CODE128:
                    byte[] byteData = asByteArray(data);
                    mPrinterManager.printBarcode(
                            barcodeSymbol,
                            byteData,
                            moduleSize,
                            moduleHeight,
                            hriPosition,
                            hriFont,
                            alignment
                    );
                    break;
            }

            msg = getString(R.string.print_barcode) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_barcode) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_barcode), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printPdf417] button.
     *
     * @param data            Barcode data to send to the printer
     * @param errorCorrection Error correction level
     * @param row             The number of rows
     * @param column          The number of columns in data area
     * @param moduleSize      Nominal fine element width
     * @param moduleHeight    Module height
     * @param alignment       Alignment
     * @param pdf417Symbol    Symbol of PDF417
     */
    public void doPrintPdf417(String data, ErrorCorrection errorCorrection, int row, int column, ModuleSize moduleSize, int moduleHeight, PrintAlignment alignment, Pdf417Symbol pdf417Symbol) {
        writeLog(getString(R.string.print_pdf417), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printPDF417(
                    data,
                    errorCorrection,
                    row,
                    column,
                    moduleSize,
                    moduleHeight,
                    alignment,
                    pdf417Symbol
            );

            msg = getString(R.string.print_pdf417) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_pdf417) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_pdf417), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printQrcode] button.
     *
     * @param data            Barcode data to send to the printer
     * @param errorCorrection Error correction level
     * @param moduleSize      Module size
     * @param alignment       Alignment
     * @param model           QR Code Model
     */
    public void doPrintQrcode(String data, ErrorCorrection errorCorrection, ModuleSize moduleSize, PrintAlignment alignment, QrModel model) {
        writeLog(getString(R.string.print_qrcode), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printQRcode(
                    data,
                    errorCorrection,
                    moduleSize,
                    alignment,
                    model
            );

            msg = getString(R.string.print_qrcode) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_qrcode) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_qrcode), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printDataMatrix] button.
     *
     * @param data             Barcode data to send to the printer
     * @param dataMatrixModule The number of Data Matrix modules
     * @param moduleSize       Module size
     * @param alignment        Alignment
     */
    public void doPrintDataMatrix(String data, DataMatrixModule dataMatrixModule, ModuleSize moduleSize, PrintAlignment alignment) {
        writeLog(getString(R.string.print_datamatrix), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printDataMatrix(
                    data,
                    dataMatrixModule,
                    moduleSize,
                    alignment
            );

            msg = getString(R.string.print_datamatrix) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_datamatrix) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_datamatrix), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printMaxiCode] button.
     *
     * @param data         Barcode data to send to the printer
     * @param maxiCodeMode MaxiCode mode
     * @param alignment    Alignment
     */
    public void doPrintMaxiCode(String data, MaxiCodeMode maxiCodeMode, PrintAlignment alignment) {
        writeLog(getString(R.string.print_maxicode), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printMaxiCode(
                    data,
                    maxiCodeMode,
                    alignment
            );

            msg = getString(R.string.print_maxicode) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_maxicode) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_maxicode), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printGS1DataBarStacked] button.
     *
     * @param data       Barcode data to send to the printer
     * @param moduleSize Module size
     * @param alignment  Alignment
     */
    protected void doPrintGs1Stacked(String data, ModuleSize moduleSize, PrintAlignment alignment) {
        writeLog(getString(R.string.print_gs1_stacked), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printGS1DataBarStacked(
                    data,
                    moduleSize,
                    alignment
            );
            msg = getString(R.string.print_gs1_stacked) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_gs1_stacked) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_gs1_stacked), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printGS1DataBarStackedOmnidirectional] button.
     *
     * @param data         Barcode data to send to the printer
     * @param moduleHeight Barcode module height (the number of the modules)
     * @param moduleSize   Module size
     * @param alignment    Alignment
     */
    public void doPrintGs1StackedOmnidirectional(String data, int moduleHeight, ModuleSize moduleSize, PrintAlignment alignment) {
        writeLog(getString(R.string.print_gs1_stackedomnidirectional), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printGS1DataBarStackedOmnidirectional(
                    data,
                    moduleHeight,
                    moduleSize,
                    alignment
            );
            msg = getString(R.string.print_gs1_stackedomnidirectional) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_gs1_stackedomnidirectional) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_gs1_stackedomnidirectional), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printGS1DatabarExpandedStacked] button.
     *
     * @param data       Barcode data to send to the printer
     * @param column     The number of columns
     * @param moduleSize Module size
     * @param alignment  Alignment
     */
    public void doPrintGs1ExpandedStacked(String data, int column, ModuleSize moduleSize, PrintAlignment alignment) {
        writeLog(getString(R.string.print_gs1_expandedstacked), WRITE_LOG_IN, data);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printGS1DataBarExpandedStacked(
                    data,
                    column,
                    moduleSize,
                    alignment
            );
            msg = getString(R.string.print_gs1_expandedstacked) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_gs1_expandedstacked) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_gs1_expandedstacked), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [cutPaper] button.
     *
     * @param cuttingMethod Cutting method
     * @param cutTypeItem   For log output
     */
    public void doCutPaper(CuttingMethod cuttingMethod, String cutTypeItem) {
        writeLog(getString(R.string.cut_paper), WRITE_LOG_IN, cutTypeItem);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.cutPaper(cuttingMethod);

            msg = getString(R.string.cut_paper) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.cut_paper) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.cut_paper), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [openDrawer] button.
     *
     * @param drawerNum      Drawer number
     * @param pulseWidth     pulse width
     * @param drawerNumItem  For log output
     * @param pulseWidthItem For log output
     */
    public void doOpenDrawer(DrawerNum drawerNum, PulseWidth pulseWidth, String drawerNumItem, String pulseWidthItem) {
        writeLog(getString(R.string.open_drawer), WRITE_LOG_IN, getString(R.string.msg_drawer, drawerNumItem, pulseWidthItem));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.openDrawer(drawerNum, pulseWidth);

            msg = getString(R.string.open_drawer) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.open_drawer) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.open_drawer), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [buzzer] button.
     *
     * @param onTime  Buzzer On time (millisecond)
     * @param offTime Buzzer Off time (millisecond)
     */
    public void doBuzzer(int onTime, int offTime) {
        writeLog(getString(R.string.buzzer), WRITE_LOG_IN, getString(R.string.msg_buzzer, Integer.toString(onTime), Integer.toString(offTime)));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.buzzer(onTime, offTime);

            msg = getString(R.string.buzzer) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.buzzer) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.buzzer), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [externalBuzzer] button.
     *
     * @param buzzerPattern Buzzer pattern
     * @param buzzerCount   Buzzer count
     */
    public void doExternalBuzzer(BuzzerPattern buzzerPattern, int buzzerCount) {
        writeLog(getString(R.string.externalBuzzer), WRITE_LOG_IN, getString(R.string.msg_buzzer, Integer.toString(buzzerPattern.getValue()), Integer.toString(buzzerCount)));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.externalBuzzer(buzzerPattern, buzzerCount);

            msg = getString(R.string.externalBuzzer) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.externalBuzzer) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.externalBuzzer), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [sendBinary] button.
     *
     * @param binaryText Binary data to send to the printer
     */
    public void doSendBinary(String binaryText) {
        writeLog(getString(R.string.send_binary), WRITE_LOG_IN, binaryText);

        int ret = 0;
        String msg;
        byte[] binary = asByteArray(binaryText);
        try {
            mPrinterManager.sendBinary(binary);
            msg = getString(R.string.send_binary) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.send_binary) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.send_binary), WRITE_LOG_OUT, ret);

    }


    /**
     * Implementation when pressing the [sendDataFile] button.
     */
    private void doSendDataFile() {
        writeLog(getString(R.string.send_data_file), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.sendDataFile(mSelectPath, Dithering.DITHERING_ERRORDIFFUSION);
            msg = getString(R.string.send_data_file) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.send_data_file) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.send_data_file), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [sendDataFile] button.
     *
     * @param alignment Alignment
     */
    public void doSendDataFile(PrintAlignment alignment) {
        writeLog(getString(R.string.send_data_file), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.sendDataFile(mSelectPath, alignment, Dithering.DITHERING_ERRORDIFFUSION);
            msg = getString(R.string.send_data_file) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.send_data_file) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.send_data_file), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [getPrinterResponse] button.
     *
     * @param id Response type constant
     */
    public void doGetPrinterResponse(int id) {
        writeLog(getString(R.string.get_printer_response), WRITE_LOG_IN);

        if (id == PrinterManager.PRINTER_RESPONSE_KEY_CODE) {
            ArrayList<String> buf = new ArrayList<String>();

            int ret = 0;
            String msg;
            StringBuffer keyCode = new StringBuffer(256);
            try {
                mPrinterManager.getPrinterResponse(id, buf);
                int size = buf.size();
                for (int index = 0; index < size; index++) {
                    if (index != 0) {
                        keyCode.append(",");
                    }
                    keyCode.append(buf.get(index));
                }

                msg = getString(R.string.get_printer_response) + getString(R.string.msg_ok) + getString(R.string.msg_get_printer_response_key, keyCode.toString());
            } catch (PrinterException e) {
                ret = e.getErrorCode();
                msg = getString(R.string.get_printer_response) + getString(R.string.msg_ng, ret);
            }
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            writeLog(getString(R.string.get_printer_response), WRITE_LOG_OUT, ret, getString(R.string.msg_get_printer_response_key, keyCode.toString()));

        } else {
            int[] buf = new int[1];
            if (id == PrinterManager.PRINTER_RESPONSE_REQUEST) {
                buf[0] = RESPONSE_REQUEST_CODE;
            }
            int ret = 0;
            String msg;
            try {
                mPrinterManager.getPrinterResponse(id, buf);
                msg = getString(R.string.get_printer_response) + getString(R.string.msg_ok) + getString(R.string.msg_get_printer_response, buf[0]);
            } catch (PrinterException e) {
                ret = e.getErrorCode();
                msg = getString(R.string.get_printer_response) + getString(R.string.msg_ng, ret);
            }
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            writeLog(getString(R.string.get_printer_response), WRITE_LOG_OUT, ret, getString(R.string.msg_get_printer_response, buf[0]));
        }

    }


    /**
     * Implementation when pressing the [registerLogo] button.
     *
     * @param logoID Logo ID to register (key code)
     */
    public void doRegisterLogoID1(int logoID) {
        writeLog(getString(R.string.register_logo), WRITE_LOG_IN, Integer.toString(logoID));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerLogo(mSelectPath, logoID, Dithering.DITHERING_ERRORDIFFUSION);
            msg = getString(R.string.register_logo) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_logo) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_logo), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [registerLogo] button.
     *
     * @param logoID Logo ID to register (key code)
     */
    public void doRegisterLogoID2(String logoID) {
        writeLog(getString(R.string.register_logo), WRITE_LOG_IN, 0, logoID);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerLogo(mSelectPath, logoID, Dithering.DITHERING_ERRORDIFFUSION);
            msg = getString(R.string.register_logo) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_logo) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_logo), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printLogo] button.
     *
     * @param logoID Logo ID to print (key code)
     */
    public void doPrintLogoID1(int logoID) {
        writeLog(getString(R.string.print_logo), WRITE_LOG_IN, 0, Integer.toString(logoID));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printLogo(logoID);
            msg = getString(R.string.print_logo) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_logo) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_logo), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [printLogo] button.
     *
     * @param logoID Logo ID to print (key code)
     */
    public void doPrintLogoID2(String logoID, PrintAlignment alignment) {
        writeLog(getString(R.string.print_logo), WRITE_LOG_IN, 0, logoID);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.printLogo(logoID, alignment);

            msg = getString(R.string.print_logo) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_logo) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.print_logo), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [unregisterLogo] button.
     *
     * @param logoID Logo ID to delete (key code)
     */
    public void doUnregisterLogoID1(int logoID) {
        writeLog(getString(R.string.unregister_logo), WRITE_LOG_IN, 0, Integer.toString(logoID));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.unregisterLogo(logoID);
            msg = getString(R.string.unregister_logo) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.unregister_logo) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.unregister_logo), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [unregisterLogo] button.
     *
     * @param logoID Logo ID to delete (key code)
     */
    public void doUnregisterLogoID2(String logoID) {
        writeLog(getString(R.string.unregister_logo), WRITE_LOG_IN, logoID);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.unregisterLogo(logoID);
            msg = getString(R.string.unregister_logo) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.unregister_logo) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.unregister_logo), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [registerStyleSheet] button.
     *
     * @param styleSheetNo Style sheet number to register
     */
    public void doRegisterStyleSheet(int styleSheetNo) {
        writeLog(getString(R.string.register_style_sheet), WRITE_LOG_IN, 0, Integer.toString(styleSheetNo));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerStyleSheet(mSelectPath, styleSheetNo);
            msg = getString(R.string.register_style_sheet) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_style_sheet) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_style_sheet), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [unregisterStyleSheet] button.
     *
     * @param styleSheetNo Style sheet number to delete
     */
    public void doUnregisterStyleSheet(int styleSheetNo) {
        writeLog(getString(R.string.unregister_style_sheet), WRITE_LOG_IN, Integer.toString(styleSheetNo));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.unregisterStyleSheet(styleSheetNo);
            msg = getString(R.string.unregister_style_sheet) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.unregister_style_sheet) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.unregister_style_sheet), WRITE_LOG_OUT, ret);
    }

    /**
     * Implementation when pressing the [printPageMode] button.
     */
    protected void doPrintPageMode() {
        writeLog(getString(R.string.print_page_mode), WRITE_LOG_IN);
        int ret = 0;
        String msg;
        String fileName = "TicketImage.jpg";
        String path = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();

        try {
            InputStream in = getAssets().open(fileName);
            try {
                File file = new File(path, fileName);
                FileOutputStream out = new FileOutputStream(file.getPath());
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();

            } catch (IOException e) {

            }
            in.close();
        } catch (IOException e) {

        }

        try {
            mPrinterManager.enterPageMode();
            mPrinterManager.setPageModeArea(0, 0, 576, 355);
            mPrinterManager.printPageModeRectangle(0, 0, 575, 344, LineStyle.LINESTYLE_THIN);
            mPrinterManager.printPageModeRectangle(7, 7, 567, 336, LineStyle.LINESTYLE_THIN);
            mPrinterManager.printPageModeLine(404, 11, 404, 334, LineStyle.LINESTYLE_THIN);
            mPrinterManager.printPageModeText(21, 37, "NO.123456789");
            mPrinterManager.printPageModeText(212, 330, "Date 2020-01-01");
            mPrinterManager.printPageModeImageFile(
                    10,
                    212,
                    path + "/" + fileName,
                    Dithering.DITHERING_DISABLE);
            mPrinterManager.setPageModeArea(404, 0, 163, 345);
            mPrinterManager.setPageModeDirection(Direction.DIRECTION_BOTTOM_TO_TOP);
            mPrinterManager.printPageModeBarcode(
                    20,
                    132,
                    BarcodeSymbol.BARCODE_SYMBOL_CODE128,
                    new byte[]{0x67, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x68},
                    ModuleSize.BARCODE_MODULE_WIDTH_2,
                    80,
                    HriPosition.HRI_POSITION_ABOVE,
                    CharacterFont.FONT_A);
            mPrinterManager.printPageMode(CuttingMethod.CUT_PARTIAL);
            mPrinterManager.exitPageMode();
            msg = getString(R.string.print_page_mode) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.print_page_mode) + getString(R.string.msg_ng, ret);
        }

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        writeLog(getString(R.string.print_page_mode), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [setBarcodeScannerListener] button.
     *
     * @param notationMethod Notation method
     */
    public void doSetBarcodeScannerListener(String notationMethod) {
        writeLog(getString(R.string.set_barcode_scanner_listener), WRITE_LOG_IN, notationMethod);

        int ret = 0;
        String msg;
        try {
            if (notationMethod.equals("NOTATION_STRING")) {
                mIsStringDisplay = true;
            } else {
                mIsStringDisplay = false;
            }
            mBarcodeScannerListener = SetupPrinterActivity.this;
            mPrinterManager.setBarcodeScannerListener(mBarcodeScannerListener);
            msg = getString(R.string.set_barcode_scanner_listener) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_barcode_scanner_listener) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_barcode_scanner_listener), WRITE_LOG_OUT, ret);
    }


    /**
     * Implementation when pressing the [showTemplate] button.
     *
     * @param time_ms show template time
     */
    public void doShowTemplate(int time_ms) {
        writeLog(getString(R.string.show_template), WRITE_LOG_IN, Integer.toString(time_ms));

        int ret = 0;
        String msg;
        try {
            mPrinterManager.showTemplate(time_ms);
            msg = getString(R.string.show_template) + getString(R.string.msg_ok);
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.show_template) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.show_template), WRITE_LOG_OUT, ret);
    }

    public void doShowSlide(int slideID, int time_ms) {
        writeLog(getString(R.string.show_slide), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.showSlide(slideID, time_ms);
            msg = getString(R.string.show_slide) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.show_slide) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.show_slide), WRITE_LOG_OUT, ret);
    }

    public void doExecuteMacro(int macroID, int repeatCount) {
        writeLog(getString(R.string.execute_macro), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.executeMacro(macroID, repeatCount);
            msg = getString(R.string.execute_macro) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.execute_macro) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.execute_macro), WRITE_LOG_OUT, ret);
    }

    public void doTurnOnScreen(boolean isOn) {
        writeLog(getString(R.string.turn_on_screen), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.turnOnScreen(isOn);
            msg = getString(R.string.turn_on_screen) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.turn_on_screen) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.turn_on_screen), WRITE_LOG_OUT, ret);
    }

    public void doSelectTemplate(int templateID, int slideID) {
        writeLog(getString(R.string.select_template), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.selectTemplate(templateID, slideID);
            msg = getString(R.string.select_template) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.select_template) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.select_template), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateImageData(int mapID, int imageID) {
        writeLog(getString(R.string.set_template_image_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateImageData(mapID, imageID);
            msg = getString(R.string.set_template_image_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_image_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_image_data), WRITE_LOG_OUT, ret);
    }

    public void doSelectTemplateTextObject(int mapID) {
        writeLog(getString(R.string.select_template_text_object), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.selectTemplateTextObject(mapID);
            msg = getString(R.string.select_template_text_object) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.select_template_text_object) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.select_template_text_object), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextAlignment(PrintAlignment alignment) {
        writeLog(getString(R.string.set_template_text_alignment), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextAlignment(alignment);
            msg = getString(R.string.set_template_text_alignment) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_alignment) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_alignment), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextData(String text) {
        writeLog(getString(R.string.set_template_text_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextData(text);
            msg = getString(R.string.set_template_text_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_data), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextLeftMargin(int margin) {
        writeLog(getString(R.string.set_template_text_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextLeftMargin(margin);
            msg = getString(R.string.set_template_text_left_margin) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_left_margin) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_left_margin), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextLineSpacing(int spacing) {
        writeLog(getString(R.string.set_template_text_line_spacing), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextLineSpacing(spacing);
            msg = getString(R.string.set_template_text_line_spacing) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_line_spacing) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_line_spacing), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextBold(CharacterBold bold) {
        writeLog(getString(R.string.set_template_text_bold), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextBold(bold);
            msg = getString(R.string.set_template_text_bold) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_bold) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_bold), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextUnderline(CharacterUnderline underline) {
        writeLog(getString(R.string.set_template_text_underline), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextUnderline(underline);
            msg = getString(R.string.set_template_text_underline) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_underline) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_underline), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextSize(CharacterScale scale) {
        writeLog(getString(R.string.set_template_text_size), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextSize(scale);
            msg = getString(R.string.set_template_text_size) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_size) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_size), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextFont(CharacterFont font) {
        writeLog(getString(R.string.set_template_text_font), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextFont(font);
            msg = getString(R.string.set_template_text_font) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_font) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_font), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextRegisteredFont(RegisteredFont font) {
        writeLog(getString(R.string.set_template_text_registered_font), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextRegisteredFont(font);
            msg = getString(R.string.set_template_text_registered_font) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_registered_font) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_registered_font), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextRightSpacing(int spacing) {
        writeLog(getString(R.string.set_template_text_right_spacing), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextRightSpacing(spacing);
            msg = getString(R.string.set_template_text_right_spacing) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_right_spacing) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_right_spacing), WRITE_LOG_OUT, ret);
    }

    public void doSetTemplateTextColor(int color) {
        writeLog(getString(R.string.set_template_text_color), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateTextColor(color);
            msg = getString(R.string.set_template_text_color) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_text_color) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_text_color), WRITE_LOG_OUT, ret);
    }

    protected void doSetTemplateBarcodeData(int mapID, String text) {
        writeLog(getString(R.string.set_template_barcode_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateBarcodeData(mapID, text);
            msg = getString(R.string.set_template_barcode_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_barcode_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_barcode_data), WRITE_LOG_OUT, ret);
    }

    protected void doSetTemplateQrcodeData(int mapID, ModuleSize moduleSize, ErrorCorrection errorCorrection, QrDataMode mode, QrQuietZone quietZone, String text) {
        writeLog(getString(R.string.set_template_qrcode_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.setTemplateQrCodeData(mapID, moduleSize, errorCorrection, mode, quietZone, text);
            msg = getString(R.string.set_template_qrcode_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.set_template_qrcode_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.set_template_qrcode_data), WRITE_LOG_OUT, ret);
    }

    protected void doRegisterTemplate(int templateID, String label) {
        writeLog(getString(R.string.register_template), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerTemplate(templateID, label, mSelectPath);
            msg = getString(R.string.register_template) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_template) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_template), WRITE_LOG_OUT, ret);
    }

    protected void doUnregisterTemplate(int templateID) {
        writeLog(getString(R.string.unregister_template), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.unregisterTemplate(templateID);
            msg = getString(R.string.unregister_template) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.unregister_template) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.unregister_template), WRITE_LOG_OUT, ret);
    }

    protected void doRegisterImageData(int imageID, String label) {
        writeLog(getString(R.string.register_image_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerImageData(imageID, label, mSelectPath);
            msg = getString(R.string.register_image_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_image_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_image_data), WRITE_LOG_OUT, ret);
    }

    protected void doUnregisterImageData(int imageID) {
        writeLog(getString(R.string.unregister_image_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.unregisterImageData(imageID);
            msg = getString(R.string.unregister_image_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.unregister_image_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.unregister_image_data), WRITE_LOG_OUT, ret);
    }

    protected void doRegisterSlideData(int slideID, String label) {
        writeLog(getString(R.string.register_slide_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerSlideData(slideID, label, mSelectPath);
            msg = getString(R.string.register_slide_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_slide_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_slide_data), WRITE_LOG_OUT, ret);
    }

    protected void doUnregisterSlideData(int slideID) {
        writeLog(getString(R.string.unregister_slide_data), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.unregisterSlideData(slideID);
            msg = getString(R.string.unregister_slide_data) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.unregister_slide_data) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.unregister_slide_data), WRITE_LOG_OUT, ret);
    }

    protected void doRegisterUserDefinedCharacter() {
        writeLog(getString(R.string.register_user_defined_character), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerUserDefinedCharacter(mSelectPath);
            msg = getString(R.string.register_user_defined_character) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_user_defined_character) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_user_defined_character), WRITE_LOG_OUT, ret);
    }

    protected void doRegisterOptionFont(int startCode, int endCode, int width, int height) {
        writeLog(getString(R.string.register_option_font), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.registerOptionFont(startCode, endCode, width, height, mSelectPath);
            msg = getString(R.string.register_option_font) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.register_option_font) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.register_option_font), WRITE_LOG_OUT, ret);

    }

    public void doControlMacroRegistration(int macroID, MacroRegistrationFunction control) {
        writeLog(getString(R.string.control_macro_registration), WRITE_LOG_IN);

        int ret = 0;
        String msg;
        try {
            mPrinterManager.controlMacroRegistration(macroID, control);
            msg = getString(R.string.control_macro_registration) + getString(R.string.msg_ok);

        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.control_macro_registration) + getString(R.string.msg_ng, ret);

        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.control_macro_registration), WRITE_LOG_OUT, ret);

    }

    public void doGetDisplayResponse(int responseID, int param) {
        writeLog(getString(R.string.get_display_response), WRITE_LOG_IN);

        int ret = 0;
        String msg = new String();
        try {
            int[] buf;
            ArrayList<Integer> list;
            ArrayList<String> label;
            switch (responseID) {
                case PrinterManager.DISPLAY_RESPONSE_REQUEST:
                    buf = new int[]{RESPONSE_REQUEST_CODE};
                    mPrinterManager.getDisplayResponse(responseID, param, buf);
                    msg = getString(R.string.get_display_response) + getString(R.string.msg_ok) + getString(R.string.msg_get_printer_response, buf[0]);
                    break;
                case PrinterManager.DISPLAY_RESPONSE_USER_AREA:
                    buf = new int[1];
                    mPrinterManager.getDisplayResponse(responseID, param, buf);
                    msg = getString(R.string.get_display_response) + getString(R.string.msg_ok) + getString(R.string.msg_get_printer_response, buf[0]);
                    break;
                case PrinterManager.DISPLAY_RESPONSE_TEMPLATE_ID_LIST:
                case PrinterManager.DISPLAY_RESPONSE_SLIDE_ID_LIST:
                case PrinterManager.DISPLAY_RESPONSE_IMAGE_ID_LIST:
                    list = new ArrayList<Integer>();
                    mPrinterManager.getDisplayResponse(responseID, param, list);
                    StringBuilder stringBuilder = new StringBuilder();
                    int size = list.size();
                    for (int index = 0; index < size; index++) {
                        if (index != 0) {
                            stringBuilder.append(",");
                        }
                        int num = list.get(index);
                        String str = String.valueOf(num);
                        stringBuilder.append(str);
                    }
                    msg = getString(R.string.get_printer_response) + getString(R.string.msg_ok) + getString(R.string.msg_get_printer_response_key, stringBuilder.toString());
                    break;
                case PrinterManager.DISPLAY_RESPONSE_TEMPLATE_LABEL:
                case PrinterManager.DISPLAY_RESPONSE_SLIDE_LABEL:
                case PrinterManager.DISPLAY_RESPONSE_IMAGE_LABEL:
                    label = new ArrayList<String>();
                    mPrinterManager.getDisplayResponse(responseID, param, label);
                    msg = getString(R.string.get_display_response) + getString(R.string.msg_ok) + getString(R.string.msg_get_display_response_label, label.get(0));
                    break;
                default:
            }
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            msg = getString(R.string.get_display_response) + getString(R.string.msg_ng, ret);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        writeLog(getString(R.string.get_display_response), WRITE_LOG_OUT, ret, msg);
    }


    /**
     * Display an alert dialog.
     *
     * @param dialogId Dialog Id
     */
    private void showAlertDialog(int dialogId) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.ID, dialogId);
        args.putInt(AlertDialogFragment.MODEL, mPrinterManager.getPrinterModel());
        alertDialogFragment.setArguments(args);
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.add(alertDialogFragment, AlertDialogFragment.DIALOG);
        ft.commitAllowingStateLoss();
    }

    /**
     * File selection dialog of Open menu.
     *
     * @param dialogId Dialog Id
     */
    private void showSelectFileDialog(int dialogId) {
//        android.app.DialogFragment dialogFragment = new FileSelectDialogFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(FileSelectDialogFragment.ROOT_DIRECTORY, getString(R.string.file_select_directory));
//        bundle.putString(FileSelectDialogFragment.INITIAL_DIRECTORY, Environment.getExternalStorageDirectory().getPath());
//        bundle.putString(FileSelectDialogFragment.PREVIOUS, getString(R.string.file_select_previews_directory));
//        bundle.putInt(FileSelectDialogFragment.ID, dialogId);
//        dialogFragment.setArguments(bundle);
//        dialogFragment.setCancelable(true);
//        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
//        ft.add(dialogFragment, FileSelectDialogFragment.DIALOG);
//        ft.commitAllowingStateLoss();
    }

    /**
     * Implementation when file is selected.
     *
     * @param path     Selected file
     * @param dialogId Dialog Id
     */
    protected void onFileSelected(String path, int dialogId) {
        if (path == null) {
            //not selected
        } else {
            mSelectPath = path;

            switch (dialogId) {
                case DIALOG_SEND_DATA_FILE:
                    int printerModel = mPrinterManager.getPrinterModel();
                    if ((printerModel == PrinterManager.PRINTER_MODEL_DPU_S245)
                            || (printerModel == PrinterManager.PRINTER_MODEL_DPU_S445)) {
                        doSendDataFile();
                    } else {
                        showAlertDialog(DIALOG_SEND_DATA_FILE);
                    }
                    break;
                case DIALOG_REGISTER_LOGO_ID1:
                    showAlertDialog(DIALOG_REGISTER_LOGO_ID1);
                    break;
                case DIALOG_REGISTER_LOGO_ID2:
                    showAlertDialog(DIALOG_REGISTER_LOGO_ID2);
                    break;
                case DIALOG_REGISTER_STYLE_SHEET_NO:
                    showAlertDialog(DIALOG_REGISTER_STYLE_SHEET_NO);
                    break;
                case DIALOG_REGISTER_TEMPLATE:
                    showAlertDialog(DIALOG_REGISTER_TEMPLATE);
                    break;
                case DIALOG_REGISTER_IMAGE_DATA:
                    showAlertDialog(DIALOG_REGISTER_IMAGE_DATA);
                    break;
                case DIALOG_REGISTER_SLIDE_DATA:
                    showAlertDialog(DIALOG_REGISTER_SLIDE_DATA);
                    break;
                case DIALOG_REGISTER_USER_DEFINED_CHARACTER:
                    doRegisterUserDefinedCharacter();
                    break;
                case DIALOG_REGISTER_OPTION_FONT:
                    showAlertDialog(DIALOG_REGISTER_OPTION_FONT);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * After that, implementation of private methods
     */

    private byte[] asByteArray(String hex) {
        byte[] bytes = new byte[hex.length() / 2];

        try {
            for (int index = 0; index < bytes.length; index++) {
                String byteStr = hex.substring(index * 2, (index + 1) * 2);
                bytes[index] = (byte) Integer.parseInt(byteStr, 16);
            }
        } catch (IndexOutOfBoundsException e) {
        } catch (NumberFormatException e) {
        }

        return bytes;
    }

    private boolean checkBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showAlertDialog(DIALOG_BLUETOOTH_NO_SUPPORT);
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
            return false;
        }
        return true;
    }

    private boolean checkWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return false;
        }

        if (wifiManager.getWifiState() != wifiManager.WIFI_STATE_ENABLED) {
            showAlertDialog(DIALOG_ENABLE_WIFI);
            return false;
        }
        return true;
    }

    public void enableWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
            this.startActivityForResult(panelIntent, REQUEST_CODE_WIFI);
        } else if (wifiManager != null && wifiManager.setWifiEnabled(true)) {
            Toast.makeText(getApplicationContext(), R.string.enabled_wifi, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.faled_to_enable_wifi, Toast.LENGTH_LONG).show();
        }
    }

    private void setProperty() {
        if (mPrinterManager != null) {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            int sendTimeout = PrinterUtil.getInt(pref.getString(getString(R.string.key_send_timeout), getString(R.string.send_timeout_default)));
            mPrinterManager.setSendTimeout(sendTimeout);

            int receiveTimeout = PrinterUtil.getInt(pref.getString(getString(R.string.key_receive_timeout), getString(R.string.receive_timeout_default)));
            mPrinterManager.setReceiveTimeout(receiveTimeout);

            int socketKeepingTime = PrinterUtil.getInt(pref.getString(getString(R.string.key_socket_keeping_time), getString(R.string.socket_keeping_time_default)));
            mPrinterManager.setSocketKeepingTime(socketKeepingTime);

            int internationalCharacter;
            int codePage;
            if (Locale.JAPAN.equals(Locale.getDefault())) {
                internationalCharacter = PrinterUtil.getInt(pref.getString(getString(R.string.key_international_character), getString(R.string.international_character_default_jp_values)));
                codePage = PrinterUtil.getInt(pref.getString(getString(R.string.key_code_page), getString(R.string.code_page_default_jp_values)));
            } else {
                internationalCharacter = PrinterUtil.getInt(pref.getString(getString(R.string.key_international_character), getString(R.string.international_character_default_en_values)));
                codePage = PrinterUtil.getInt(pref.getString(getString(R.string.key_code_page), getString(R.string.code_page_default_en_values)));
            }
            mPrinterManager.setInternationalCharacter(internationalCharacter);
            mPrinterManager.setCodePage(codePage);
        }
    }

    private void writeLog(String command, boolean start) {
        writeLog(command, start, 0);
    }

    private void writeLog(String command, boolean start, String msg) {
        writeLog(command, start, 0, msg);
    }

    private void writeLog(String command, boolean start, int returnCode) {
        writeLog(command, start, returnCode, "");
    }

    private void writeLog(String command, boolean start, int returnCode, String msg) {
        EditText edtLog = (EditText) findViewById(R.id.edittext_log);
        if (edtLog != null) {
            StringBuffer buf = new StringBuffer(128);
//            buf.append(getString(R.string.write_log_bracket, PrinterUtil.getDateString(getString(R.string.write_log_date))));

            buf.append(getString(R.string.write_log_command, command));
            if (start) {
                buf.append(getString(R.string.write_log_in));
            } else {
                buf.append(getString(R.string.write_log_out));
                if (returnCode != 0) {
                    buf.append(getString(R.string.write_log_result, returnCode));
                }
            }

            if (!PrinterUtil.isEmpty(msg)) {
                buf.append(getString(R.string.write_log_msg, msg));
            }

            buf.append(getString(R.string.write_log_end));

            edtLog.append(buf.toString());
        }
    }

    private void checkLocationRequest() {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(SetupPrinterActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                    Intent intent = new Intent(SetupPrinterActivity.this, DeviceActivity.class);
                    intent.putExtra(DeviceActivity.TYPE, mSelectPort);
                    startActivityForResult(intent, REQUEST_SELECT_DEVICE);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Request for location information setting
                                ((ResolvableApiException) exception).startResolutionForResult(SetupPrinterActivity.this, REQUEST_LOCATION_SETTING_RESOLUTION);
                                return;
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }
}
