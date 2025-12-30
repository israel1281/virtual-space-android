package com.virtualspace.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.virtualspace.app.core.VirtualApp;
import com.virtualspace.app.core.VirtualAppLoader;

public class VirtualAppActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_app);
        
        Intent intent = getIntent();
        String packageName = intent.getStringExtra("package_name");
        String apkPath = intent.getStringExtra("apk_path");
        
        if (packageName != null && apkPath != null) {
            launchVirtualApp(packageName, apkPath);
        } else {
            finish();
        }
    }
    
    private void launchVirtualApp(String packageName, String apkPath) {
        VirtualApp app = new VirtualApp();
        app.packageName = packageName;
        app.apkPath = apkPath;
        
        boolean success = VirtualAppLoader.loadAndRunApp(this, app);
        
        if (success) {
            Toast.makeText(this, "Virtual app loaded: " + packageName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to load virtual app", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
