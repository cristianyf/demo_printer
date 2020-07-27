package module_android_demo.example.com.demo_printer;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;

import com.rscja.deviceapi.Printer;
import com.rscja.deviceapi.exception.ConfigurationException;

import module_android_demo.example.com.demo_printer.fragment.PrinterBarcodeFragment;
import module_android_demo.example.com.demo_printer.fragment.PrinterMainFragment;
import module_android_demo.example.com.demo_printer.fragment.PrinterPictrueFragment;
import module_android_demo.example.com.demo_printer.fragment.PrinterSetFragment;

public class MainActivity extends AppCompatActivity {
    String TAG = "PrinterActivity";

    public Printer mPrinter;
    public int speed = 1;
    public int gray = 3;

    public int leftMargin = 0;
    public int rightMargin = 0;
    public int rowSpacing = 33;

    public boolean isItalic = false;
    public boolean isFrame = false;
    public boolean isBold = false;
    public boolean isdoubleWidth = false;
    public boolean isDoubleHigh = false;
    public boolean isUnderline = false;
    public boolean isWhite = false;

    public int barcodeW = 1;
    public int barcodeH = 64;
    public int barcodePosition = 0;


    private FragmentTabHost mTabHost;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewPageData();

        try {
            mPrinter = Printer.getInstance();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    protected void initViewPageData() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fm = getSupportFragmentManager();
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, fm, R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_activity_printerMain)).setIndicator(getString(R.string.title_activity_printerMain)),
                PrinterMainFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_printer_pic)).setIndicator(getString(R.string.title_printer_pic)),
                PrinterPictrueFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_printer_barcode)).setIndicator(getString(R.string.title_printer_barcode)),
                PrinterBarcodeFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_activity_app_config)).setIndicator(getString(R.string.title_activity_app_config)),
                PrinterSetFragment.class, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPrinter != null) {
            mPrinter.free();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new InitTask().execute();
    }

    //打印完之后进纸2行，方便斯纸
    public void printFeed() {
        mPrinter.setPrintRowSpacing(33);
        mPrinter.setFeedRow(2);
        mPrinter.setPrintRowSpacing(rowSpacing);
    }

    //打印图片
    public void printPicture(Bitmap bitmap, int time) {
        if (bitmap != null) {
            //  Log.e(TAG,"printPicture==>clearCache清缓存");
            mPrinter.clearCache();//清空缓存区
            //   Log.e(TAG,"printPicture==>setPrintRowSpacing设置行间距为0");
            mPrinter.setPrintRowSpacing(0);
            // Log.e(TAG,"printPicture==>print开始打印 time"+time);
            mPrinter.print(compressBitmap(bitmap), time);
            //  Log.e(TAG,"printPicture==>print开始完成");
            mPrinter.setPrintRowSpacing(rowSpacing);
            printFeed();
        }
    }

    //初始化打印机参数
    public void initParameter() {
        mPrinter.setPrintRowSpacing(rowSpacing);
        mPrinter.setPrintLeftMargin(leftMargin);
        mPrinter.setPrintRightMargin(rightMargin);
        mPrinter.setPrintGrayLevel(gray + 1);
        mPrinter.setPrintSpeed(speed);
        mPrinter.setPrintCharacterStyle(isItalic, isFrame, isBold, isdoubleWidth, isDoubleHigh, isWhite, isUnderline);
        // mPrinter.print(new byte[]{0x0A});//调整纸张，防止第一次打印图片移位置
    }

    public Bitmap compressBitmap(Bitmap bitmapOrg) {
        // 加载需要操作的图片，这里是一张图片
//        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.alipay);
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        if (width <= 384)//大于384才需要压缩
            return bitmapOrg;
        // 定义预转换成的图片的宽度和高度
        int newWidth = 384;
        // 计算缩放率，新尺寸除原始尺寸,使用宽的压缩了，防止图片变形
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = scaleWidth; //((float) newHeight) / height;
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
        // 将上面创建的Bitmap转换成Drawable对象，使得其可以使用在ImageView, ImageButton中
//        BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
        return resizedBitmap;
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mPrinter.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();
            if (!result) {
                Toast.makeText(MainActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "init success", Toast.LENGTH_SHORT).show();
                initParameter();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(MainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }
}
