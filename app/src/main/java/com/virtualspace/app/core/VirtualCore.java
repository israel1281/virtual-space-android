package com.virtualspace.app.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.virtualspace.app.utils.ApkInstaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VirtualCore {
    private static VirtualCore sInstance;
    private Context mContext;
    private List<VirtualApp> mInstalledApps = new ArrayList<>();
    private VirtualEnvironment mEnvironment;
    
    public static VirtualCore get() {
        if (sInstance == null) {
            synchronized (VirtualCore.class) {
                if (sInstance == null) {
                    sInstance = new VirtualCore();
                }
            }
        }
        return sInstance;
    }
    
    public void startup(Context context) {
        mContext = context.getApplicationContext();
        mEnvironment = new VirtualEnvironment(mContext);
        initVirtualEnvironment();
        loadDemoApps();
    }
    
    private void initVirtualEnvironment() {
        File virtualDir = new File(mContext.getFilesDir(), "virtual");
        if (!virtualDir.exists()) {
            virtualDir.mkdirs();
        }
    }
    
    private void loadDemoApps() {
        // Add demo app for testing
        VirtualApp demoApp = ApkInstaller.createDemoApp(mContext);
        mInstalledApps.add(demoApp);
    }
    
    public boolean installApp(String apkPath) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            
            if (packageInfo != null) {
                VirtualApp app = new VirtualApp();
                app.packageName = packageInfo.packageName;
                app.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                app.apkPath = apkPath;
                app.isInstalled = true;
                
                // Extract APK to virtual environment
                mEnvironment.extractApk(apkPath, app.packageName);
                
                mInstalledApps.add(app);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<VirtualApp> getInstalledApps() {
        return new ArrayList<>(mInstalledApps);
    }
    
    public Context getContext() {
        return mContext;
    }
    
    public VirtualEnvironment getEnvironment() {
        return mEnvironment;
    }
}
