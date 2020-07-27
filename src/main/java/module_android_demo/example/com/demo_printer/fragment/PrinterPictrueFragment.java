package module_android_demo.example.com.demo_printer.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import module_android_demo.example.com.demo_printer.MainActivity;
import module_android_demo.example.com.demo_printer.R;


public class PrinterPictrueFragment extends Fragment implements View.OnClickListener {

    MainActivity mContext;
    Button btnPicPrint;
    ImageView ivPrintImg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_printer_pictrue, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        btnPicPrint = (Button) mContext.findViewById(R.id.btnPicPrint);
        ivPrintImg = (ImageView) mContext.findViewById(R.id.ivPrintImg);
        ivPrintImg.setImageDrawable(getResources().getDrawable((R.drawable.printimg)));
        btnPicPrint.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnPicPrint:
                Bitmap bitmap = ((BitmapDrawable) ivPrintImg.getDrawable()).getBitmap();
                if (bitmap != null) {
                    new PrintTask(bitmap).execute();
                }
                break;
        }
    }


    //-------------------------
    public class PrintTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;
        Bitmap bitmap;

        public PrintTask(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            int gray = (mContext.gray + 1);
            int speed = mContext.speed;
            int time1 = 0;
            switch (gray) {
                case 1:
                    time1 = 600;
                    break;
                case 2:
                    time1 = 800;
                    break;
                case 3:
                    switch (speed) {
                        case 0:
                            time1 = 1100;
                            break;
                        case 1:
                            time1 = 900;
                            break;
                        case 2:
                            time1 = 800;
                            break;
                    }
                    break;
                case 4:
                    switch (speed) {
                        case 0:
                            time1 = 1400;
                            break;
                        case 1:
                            time1 = 900;
                            break;
                        case 2:
                            time1 = 700;
                            break;
                    }
                    break;
                case 5:
                    switch (speed) {
                        case 0:
                            time1 = 1700;
                            break;
                        case 1:
                            time1 = 1100;
                            break;
                        case 2:
                            time1 = 700;
                            break;
                    }
                    break;
                case 6:
                    switch (speed) {
                        case 0:
                            time1 = 1900;
                            break;
                        case 1:
                            time1 = 1200;
                            break;
                        case 2:
                            time1 = 800;
                            break;
                    }
                    break;
                case 7:
                    switch (speed) {
                        case 0:
                            time1 = 2100;
                            break;
                        case 1:
                            time1 = 1400;
                            break;
                        case 2:
                            time1 = 900;
                            break;
                    }
                    break;
                case 8:
                    switch (speed) {
                        case 0:
                            time1 = 2300;
                            break;
                        case 1:
                            time1 = 1600;

                            break;
                        case 2:
                            time1 = 900;
                            break;
                    }
                    break;
            }
            mContext.printPicture(bitmap, time1);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
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
