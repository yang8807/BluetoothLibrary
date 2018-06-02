package com.laotunong.blelibrary.file;

import android.widget.Toast;

import java.io.File;

public class FileUtils {


    public static File createFile(String path, String fileDirectory, String fileName) {

        File file = new File(path + "/" + fileDirectory);
        if (!file.exists()) {
            file.mkdir();
        }

        File fileNames = new File(file, fileName + ".csv");
        return fileNames;
    }
}
