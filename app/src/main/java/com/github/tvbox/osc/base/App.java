package com.github.tvbox.osc.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.github.tvbox.osc.data.AppDataManager;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.util.EpgUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LocaleHelper;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.WLogUtil;
import com.github.tvbox.osc.util.assetscopy.AssetsToSdcardAndReplaceUtil;
import com.github.tvbox.osc.util.js.JSEngine;
import com.kingja.loadsir.core.LoadSir;
import com.lzy.okgo.OkGo;
import com.orhanobut.hawk.Hawk;

import java.io.File;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;
import tn.uu.baselibrary.utils.AssetsFilePathModel;

/**
 * @author pj567
 * @date :2020/12/17
 * @description:
 */
public class App extends MultiDexApplication {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        WLogUtil.getInstance().init(this);

        registerActivityLifecycleCallbacks();

        //复制jar文件
        AssetsFilePathModel pathModel = new AssetsFilePathModel("csp_jar", "");
        AssetsToSdcardAndReplaceUtil.copyIfNotExist(this, pathModel);

        AssetsFilePathModel tvLivePathModel = new AssetsFilePathModel("tvlive", "");
        AssetsToSdcardAndReplaceUtil.copyIfNotExist(this, tvLivePathModel);

        initParams();
        // takagen99 : Initialize Locale
        initLocale();
        // OKGo
        OkGoHelper.init();
        // Get EPG Info
        EpgUtil.init();
        // 初始化Web服务器
        ControlManager.init(this);
        //初始化数据库
        AppDataManager.init();
        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .commit();
        AutoSizeConfig.getInstance().setCustomFragment(true).getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
        PlayerHelper.init();

        // Delete Cache
        File dir = getCacheDir();
        FileUtils.recursiveDelete(dir);
        dir = getExternalCacheDir();
        FileUtils.recursiveDelete(dir);

        // Add JS support
        JSEngine.getInstance().create();

        OkGo.getInstance().init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    private void initParams() {
        // Hawk
        Hawk.init(this).build();
        Hawk.put(HawkConfig.DEBUG_OPEN, false);

        putDefault(HawkConfig.HOME_REC, 2);       // Home Rec 0=豆瓣, 1=推荐, 2=历史
        putDefault(HawkConfig.PLAY_TYPE, 1);      // Player   0=系统, 1=IJK, 2=Exo
        putDefault(HawkConfig.IJK_CODEC, "硬解码");// IJK Render 软解码, 硬解码
//        putDefault(HawkConfig.HOME_NUM, 2);       // History Number
//        putDefault(HawkConfig.DOH_URL, 2);        // DNS
//        putDefault(HawkConfig.SEARCH_VIEW, 1);    // Text or Picture

    }


    public void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                WLogUtil.d("xxx",activity + " onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                WLogUtil.d("xxx",activity + " onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                WLogUtil.d("xxx",activity + " onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                WLogUtil.d("xxx",activity + " onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                WLogUtil.d("xxx",activity + " onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                WLogUtil.d("xxx",activity + " onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                WLogUtil.d("xxx",activity + " onActivityDestroyed");
            }
        });
    }


    private void initLocale() {
        if (Hawk.get(HawkConfig.HOME_LOCALE, 0) == 0) {
            LocaleHelper.setLocale(App.this, "zh");
        } else {
            LocaleHelper.setLocale(App.this, "");
        }
    }

    public static App getInstance() {
        return instance;
    }

    private void putDefault(String key, Object value) {
        if (!Hawk.contains(key)) {
            Hawk.put(key, value);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JSEngine.getInstance().destroy();
    }

}