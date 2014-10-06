package com.hazy.hazyinstaller;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class storageChecker {

    public long sd_card_free() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        int availBlocks = stat.getAvailableBlocks();
        int blockSize = stat.getBlockSize();
        long free_memory = (long)availBlocks * (long)blockSize;
        return free_memory * 1048576;
    }

}
