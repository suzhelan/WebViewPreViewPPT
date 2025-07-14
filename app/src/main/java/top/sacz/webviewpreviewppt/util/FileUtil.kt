package top.sacz.webviewpreviewppt.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtil {
    /**
     * 从Uri获取文件并复制到本地
     */
    fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        // 创建临时文件
        val fileExtension = getFileExtension(context, uri)
        val tempFile = File.createTempFile(
            "temp_${System.currentTimeMillis()}",
            if (fileExtension.isNullOrEmpty()) null else ".$fileExtension",
            context.cacheDir
        )
        // 确保文件可写
        tempFile.deleteOnExit()
        // 将Uri内容复制到临时文件
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(4 * 1024) // 4KB buffer
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        } ?: throw IOException("无法打开Uri输入流")
        return tempFile
    }
    /**
     * 获取文件扩展名
     */
    private fun getFileExtension(context: Context, uri: Uri): String? {
        return when {
            uri.scheme == ContentResolver.SCHEME_CONTENT -> {
                val mimeType = context.contentResolver.getType(uri)
                if (mimeType != null) {
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                } else {
                    null
                }
            }

            else -> MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        }
    }
}