package tn.uu.baselibrary.utils

import android.util.Log
import com.github.tvbox.osc.api.ConstantForResource
import java.io.File

/**
 * 资源枚举类
 * @property assetsDir assets资源文件夹路径
 * @property sdcardDir sdcard文件夹路径
 */
class AssetsFilePathModel(val assetsDir: String, private val sdcardDir: String) {

    val absolutePath: String by lazy {
        val path = ConstantForResource.rootPath + File.separator + sdcardDir
        createDir(path)
        path
    }

    private fun createDir(dir: String) {
        val pathFile = File(dir)
        if (!pathFile.exists()) {
            val res = pathFile.mkdirs()
            if (!res) {
                Log.e("AssetsFilePathModel", "创建文件夹失败了 $dir");
                return;
            }
        }
    }

}
