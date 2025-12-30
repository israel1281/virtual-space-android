package com.virtualspace.app;

import android.app.Application;
import com.virtualspace.app.core.VirtualCore;

public class VirtualSpaceApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        VirtualCore.get().startup(this);
    }
}
