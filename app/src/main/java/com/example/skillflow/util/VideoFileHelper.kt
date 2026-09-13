package com.example.skillflow.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VideoFileHelper {

    fun createCameraVideoUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
        val videoFile = File(storageDir, "VID_$timeStamp.mp4")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            videoFile
        )
    }

    fun saveVideoToInternalStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val videosDir = File(context.filesDir, "videos")
            if (!videosDir.exists()) videosDir.mkdirs()

            val destFile = File(videosDir, "nugget_video_$timeStamp.mp4")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            sourceUri.toString()
        }
    }

    fun deleteInternalVideoFile(context: Context, videoPath: String?) {
        if (videoPath.isNullOrEmpty()) return
        try {
            if (videoPath.startsWith("/")) {
                val file = File(videoPath)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
