package jsc.exam.sophix;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Keep;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 */
public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";
    //aesKey必须是16位数字或字母的组合，是和补丁工具设置里面AES Key保持完全一致, 补丁才能正确被解密进而加载
    final String aesKey = "";
    final String appId = "";
    final String appSecret = "";
    final String rsa = "";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(MyApplication.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
        MultiDex.install(this);
        initSophix();
    }

    private void initSophix() {
        String appVersion;
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            appVersion = "0.0.0";
        }
        SophixManager.getInstance().setContext(this)
                .setAesKey(aesKey)
                .setAppVersion(appVersion)
                .setSecretMetaData(appId, appSecret, rsa)
                .setEnableDebug(false)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        switch (code){
                            case PatchStatus.CODE_LOAD_SUCCESS:
//                                Toast.makeText(getBaseContext(), "Patch更新成功", Toast.LENGTH_SHORT).show();
                                break;
                            case PatchStatus.CODE_LOAD_RELAUNCH:
                                // 如果需要在后台重启，建议此处用SharePreference保存状态。
                                showForceRestartAppDialog();
                                break;
                        }
                    }
                }).initialize();
    }

    private void showForceRestartAppDialog(){
        new AlertDialog.Builder(getBaseContext())
                .setMessage("Patch更新成功，请重启应用！")
                .setCancelable(false)
                .setPositiveButton("退出应用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SophixManager.getInstance().killProcessSafely();
                    }
                }).show();
    }
}