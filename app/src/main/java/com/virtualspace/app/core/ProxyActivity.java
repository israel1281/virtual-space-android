package com.virtualspace.app.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        
        Log.d(TAG, "Starting virtual app: " + targetPackage);
        
        if (targetPackage != null) {
            runtime = VirtualAppRuntime.getRuntime(targetPackage);
            if (runtime != null) {
                setupVirtualAppUI();
                runtime.startActivity(this, targetActivity, intent);
            } else {
                Log.e(TAG, "No runtime found for package: " + targetPackage);
                showError("Virtual app runtime not found");
            }
        } else {
            showError("Invalid virtual app");
        }
    }
    
    private void setupVirtualAppUI() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        
        TextView titleView = new TextView(this);
        titleView.setText("Virtual Space");
        titleView.setTextSize(20);
        titleView.setTextColor(0xFF333333);
        
        TextView appView = new TextView(this);
        appView.setText("Running: " + (runtime != null ? runtime.getApplicationInfo().realPackageName : targetPackage));
        appView.setTextSize(16);
        appView.setTextColor(0xFF666666);
        appView.setPadding(0, 16, 0, 0);
        
        TextView statusView = new TextView(this);
        statusView.setText("✓ Isolated Environment\n✓ Virtual Package Manager\n✓ Sandboxed Execution");
        statusView.setTextSize(14);
        statusView.setTextColor(0xFF009688);
        statusView.setPadding(0, 24, 0, 0);
        
        layout.addView(titleView);
        layout.addView(appView);
        layout.addView(statusView);
        
        setContentView(layout);
    }
    
    private void showError(String message) {
        TextView errorView = new TextView(this);
        errorView.setText("Error: " + message);
        errorView.setTextSize(16);
        errorView.setTextColor(0xFFFF0000);
        errorView.setPadding(32, 32, 32, 32);
        setContentView(errorView);
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        
        // Auto-close after 3 seconds
        errorView.postDelayed(this::finish, 3000);
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
