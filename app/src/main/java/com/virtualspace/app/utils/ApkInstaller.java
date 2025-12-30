package com.virtualspace.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.virtualspace.app.core.VirtualApp;
import com.virtualspace.app.core.VirtualCore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ApkInstaller {
    
    public static boolean installApkToVirtualSpace(Context context, String apkPath) {
        try {
            File apkFile = new File(apkPath);
            if (!apkFile.exists()) {
                // Try to handle content URI
                return installFromContentUri(context, apkPath);
            }
            
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, 
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            
            if (packageInfo != null) {
                return VirtualCore.get().installApp(apkPath);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static boolean installFromContentUri(Context context, String uriString) {
        try {
            Uri uri = Uri.parse(uriString);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            
            if (inputStream != null) {
                // Copy to internal storage
                File tempApk = new File(context.getCacheDir(), "temp_install.apk");
                FileOutputStream outputStream = new FileOutputStream(tempApk);
                
                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                
                inputStream.close();
                outputStream.close();
                
                // Install from temp file
                boolean result = VirtualCore.get().installApp(tempApk.getAbsolutePath());
                tempApk.delete(); // Clean up
                
                return result;
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
