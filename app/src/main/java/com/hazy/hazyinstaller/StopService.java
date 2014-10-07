package com.hazy.hazyinstaller;

import android.os.*;

public class StopService {

    public void StopService() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
