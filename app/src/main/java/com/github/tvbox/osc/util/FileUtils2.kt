package com.github.tvbox.osc.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import kotlin.jvm.Throws

class FileUtils2(val context: Context) {

    companion object {
        private const val DIR_NAME = "showu"
        private const val TAG = "FileUtils"
        private const val SIZE = 1024
    }

    private val mScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO) }

    /**
     * Get save public file dir
     * 获取公用的sdcard路径：
     * /storage/emulated/0/Android/data/cn.words.showu/files/showu/
     * @param fileName
     * @return
     */
    fun getSavePublicFileDir(dir: String): String? {
        val dirPath = DIR_NAME + File.separator + dir
        val file = context.getExternalFilesDir(dirPath) ?: return null
        if (!file.exists()) {
            val res = file.mkdirs()
            if (!res) WLogUtil.e(TAG, "创建文件夹失败了！！！")
        }
        return file.absolutePath
    }

    /**
     * Save file to public
     * 保存文件到公用的sdcard路径下
     * @param content
     * @param fileName
     * @param dir
     */
    fun saveFileToPublic(content: String, fileName: String, dir: String) {
        val parentPath = getSavePublicFileDir(dir) ?: return
        val file1 = File(parentPath)
        if (!file1.exists()) {
            file1.mkdirs()
        }
        val filePath = getSavePublicFileDir(dir) + File.separator + fileName
        val file = File(filePath)
        try {
            val fos = FileOutputStream(file)
            val bos = BufferedOutputStream(fos)
            bos.write(content.toByteArray(), 0, content.toByteArray().size)
            bos.flush()
            bos.close()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveFileToPublicWithAsync(content: String, fileName: String, dir: String) {
        mScope.launch { saveFileToPublic(content, fileName, dir) }
    }

    fun openUriForRead(uri: Uri?, contentCallback: (String)->Unit) {
        if (uri == null) return
        //获取输入流
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        try {
            val readContent = ByteArray(1024)
            var len = 0
            do {
                //读文件
                len = inputStream?.read(readContent) ?: return
                if (len != -1) {
                    val content = String(readContent).substring(0, len)
                    contentCallback(content)
                }
            } while (len != -1)

        } catch (e: Exception) {
            Log.d("test", e.localizedMessage)
        } finally {
            inputStream?.close()
        }
    }

    /**
     * 将file转换为inputStream
     * @param file
     * @return
     */
    fun file2InputStream(file: File): InputStream? {
        if (file.exists() && file.isFile) return FileInputStream(file)
        return null
    }

    /**
     * 将inputStream转化为file
     * @param is
     * @param file 要输出的文件目录
     */
    fun inputStream2File(inputStream: InputStream, file: File?) {
        var os: OutputStream? = null
        try {
            os = FileOutputStream(file)
            var len = 0
            val buffer = ByteArray(8192)
            while (inputStream.read(buffer).also { len = it } != -1) {
                os.write(buffer, 0, len)
            }
        } finally {
            os?.close()
            inputStream.close()
        }
    }

    fun saveBitmapToFile(bitmap: Bitmap?, filePath: String): File? {
        if (bitmap == null) return null
        val imageFile = File(filePath)
        try {
            val out = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            bitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageFile
    }

    @Throws
    fun getDataDataFile(name: String): File{
        return context.getFileStreamPath(name)
    }

    fun getExternalDataFile(name: String): File? {
        return context.getExternalFilesDir(name)
    }

    fun getUserConfigPath(name: String): String {
       return getDataDataFile(name).absolutePath
    }

    fun getUserConfigFile(name: String): File {
        val configFile = FileUtils2(context).getDataDataFile("user_config")
        if (!configFile.exists()) {
            configFile.mkdir()
        }
        return File(configFile.absolutePath, name)
    }

    fun getUserConfigFilePath(name: String): String {
        val configFile = FileUtils2(context).getDataDataFile("user_config")
        if (!configFile.exists()) {
            configFile.mkdir()
        }
        return File(configFile.absolutePath, name).absolutePath
    }

    fun saveFileToDataDataFolderWithAsync(content: String, name: String) {
        mScope.launch {
            saveFileToDataDataFolder(content, name)
        }
    }


    /**
     * Save file
     *  Context.MODE_PRIVATE	指定该文件数据只能被本应用程序读、写
        Context.MODE_WORLD_READABLE	指定该文件数据能被其他应用程序读，但不能写
        Context.MODE_WORLD_WRITEABLE	指定该文件数据能被其他应用程序读
        Context.MODE_APPEND	该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件；
     * @param content
     * @param name: 文件名称，不能包含路径
     */
    fun saveFileToDataDataFolder(name: String, content: String): Boolean {

        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE)
            fileOutputStream.write(content.toByteArray())
            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            fileOutputStream?.close()
        }
        return false
    }

    fun readFile(name: String, lineBlock: ((String) -> Unit)? = null): String? {
        var fileInputStream: FileInputStream? = null
        var reader: Reader? = null
        var bufferedReader: BufferedReader? = null
        try {
            fileInputStream = context.openFileInput(name)
            reader = InputStreamReader(fileInputStream) // 字符流
            bufferedReader = BufferedReader(reader) //缓冲流
            val result = StringBuilder()
            var temp: String?
            while (true) {
                temp = bufferedReader.readLine() ?: break
                lineBlock?.invoke(temp)
                result.append(temp)
            }
            return result.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (io: IOException) {
                io.printStackTrace()
            }
            try {
                fileInputStream?.close()
            } catch (io: IOException) {
                io.printStackTrace()
            }
            try {
                bufferedReader?.close()
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        return null
    }

}