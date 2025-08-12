package top.sacz.webviewpreviewppt;

import android.app.Application;
import android.util.Log;

import com.tencent.tbs.reader.TbsFileInterfaceImpl;

public class FixApplication extends Application {
    private static final String TAG = "FixApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: startInit");
        TbsFileInterfaceImpl.setLicenseKey("s9pkTbhI3YsXnF8JasmIA5d0YNjNRdKWXVqpQ91EMQkJqchQ3oBQezrxPVjQPBGI");
        int ret = TbsFileInterfaceImpl.initEngine(this);
        Log.d(TAG, "onCreate: "+ ret);
    }
}
