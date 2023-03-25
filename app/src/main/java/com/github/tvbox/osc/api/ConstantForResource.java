package com.github.tvbox.osc.api;

import com.github.tvbox.osc.base.App;

import java.io.File;

public class ConstantForResource {

    public static final String rootPath = App.getInstance().getExternalFilesDir("tv").getAbsolutePath();


    /**
     * 获取保存的直播频点列表
     * @return
     */
    public static File getTvLiveFile() {
        String path1 =rootPath + "/tvlive1.txt";
        File file = new File(path1);
        if (file.exists()) {
            return file;
        }
        return new File(rootPath + "/tvlive.txt");
    }

}
