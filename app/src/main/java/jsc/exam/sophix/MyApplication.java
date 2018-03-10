package jsc.exam.sophix;

import android.app.Application;

import com.taobao.sophix.SophixManager;

/**
 * Created on 2018/2/2.
 *
 * @author jsc
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //hot fix. Load patch from aliyun service.
        SophixManager.getInstance().queryAndLoadNewPatch();
    }
}
