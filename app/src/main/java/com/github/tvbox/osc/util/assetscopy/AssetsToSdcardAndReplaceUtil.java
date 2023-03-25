package com.github.tvbox.osc.util.assetscopy;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.github.tvbox.osc.util.WLogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;import tn.uu.baselibrary.utils.AssetsFilePathModel;

/**
 * Assets文件夹文件复制功能
 *  //对sdcard中用户手册进行删除操作, 保证用户手册的一致性
 *   AssetsFilePathModel manualsPathModel = new AssetsFilePathModel("manuals", "SpecAn2/Manual");
 *   AssetsToSdcardAndReplaceUtil.syncFiles(manualsPathModel);
 */
public class AssetsToSdcardAndReplaceUtil {

    private static final String TAG = "AssetsToSdcardUtil";

    //不存在的话，直接复制文件
    public static void copyIfNotExist(Context context, AssetsFilePathModel pathModel) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] fileNames = assetManager.list(pathModel.getAssetsDir());
            for (String temp : fileNames) {  //取assets文件夹下的所有文件
                File sdcardFile = new File(pathModel.getAbsolutePath() + File.separator + temp);
                if (!sdcardFile.exists() || !sdcardFile.isFile()) { //如果不存在的话，进行复制
                    if (sdcardFile.createNewFile()) {
                        WLogUtil.d(TAG, "Create " + temp + " file successfully");
                    } else {
                        Log.i(TAG, "Creating " + temp + " file failed");
                    }
                    //复制文件,使用同步的方法，防止出现其他异常
                    File outFile = new File(pathModel.getAbsolutePath() + File.separator + temp);
                    InputStream is = assetManager.open(pathModel.getAssetsDir() + File.separator + temp);
                    FileOutputStream fos = new FileOutputStream(outFile);
                    copyFromStream(is, fos);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //保证某个文件夹中的所有文件名完全一致，如果与assets文件中文件名不相同，需要删除sdcard中的文件
    public static void syncFiles(Context context, AssetsFilePathModel pathModel) {
        deleteFilesIfNotSame(context, pathModel);
        copyIfNotExist(context, pathModel);
    }

    //如果不相同，删除sdcard中文件
    private static void deleteFilesIfNotSame(Context context, AssetsFilePathModel pathModel) {
        try {
            //获取assets文件夹下的所有文件名称
            AssetManager assetManager = context.getAssets();
            String[] assets = assetManager.list(pathModel.getAssetsDir());

            //获取sdcard下所有的文件
            File sdcardFile = new File(pathModel.getAbsolutePath());
            File[] sdcardFiles = sdcardFile.listFiles();

            if (sdcardFiles != null) {
                for (File file : sdcardFiles) {
                    //sdcard中文件与assets中文件不一致，需要删除
                    boolean isHave = false;
                    for (String assetFile : assets) {
                        if (assetFile.equals(file.getName())) {
                            isHave = true;
                        }
                    }
                    if (!isHave) {
                        boolean res = file.delete();
                        WLogUtil.d("AssetFileUtils", "删除了文件： " + file.getAbsolutePath() + " " + res);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFromStream(InputStream is, FileOutputStream fos) {
        try {
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
