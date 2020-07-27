package module_android_demo.example.com.demo_printer.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rscja.deviceapi.Printer;
import com.rscja.deviceapi.exception.PrinterBarcodeInvalidException;

import java.util.HashMap;
import java.util.Map;

import module_android_demo.example.com.demo_printer.MainActivity;
import module_android_demo.example.com.demo_printer.R;

import static com.rscja.deviceapi.Printer.BarcodeType.UPC_A;


public class PrinterBarcodeFragment extends Fragment implements View.OnClickListener {

    MainActivity mContext;
    Button btnPrintBarcode;
    Spinner spBarcodeType, spBarcodePosition, spBarcodeWidth;
    EditText editPrintBarcodeData, etHeight;
    TextView tvMsg, tvMsg2;
    ImageView iv2D;
    int len = 17;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_printer_barcode, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        btnPrintBarcode = (Button) mContext.findViewById(R.id.btnPrintBarcode);
        spBarcodeType = (Spinner) mContext.findViewById(R.id.spBarcodeType);
        spBarcodePosition = (Spinner) mContext.findViewById(R.id.spBarcodePosition);
        editPrintBarcodeData = (EditText) mContext.findViewById(R.id.editPrintBarcodeData);
        etHeight = (EditText) mContext.findViewById(R.id.etHeight);
        spBarcodeWidth = (Spinner) mContext.findViewById(R.id.spBarcodeWidth);
        tvMsg = (TextView) mContext.findViewById(R.id.tvMsg);
        tvMsg2 = (TextView) mContext.findViewById(R.id.tvMsg2);
        iv2D = (ImageView) mContext.findViewById(R.id.iv2D);
        btnPrintBarcode.setOnClickListener(this);
        spBarcodeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setMsg(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spBarcodeWidth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setMsg2(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        etHeight.setText(mContext.barcodeH + "");
        spBarcodeWidth.setSelection(mContext.barcodeW);
        spBarcodePosition.setSelection(mContext.barcodePosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrintBarcode:
                String bar = editPrintBarcodeData.getText().toString();
                if (bar == null || bar.isEmpty()) {
                    Toast.makeText(mContext, getResources().getString(R.string.print_notBarcode), Toast.LENGTH_SHORT).show();
                    return;
                }
                printBarcode(bar, spBarcodeType.getSelectedItemPosition());
                break;
        }
    }

    private void printBarcode(String barcode, int type) {
        Printer.BarcodeType barcodeType = null;
        switch (type) {
            case 0:
                barcodeType = UPC_A;
                break;
            case 1:
                barcodeType = Printer.BarcodeType.UPC_E;
                break;
            case 2:
                barcodeType = Printer.BarcodeType.JAN13_EAN13;
                break;
            case 3:
                barcodeType = Printer.BarcodeType.JAN8_EAN8;
                break;
            case 4:
                barcodeType = Printer.BarcodeType.CODE39;
                break;
            case 5:
                barcodeType = Printer.BarcodeType.ITF_Interleaved_2_of_5;
                break;
            case 6:
                barcodeType = Printer.BarcodeType.CODABAR_NW_7;
                break;
            case 7:
                barcodeType = Printer.BarcodeType.CODE93;
                break;
            case 8:
                barcodeType = Printer.BarcodeType.CODE128;
                break;
            case 9:
                barcodeType = Printer.BarcodeType.UCC_EAN128;
                break;
            case 10:
                Bitmap bitmap = generateBitmap(barcode, 320, 320);
                iv2D.setImageBitmap(bitmap);
                break;
        }
        if (barcodeType != null) {
            if (setBarcodeHW()) {
                if (barcode.length() > len) {
                    Toast.makeText(mContext, getResources().getString(R.string.print_7), Toast.LENGTH_LONG).show();
                    return;
                }
                new PrintTask(null, barcode, barcodeType).execute();
            }
        } else {
            Bitmap bitmap = ((BitmapDrawable) iv2D.getDrawable()).getBitmap();
            if (bitmap != null) {
                bitmap = mContext.compressBitmap(bitmap);
                new PrintTask(bitmap, "", null).execute();
            }
        }
    }

    private boolean setBarcodeHW() {
        String h = etHeight.getText().toString();
        if (h == null || h.isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string.printNotEmpty), Toast.LENGTH_SHORT).show();
            return false;
        }
        int height = Integer.parseInt(h);
        if (height > 255 || height < 1) {
            Toast.makeText(mContext, getResources().getString(R.string.printHeightTip), Toast.LENGTH_SHORT).show();
            return false;
        }

