package com.example.miic.oa.common.fileDownload;

import android.os.Environment;

import java.io.File;

/**
 * Created by XuKe on 2018/2/27.
 */

public class FileUtils {
    private String path = Environment.getExternalStorageDirectory().toString() + "/OA_file";

    public FileUtils(String filePath) {
        File file = new File(filePath);
        /**
         *如果文件夹不存在就创建
         */
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    /**
     * 创建一个文件
     * @param FileName 文件名
     * @return
     */
    public File createFile(String FileName) {
        return new File(path, FileName);
    }
}
