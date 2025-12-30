package com.virtualspace.app.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ProxyActivity extends Activity {
    private static final String TAG = "ProxyActivity";
    private VirtualAppRuntime runtime;
    private String targetPackage;
    private String targetActivity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        targetPackage = intent.getStringExtra("target_package");
        targetActivity = intent.getStringExtra("target_activity");
        
        if (targetPackage != null && targetActivity != null) {
            runtime = VirtualAppRuntime.getRuntime(targetPackage);
            if (runtime != null) {
                runtime.startActivity(this, targetActivity, intent);
            } else {
                Log.e(TAG, "No runtime found for package: " + targetPackage);
                finish();
            }
        } else {
            finish();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (runtime != null) {
            runtime.onActivityResume(this);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (runtime != null) {
            runtime.onActivityPause(this);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (runtime != null) {
            runtime.onActivityDestroy(this);
        }
    }
}