        mContext.mPrinter.setBarcodeHeight(height);
        mContext.mPrinter.setBarcodeWidth(spBarcodeWidth.getSelectedItemPosition() + 1);
        mContext.mPrinter.setBarcodeHRI(spBarcodePosition.getSelectedItemPosition());

        mContext.barcodeH = height;
        mContext.barcodeW = spBarcodeWidth.getSelectedItemPosition();
        mContext.barcodePosition = spBarcodePosition.getSelectedItemPosition();
        return true;
    }

    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setMsg(int position) {
        tvMsg2.setVisibility(View.VISIBLE);
        spBarcodePosition.setVisibility(View.VISIBLE);
        etHeight.setVisibility(View.VISIBLE);
        spBarcodeWidth.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                tvMsg.setText(getResources().getString(R.string.print_upca));
                break;
            case 1:
                tvMsg.setText(getResources().getString(R.string.print_upce));
                break;
            case 2:
                tvMsg.setText(getResources().getString(R.string.print_jan13));
                break;
            case 3:
                tvMsg.setText(getResources().getString(R.string.print_jan8));
                break;
            case 4:
                tvMsg.setText(getResources().getString(R.string.print_code39));
                break;
            case 5:
                tvMsg.setText(getResources().getString(R.string.print_itf));
                break;
            case 6:
                tvMsg.setText(getResources().getString(R.string.print_codabar));
                break;
            case 7:
                tvMsg.setText(getResources().getString(R.string.print_code93));
                break;
            case 8:
                tvMsg.setText(getResources().getString(R.string.print_code128));
                break;
            case 9:
                tvMsg.setText(getResources().getString(R.string.print_ucc));
                break;
            case 10:
                tvMsg.setText("");
                tvMsg2.setVisibility(View.GONE);
                spBarcodePosition.setVisibility(View.INVISIBLE);
                etHeight.setVisibility(View.INVISIBLE);
                spBarcodeWidth.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void setMsg2(int position) {
        switch (position) {
            case 0:
                tvMsg2.setText(getResources().getString(R.string.print_1));
                len = 38;
                break;
            case 1:
                tvMsg2.setText(getResources().getString(R.string.print_2));
                len = 17;
                break;
            case 2:
                tvMsg2.setText(getResources().getString(R.string.print_3));
                len = 10;
                break;
            case 3:
                tvMsg2.setText(getResources().getString(R.string.print_4));
                len = 5;
                break;
            case 4:
                tvMsg2.setText(getResources().getString(R.string.print_5));
                len = 4;
                break;
            case 5:
                tvMsg2.setText(getResources().getString(R.string.print_6));
                len = 1;
                break;
        }
    }

    private class PrintTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;
        Bitmap bitmap;
        String barcode;
        Printer.BarcodeType barcodeType;
        String msg = "";

        public PrintTask(Bitmap bitmap, String barcode, Printer.BarcodeType barcodeType) {
            this.bitmap = bitmap;
            this.barcode = barcode;
            this.barcodeType = barcodeType;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            if (bitmap != null) {
                mContext.printPicture(bitmap, 500);
            } else {
                try {
                    mContext.mPrinter.clearCache();//清空缓存区
                    mContext.mPrinter.print(barcode, barcodeType);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mContext.printFeed();
                } catch (PrinterBarcodeInvalidException e) {
                    msg = e.getMessage();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
            mypDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(mContext);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage(getResources().getString(R.string.printing));
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }

}
