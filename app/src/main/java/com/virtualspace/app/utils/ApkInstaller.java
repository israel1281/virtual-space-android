package com.virtualspace.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.virtualspace.app.core.VirtualApp;
import com.virtualspace.app.core.VirtualCore;
import java.io.File;

public class ApkInstaller {
    
    public static boolean installApkToVirtualSpace(Context context, String apkPath) {
        try {
            File apkFile = new File(apkPath);
            if (!apkFile.exists()) {
                return false;
            }
            
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, 
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            
            if (packageInfo != null) {
                // Create virtual app entry
                VirtualApp virtualApp = new VirtualApp();
                virtualApp.packageName = packageInfo.packageName;
                virtualApp.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                virtualApp.apkPath = apkPath;
                virtualApp.isInstalled = true;
                
                // Install to virtual core
                return VirtualCore.get().installApp(apkPath);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static VirtualApp createDemoApp(Context context) {
        // Create a demo virtual app for testing
        VirtualApp demoApp = new VirtualApp();
        demoApp.packageName = "com.demo.virtualapp";
        demoApp.appName = "Demo Virtual App";
        demoApp.apkPath = "/demo/path";
        demoApp.isInstalled = true;
        return demoApp;
    }
}
