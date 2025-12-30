package com.virtualspace.app.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VirtualEnvironment {
    private Context mContext;
    private File mVirtualRoot;
    
    public VirtualEnvironment(Context context) {
        mContext = context;
        mVirtualRoot = new File(context.getFilesDir(), "virtual");
        initEnvironment();
    }
    
    private void initEnvironment() {
        if (!mVirtualRoot.exists()) {
            mVirtualRoot.mkdirs();
        }
        
        // Create standard Android directories
        new File(mVirtualRoot, "data").mkdirs();
        new File(mVirtualRoot, "cache").mkdirs();
        new File(mVirtualRoot, "lib").mkdirs();
        new File(mVirtualRoot, "apps").mkdirs();
    }
    
    public File getAppDataDir(String virtualPackageName) {
        File appDir = new File(mVirtualRoot, "data/" + virtualPackageName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return appDir;
    }
    
    public File getAppCacheDir(String virtualPackageName) {
        File cacheDir = new File(mVirtualRoot, "cache/" + virtualPackageName);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }
    
    public File getAppLibDir(String virtualPackageName) {
        File libDir = new File(mVirtualRoot, "lib/" + virtualPackageName);
        if (!libDir.exists()) {
            libDir.mkdirs();
        }
        return libDir;
    }
    
    public boolean extractApk(String apkPath, String packageName) {
        try {
            File targetDir = new File(mVirtualRoot, "apps/" + packageName);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            
            File sourceApk = new File(apkPath);
            File targetApk = new File(targetDir, "base.apk");
            
            copyFile(sourceApk, targetApk);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {
            
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
    
    public File getVirtualRoot() {
        return mVirtualRoot;
    }
    
    public File getAppsDir() {
        return new File(mVirtualRoot, "apps");
    }
    
    // Create isolated environment for each virtual app
    public VirtualAppEnvironment createAppEnvironment(String virtualPackageName) {
        return new VirtualAppEnvironment(this, virtualPackageName);
    }
    
    public static class VirtualAppEnvironment {
        private final VirtualEnvironment mParent;
        private final String mVirtualPackageName;
        private final File mDataDir;
        private final File mCacheDir;
        private final File mLibDir;
        
        VirtualAppEnvironment(VirtualEnvironment parent, String virtualPackageName) {
            mParent = parent;
            mVirtualPackageName = virtualPackageName;
            mDataDir = parent.getAppDataDir(virtualPackageName);
            mCacheDir = parent.getAppCacheDir(virtualPackageName);
            mLibDir = parent.getAppLibDir(virtualPackageName);
        }
        
        public File getDataDir() { return mDataDir; }
        public File getCacheDir() { return mCacheDir; }
        public File getLibDir() { return mLibDir; }
        public String getVirtualPackageName() { return mVirtualPackageName; }
    }
}
