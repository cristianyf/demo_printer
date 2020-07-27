package module_android_demo.example.com.demo_printer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.rscja.deviceapi.Printer;

import java.util.Locale;

import module_android_demo.example.com.demo_printer.MainActivity;
import module_android_demo.example.com.demo_printer.R;


public class PrinterMainFragment extends Fragment implements View.OnClickListener {
    MainActivity mContext;
    String TAG = "PrinterMainFragment";
    boolean isDebug = true;
    EditText et_content;
    CheckBox cbContinuous;

    TextView tv_msg;
    Button btn_print, btn_clear;
    private Handler handler = new Handler();
    ScrollView scrollMSG1;
    boolean isLeisure = true;//空闲
    boolean isLackofpaper = false;
    boolean isNORMAL = true;//打印机是否正常
    public boolean runing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_printer_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        et_content = (EditText) mContext.findViewById(R.id.et_content);
        cbContinuous = (CheckBox) mContext.findViewById(R.id.cbContinuous);

        tv_msg = (TextView) mContext.findViewById(R.id.tv_msg);
        btn_print = (Button) mContext.findViewById(R.id.btn_print);
        btn_clear = (Button) mContext.findViewById(R.id.btn_clear);
        btn_print.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        mContext.mPrinter.setPrinterStatusCallBack(new CallBack());
        cbContinuous.setOnClickListener(this);
        et_content.setText(getPrintData());
        scrollMSG1 = (ScrollView) mContext.findViewById(R.id.scrollMSG1);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        runing = false;
        mContext.mPrinter.clearCache();
        if (isDebug) Log.i(TAG, "onDestroyView==>");
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (isDebug) Log.i(TAG, "onResume==>");
        mContext.mPrinter.setPrinterStatusCallBackEnable(true);
        btn_print.setEnabled(true);
        cbContinuous.setEnabled(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_print:
                if (runing) {
                    Toast.makeText(mContext, R.string.printing, Toast.LENGTH_SHORT).show();
                    return;
                }
                String data = et_content.getText().toString();
                if (data == null || data.length() == 0) {
                    Toast.makeText(mContext, "打印数据不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                runing = true;
                if (cbContinuous.isChecked()) {
                    btn_print.setEnabled(false);
                    new AutoPrinter(data, true).start();
                } else {
                    new AutoPrinter(data, false).start();
                }
                break;
            case R.id.cbContinuous:
                runing = false;
                btn_print.setEnabled(true);
                break;
            case R.id.btn_clear:
                tv_msg.setText("");
                break;


        }
    }


    class AutoPrinter extends Thread {
        boolean isContinuous = false;
        String data = "";
        int size = 50;

        private AutoPrinter(String data, boolean isContinuous) {
            this.isContinuous = isContinuous;
            this.data = data;
        }

        public void run() {
            mContext.mPrinter.clearCache();//清空缓存区
            do {
                for (int k = 0; (k < data.length()) && runing; ) {
                    if (isDebug)
                        Log.i(TAG, "runing" + runing + "  isLeisure=" + isLeisure + "  data.leng=" + data.length() + "  k=" + k);
                    if (isLeisure) {//打印机闲暇的时候才能继续发送数据
                        int flagDataLen = data.length() - k;//未打印的剩余数据长度
                        int Statr = k;
                        int end = flagDataLen > size ? k + size : k + flagDataLen;
                        k = end;
                        String temp = data.substring(Statr, end);
                        mContext.mPrinter.print(temp);//每次发送50个字节数据，防止发送数据过多出现乱码
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            while (runing && isContinuous);
            mContext.printFeed();
            runing = false;
        }
    }

    class CallBack implements Printer.PrinterStatusCallBack {
        @Override
        public void message(Printer.PrinterStatus infoCode) {
            switch (infoCode) {
                case NORMAL://正常:
                    if (isLackofpaper)
                        setMeg(mContext.getString(R.string.printNormal));
                    isLackofpaper = false;
                    isNORMAL = true;
                    break;
                case OVERPRESSURE://过压
                    isNORMAL = false;
                    setMeg(mContext.getString(R.string.printOverpressure));
                    break;
                case LACKOFPAPER://缺纸
                    isNORMAL = false;
                    isLackofpaper = true;
                    setMeg(mContext.getString(R.string.printLackofpaper));
                    break;
                case OVERHEATING://过热
                    isNORMAL = false;
                    setMeg(mContext.getString(R.string.printOverheating));
                    break;
                case PRESSUREAXISOPEN://压轴打开
                    isNORMAL = false;
                    setMeg(mContext.getString(R.string.printPressureaxisopen));
                    break;
                case PAPERSTUCK://卡纸
                    isNORMAL = false;
                    setMeg(mContext.getString(R.string.printPaperstuck));
                    break;
                case SLICINGERROR://切片错误
                    isNORMAL = false;
                    setMeg(mContext.getString(R.string.printSlicingerror));
                    break;
                case PAPERFINISH://打印机纸将尽
                    isNORMAL = false;
                    setMeg(mContext.getString(R.string.printPaperfinish));
                    break;
                case CANCELPAPER://打印机用户未取纸
                    isNORMAL = false;
                    setMeg(mContext.getString(R.string.printCancelpaper));
                    break;
                case LEISURE:
                    isLeisure = true;
                    setMeg(mContext.getString(R.string.printLeisure));
                    break;
                case UNLEISURED:
                    isNORMAL = false;
                    isLeisure = false;
                    setMeg(mContext.getString(R.string.printUnleisure));
                    break;
            }
        }
    }

    private void setMeg(String msg) {
        String m = tv_msg.getText().toString();
        if (m.length() > 1000) {
            m = "";
        }
        m = m + msg + "\r\n";
        tv_msg.setText(m);
        scrollToBottom(scrollMSG1);
    }

    private String getPrintData() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();//“zh”为中文，“en”为英文...
        if (language.toUpperCase().contains("ZH")) {
            StringBuilder sb1 = new StringBuilder();
            sb1.append("            超市小票            \r\n");
            sb1.append(String.format("工号:00001 终端号:12345") + "\r\n");
            sb1.append("时间：2017/12/15 16:17:18\r\n");
            sb1.append("方便面  2  3.0  6.0\r\n");
            sb1.append("商品  数量 单价 金额\r\n");
            sb1.append("白糖  1  1.0  1.0\r\n");
            sb1.append("红糖  2  2.5  5.0\r\n");
            sb1.append(String.format("合计:数量:6.5   金额: 12.0\r\n"));
            sb1.append("时间：2017/12/15 16:17:18\r\n");
            sb1.append("          请保留好小票!        \n");
            return sb1.toString();
        } else {
            StringBuilder sb1 = new StringBuilder();
            sb1.append("     Carabineros de Chile       \r\n");
            sb1.append("Fecha y Hora:01/01/2021 17:01:00\r\n");
            sb1.append("                  Boleta:1955\r\n");
            sb1.append("Infractor: Marty Mcfly     \r\n");
            sb1.append("RUT:25.000.999-1   \r\n");
            sb1.append("Patente:             OUTATIME\r\n");
            sb1.append("Tipo de Infraccion: C17\r\n");
            sb1.append("Lugar de la Infraccion: \r\n");
            sb1.append("-------La Concepcion 191 -------\r\n");
            sb1.append("Nombre Carabinero: Juan Valdez\r\n");
            sb1.append("Codigo Carabinero: 51673635\r\n");
            sb1.append("Juzgado a Comparecer: Las Condes\r\n");
            sb1.append("Se retira Licencia Clase: A2 \r\n");
            return sb1.toString();
        }
    }


    public void scrollToBottom(final ScrollView scrollMSG1) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                scrollMSG1.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


}
