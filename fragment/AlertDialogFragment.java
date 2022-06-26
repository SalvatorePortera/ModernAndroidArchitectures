package com.nereus.craftbeer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.nereus.craftbeer.R;
import com.nereus.craftbeer.activity.SetupPrinterActivity;


/**
 * Alert dialog fragment class.
 */
public class AlertDialogFragment extends DialogFragment {

    public static final String DIALOG = "dialog";
    public static final String ID = "id";
    public static final String MODEL = "model";

    int dialogId;
    int printerModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialogId = getArguments().getInt(ID);
        printerModel = getArguments().getInt(MODEL);
        switch (dialogId) {
            case SetupPrinterActivity.DIALOG_SELECT_PRINTER_MODEL:
                dialog = createDialogSelectPrinterModel();
                break;
        }
        return dialog;
    }

    private Dialog createDialogSelectPrinterModel() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_connect_title)
                .setItems(R.array.printer_model_list,
                        (dialog, which) -> ((SetupPrinterActivity) getActivity()).doConnect(which)
                )
                .create();
    }
}