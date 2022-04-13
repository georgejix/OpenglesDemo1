package com.example.openglesdemo1.utils;

import android.app.Application;
import android.content.res.Resources;
import android.os.Environment;

import java.io.File;

/**
 * @anchor: andy
 * @date: 2018-11-07
 * @description:
 */
public class AppCore {

    private static AppCore sInstance;

    private Application application;
    private static String path;
    private static File projectFile;

    public static AppCore getInstance() {
        if (sInstance == null) {
            sInstance = new AppCore();
        }
        return sInstance;
    }

    public void init(Application application) {
        this.application = application;
        initDir();
    }

    public Application getContext() {
        return application;
    }

    public Resources getResources() {
        return application.getResources();
    }

    public void initDir() {
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "androidDemo";
        projectFile = new File(path);
        if (!projectFile.exists()) {
            projectFile.mkdirs();
        }
    }

    public String getFile() {
        return null == path ? Environment.getExternalStorageDirectory().getAbsolutePath() : path;
    }

}
