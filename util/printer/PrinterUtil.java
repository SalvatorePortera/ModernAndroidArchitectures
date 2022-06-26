package com.nereus.craftbeer.util.printer;

import com.nereus.craftbeer.model.printer.PrintModel;
import com.seikoinstruments.sdk.thermalprinter.PrinterException;
import com.seikoinstruments.sdk.thermalprinter.PrinterManager;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterBold;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterFont;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterReverse;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterScale;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterUnderline;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CuttingMethod;
import com.seikoinstruments.sdk.thermalprinter.printerenum.PrintAlignment;
import com.seikoinstruments.sdk.thermalprinter.printerenum.TransactionFunction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static com.nereus.craftbeer.constant.Constants.RESPONSE_REQUEST_CODE;


/**
 * Utility of string class.
 */
public class PrinterUtil {

    /**
     * Check if it is empty.
     *
     * @param chsq CharSequence type.
     * @return true = null or empty.
     */
    public static boolean isEmpty(CharSequence chsq) {
        boolean result;
        if (chsq == null) {
            result = true;

        } else if (chsq.length() == 0) {
            result = true;

        } else {
            result = false;
        }

        return result;
    }

    /**
     * Check if it is empty.
     *
     * @param str String type.
     * @return true = null or empty.
     */
    public static boolean isEmpty(String str) {
        boolean result;
        if (str == null) {
            result = true;

        } else if (str.length() == 0) {
            result = true;

        } else {
            result = false;
        }

        return result;
    }

    public static int getInt(String str) {
        return getInt(str, 0);
    }

    /**
     * String -> int
     *
     * @param str        string
     * @param defaultVal default value
     * @return int value
     */
    public static int getInt(String str, int defaultVal) {
        int value;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            value = defaultVal;
        }

        return value;
    }

    /**
     * Get date string
     *
     * @param format date format
     * @return Date string
     */
    public static String getDateString(String format) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(now);
    }

    public static void printReceipt(PrinterManager mPrinterManager, String receiptTitle, String receiptTime, String receiptContent) {
        final String feed = "\n\n";
        final String receiptBarcode = "123456789123";
        final String receiptBarcode1 = "123456789124";
        final String receiptBarcode2 = "123456789125";
        final String receiptBarcode3 = "123456789126";
        final String receipt2DCode = "ABC0123";
        final int HEIGHT_BARCODE = 162;
        final int ROW_2DCODE = 0;
        final int COLUM_2DCODE = 0;
        final int HEIGHT_2DCODE = 10;
        Timber.i("--- start printing receipt");
        int ret = 0;
        String msg;
        try {
            mPrinterManager.controlTransaction(TransactionFunction.TRANSACTION_START);
            if (mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S245
                    && mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S445) {
                mPrinterManager.sendTextEx(receiptTitle, CharacterBold.BOLD, CharacterUnderline.UNDERLINE_CANCEL, CharacterReverse.REVERSE_CANCEL, CharacterFont.FONT_A, CharacterScale.VARTICAL_1_HORIZONTAL_2, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.sendTextEx(receiptTime + receiptContent, CharacterBold.BOLD_CANCEL, CharacterUnderline.UNDERLINE_CANCEL, CharacterReverse.REVERSE_CANCEL, CharacterFont.FONT_A, CharacterScale.VARTICAL_1_HORIZONTAL_1, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.sendText(feed);
          /*      mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_UPC_A, receiptBarcode, ModuleSize.BARCODE_MODULE_WIDTH_3, HEIGHT_BARCODE, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.printPDF417(receipt2DCode, ErrorCorrection.PDF417_ERROR_CORRECTION_0, ROW_2DCODE, COLUM_2DCODE, ModuleSize.PDF417_MODULE_WIDTH_3, HEIGHT_2DCODE, PrintAlignment.ALIGNMENT_CENTER);
*/
                mPrinterManager.cutPaper(CuttingMethod.CUT_PARTIAL);
            } else {
                mPrinterManager.sendTextEx(receiptTitle, CharacterBold.BOLD, CharacterUnderline.UNDERLINE_CANCEL, CharacterFont.FONT_A, CharacterScale.VARTICAL_1_HORIZONTAL_2);
                mPrinterManager.sendTextEx(receiptTime + receiptContent, CharacterBold.BOLD_CANCEL, CharacterUnderline.UNDERLINE_CANCEL, CharacterFont.FONT_A, CharacterScale.VARTICAL_1_HORIZONTAL_1);
                mPrinterManager.sendText(feed);
        /*        mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_UPC_A, receiptBarcode, ModuleSize.BARCODE_MODULE_WIDTH_3, HEIGHT_BARCODE, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_UPC_A, receiptBarcode1, ModuleSize.BARCODE_MODULE_WIDTH_3, HEIGHT_BARCODE, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_UPC_A, receiptBarcode2, ModuleSize.BARCODE_MODULE_WIDTH_3, HEIGHT_BARCODE, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_UPC_A, receiptBarcode3, ModuleSize.BARCODE_MODULE_WIDTH_3, HEIGHT_BARCODE, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.printPDF417(receipt2DCode, ErrorCorrection.PDF417_ERROR_CORRECTION_0, ROW_2DCODE, COLUM_2DCODE, ModuleSize.PDF417_MODULE_WIDTH_3, HEIGHT_2DCODE, PrintAlignment.ALIGNMENT_CENTER);
*/
            }
            mPrinterManager.controlTransaction(TransactionFunction.TRANSACTION_PRINT);
            mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, new int[]{RESPONSE_REQUEST_CODE});
        } catch (PrinterException e) {
            try {
                mPrinterManager.controlTransaction(TransactionFunction.TRANSACTION_CLEAR);
            } catch (PrinterException e1) {
            }
            Timber.e(e);
        }

        Timber.i("--- Finish printing receipt");
    }

    public static void printReceipt(PrinterManager mPrinterManager, List<PrintModel> lines) {
        final String feed = "\n\n";

        Timber.i("--- start printing receipt");
        mPrinterManager.setCodePage(PrinterManager.CODE_PAGE_KATAKANA);
        try {
            mPrinterManager.controlTransaction(TransactionFunction.TRANSACTION_START);
            if (mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S245
                    && mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S445) {
                for (PrintModel line : lines) {
                    mPrinterManager.sendTextEx(line.getContent(),
                            line.getBold(),
                            line.getUnderline(),
                            line.getReverse(),
                            line.getFont(),
                            line.getScale(),
                            line.getAlignment());
                }

                mPrinterManager.sendText(feed);
                mPrinterManager.cutPaper(CuttingMethod.CUT_PARTIAL);
            } else {
                for (PrintModel line : lines) {
                    mPrinterManager.sendTextEx(line.getContent(),
                            line.getBold(),
                            line.getUnderline(),
                            line.getFont(),
                            line.getScale());
                }
                mPrinterManager.sendText(feed);
            }
            mPrinterManager.controlTransaction(TransactionFunction.TRANSACTION_PRINT);
            mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, new int[]{RESPONSE_REQUEST_CODE});
        } catch (PrinterException e) {
            try {
                mPrinterManager.controlTransaction(TransactionFunction.TRANSACTION_CLEAR);
            } catch (PrinterException e1) {
            }
            Timber.e(e);
        }

        Timber.i("--- Finish printing receipt");
    }
}


