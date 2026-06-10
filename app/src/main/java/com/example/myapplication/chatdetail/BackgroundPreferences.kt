package com.example.myapplication.chatdetail

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object BackgroundPreferences {
    private const val PREPS_NAME= "ChatSettings"

    fun saveBackGroundLocal(context: Context, myId: Int, targetId: Int, isGroup: Boolean, uri: Uri) {
        val sharedPref = context.getSharedPreferences(PREPS_NAME, Context.MODE_PRIVATE)
        val key = "bg_${myId}_${targetId}_${isGroup}"
        with(sharedPref.edit()) {
            putString(key, uri.toString())
            apply()
        }
    }

    fun getBackgroundLocal(context: Context, myId: Int, targetId: Int, isGroup: Boolean): Uri? {
        val sharePref = context.getSharedPreferences(PREPS_NAME, Context.MODE_PRIVATE)
        val key = "bg_${myId}_${targetId}_${isGroup}"
        val uriString = sharePref.getString(key, null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    fun clearBackgroundLocal(context: Context, myId: Int, targetId: Int, isGroup: Boolean) {
        val sharedPref = context.getSharedPreferences(PREPS_NAME, Context.MODE_PRIVATE)
        val key = "bg_${myId}_${targetId}_${isGroup}"
        with(sharedPref.edit()) {
            remove(key)
            apply()
        }
    }

    fun copyImageToInternalStorage(context: Context, uri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            val fileName = "bg_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}