package io.cordova.lexuncompany.application;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.tencent.bugly.Bugly;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by JasonYao on 2018/2/27.
 */

public class MyApplication extends Application {
    public static MyApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化NoHttp
        NoHttp.initialize(this, new NoHttp.Config()
                .setReadTimeout(30 * 1000)  //服务器响应超时时间
                .setConnectTimeout(30 * 1000)  //连接超时时间
                .setNetworkExecutor(new URLConnectionNetworkExecutor()) //使用HttpURLConnection做网络层
        );

        Logger.setTag("NoHttpSample");
        Logger.setDebug(true); //开启调试模式

        Stetho.initializeWithDefaults(this);  //初始化Chrome查看Sqlite插件

        Bugly.init(getApplicationContext(), "026a35dd56", false);  //乐巡企业版bugly

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //友盟统计
        UMConfigure.init(this, "5bbf0375b465f5d4170000f8", "测试环境", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        this.mInstance = this;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}