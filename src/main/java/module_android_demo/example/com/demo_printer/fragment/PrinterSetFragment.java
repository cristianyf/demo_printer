package module_android_demo.example.com.demo_printer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import module_android_demo.example.com.demo_printer.MainActivity;
import module_android_demo.example.com.demo_printer.R;

public class PrinterSetFragment extends Fragment implements View.OnClickListener {
    Spinner spSpeed, spGray;
    MainActivity mContext;
    Button btPrintSet;
    CheckBox cbItalic, cbFrame, cbBold, cbdoubleWidth, cbDoubleHigh, cbUnderline, cbWhite;
    EditText LeftMargin, RightMargin, edtRowSpacing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_printer_set, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        spSpeed = (Spinner) mContext.findViewById(R.id.spSpeed);
        spGray = (Spinner) mContext.findViewById(R.id.spGray);

        LeftMargin = (EditText) mContext.findViewById(R.id.LeftMargin);
        RightMargin = (EditText) mContext.findViewById(R.id.RightMargin);
        edtRowSpacing = (EditText) mContext.findViewById(R.id.edtRowSpacing);

        cbItalic = (CheckBox) mContext.findViewById(R.id.cbItalic);
        cbFrame = (CheckBox) mContext.findViewById(R.id.cbFrame);
        cbBold = (CheckBox) mContext.findViewById(R.id.cbBold);
        cbdoubleWidth = (CheckBox) mContext.findViewById(R.id.cbdoubleWidth);
        cbDoubleHigh = (CheckBox) mContext.findViewById(R.id.cbDoubleHigh);
        cbUnderline = (CheckBox) mContext.findViewById(R.id.cbUnderline);
        cbWhite = (CheckBox) mContext.findViewById(R.id.cbWhite);

        btPrintSet = (Button) mContext.findViewById(R.id.btPrintSet);
        btPrintSet.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btPrintSet:
                setRowSpacing();
                setMargin();
                setGray();
                setSpeed();
                setCharacterStyle();
                Toast.makeText(mContext, "complete", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //设置灰度
    private void setGray() {
        int gray = spGray.getSelectedItemPosition() + 1;
        mContext.gray = spGray.getSelectedItemPosition();
        mContext.mPrinter.setPrintGrayLevel(gray);
    }

    //设置速度
    private void setSpeed() {
        int speed = spSpeed.getSelectedItemPosition();
        mContext.speed = spSpeed.getSelectedItemPosition();
        mContext.mPrinter.setPrintSpeed(speed);
    }

    //设置字体
    private void setCharacterStyle() {
        boolean italic = cbItalic.isChecked();
        boolean frame = cbFrame.isChecked();
        boolean bold = cbBold.isChecked();
        boolean doubleWidth = cbdoubleWidth.isChecked();
        boolean doubleHigh = cbDoubleHigh.isChecked();
        boolean white = cbWhite.isChecked();
        boolean underline = cbUnderline.isChecked();
        mContext.isItalic = italic;
        mContext.isFrame = frame;
        mContext.isBold = bold;
        mContext.isdoubleWidth = doubleWidth;
        mContext.isDoubleHigh = doubleHigh;
        mContext.isWhite = white;
        mContext.isUnderline = underline;
        mContext.mPrinter.setPrintCharacterStyle(italic, frame, bold, doubleWidth, doubleHigh, white, underline);
    }

    //设置行间距
    private void setRowSpacing() {
        String sapcing = edtRowSpacing.getText().toString();
        if (sapcing == null || sapcing.isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string.printRowSpacingFail), Toast.LENGTH_SHORT).show();
            return;
        }
        int s = Integer.parseInt(sapcing);
        if (s > 255 || s < 0) {
            Toast.makeText(mContext, getResources().getString(R.string.printRowSpacingFail), Toast.LENGTH_SHORT).show();
            return;
        }
        mContext.rowSpacing = s;
        mContext.mPrinter.setPrintRowSpacing(s);
    }

    //设置左右边距
    private void setMargin() {
        String left = LeftMargin.getText().toString();
        String right = RightMargin.getText().toString();
        if (left == null || left.isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string.printLeftRightMargin), Toast.LENGTH_SHORT).show();
            return;
        }
        if (right == null || right.isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string.printLeftRightMargin), Toast.LENGTH_SHORT).show();
            return;
        }

        int l = Integer.parseInt(left);
        int r = Integer.parseInt(right);

        if (l > 47 || l < 0 || r > 47 || r < 0) {
            Toast.makeText(mContext, getResources().getString(R.string.printLeftRightMargin), Toast.LENGTH_SHORT).show();
            return;
        }

        if ((l + r) > 47) {
            Toast.makeText(mContext, getResources().getString(R.string.printLeftRightMargin), Toast.LENGTH_SHORT).show();
            return;
        }

        mContext.leftMargin = l;
        mContext.rightMargin = r;


        mContext.mPrinter.setPrintLeftMargin(l);
        mContext.mPrinter.setPrintRightMargin(r);
    }

    //初始化参数
    private void init() {
        cbItalic.setChecked(mContext.isItalic);
        cbFrame.setChecked(mContext.isFrame);
        cbBold.setChecked(mContext.isBold);
        cbdoubleWidth.setChecked(mContext.isdoubleWidth);
        cbDoubleHigh.setChecked(mContext.isDoubleHigh);
        cbUnderline.setChecked(mContext.isUnderline);
        cbWhite.setChecked(mContext.isWhite);

        spGray.setSelection(mContext.gray);
        spSpeed.setSelection(mContext.speed);

        edtRowSpacing.setText(mContext.rowSpacing + "");
        LeftMargin.setText(mContext.leftMargin + "");
        RightMargin.setText(mContext.rightMargin + "");
    }

}
