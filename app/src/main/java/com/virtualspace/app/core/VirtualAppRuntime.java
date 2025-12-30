package com.virtualspace.app.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualAppRuntime {
    private static final ConcurrentHashMap<String, VirtualAppRuntime> sRuntimes = new ConcurrentHashMap<>();
    
    private final String mPackageName;
    private final String mVirtualPackageName;
    private final String mProcessName;
    private final DexClassLoader mClassLoader;
    private final ThreadGroup mThreadGroup;
    private final VirtualApplicationInfo mAppInfo;
    private final Context mHostContext;
    
    private VirtualAppRuntime(Context hostContext, VirtualApp app) throws Exception {
        mHostContext = hostContext;
        mPackageName = app.packageName;
        mVirtualPackageName = "virtual." + app.packageName + "." + app.userId;
        mProcessName = mVirtualPackageName + ":main";
        mThreadGroup = new ThreadGroup("VirtualApp-" + mVirtualPackageName);
        
        // Create isolated classloader
        File appDir = VirtualCore.get().getEnvironment().getAppDataDir(mVirtualPackageName);
        mClassLoader = new DexClassLoader(
            app.apkPath,
            appDir.getAbsolutePath(),
            null,
            hostContext.getClassLoader()
        );
        
        // Parse app info
        mAppInfo = parseApplicationInfo(app.apkPath);
    }
    
    public static VirtualAppRuntime createRuntime(Context context, VirtualApp app) {
        try {
            VirtualAppRuntime runtime = new VirtualAppRuntime(context, app);
            sRuntimes.put(app.packageName, runtime);
            return runtime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static VirtualAppRuntime getRuntime(String packageName) {
        return sRuntimes.get(packageName);
    }
    
    public void startActivity(Activity proxyActivity, String activityName, Intent originalIntent) {
        Thread activityThread = new Thread(mThreadGroup, () -> {
            try {
                Class<?> activityClass = mClassLoader.loadClass(activityName);
                Constructor<?> constructor = activityClass.getConstructor();
                Object activityInstance = constructor.newInstance();
                
                // Call onCreate with virtualized context
                Method onCreate = activityClass.getMethod("onCreate", android.os.Bundle.class);
                onCreate.invoke(activityInstance, originalIntent.getExtras());
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Activity-" + activityName);
        
        activityThread.start();
    }
    
    public void onActivityResume(Activity proxyActivity) {
        // Handle activity lifecycle in virtual context
    }
    
    public void onActivityPause(Activity proxyActivity) {
        // Handle activity lifecycle in virtual context
    }
    
    public void onActivityDestroy(Activity proxyActivity) {
        // Cleanup virtual activity resources
    }
    
    private VirtualApplicationInfo parseApplicationInfo(String apkPath) throws Exception {
        PackageManager pm = mHostContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, 
            PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS);
        
        VirtualApplicationInfo appInfo = new VirtualApplicationInfo();
        if (packageInfo != null) {
            appInfo.packageName = mVirtualPackageName;
            appInfo.processName = mProcessName;
            appInfo.realPackageName = packageInfo.packageName;
            appInfo.activities = packageInfo.activities;
            appInfo.services = packageInfo.services;
            appInfo.receivers = packageInfo.receivers;
        }
        
        return appInfo;
    }
    
    public String getVirtualPackageName() {
        return mVirtualPackageName;
    }
    
    public String getProcessName() {
        return mProcessName;
    }
    
    public ThreadGroup getThreadGroup() {
        return mThreadGroup;
    }
    
    public VirtualApplicationInfo getApplicationInfo() {
        return mAppInfo;
    }
}
