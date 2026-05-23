package com.example.myapplication.chatdetail

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@SuppressLint("Range")
fun getOriginalFileName(context: Context, uri: Uri): String {
    var result:String? = null
    if(uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if(cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor?.close()
        }
        }
    if(result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if(cut != -1) {
            result = result?.substring(cut!! +1)
        }
    }
    val finalName = result ?: "file_${System.currentTimeMillis()}"

    if (!finalName.contains(".")) {
        val mimeType = context.contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        if (extension != null) {
            return "$finalName.$extension"
        } else {
            if (mimeType != null && mimeType.startsWith("image")) {
                return "$finalName.jpg"
            }
        }
    }

    return finalName
}
fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        val realFileName = getOriginalFileName(context,uri)
        val tempFile = File(context.cacheDir, realFileName)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
