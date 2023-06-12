package me.ako.yts.domain.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri

class FileDownloader(private val context: Context): Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadTorrent(url: String?, title: String?): Long {
        /*val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)*/
        val request = DownloadManager.Request(url?.toUri())
            .setTitle(title)
            .setAllowedOverMetered(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "YTS/$title")
        return downloadManager.enqueue(request)
    }

    override fun downloadImage(url: String, title: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(title)
            .setAllowedOverMetered(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "YTS/$title")
        return downloadManager.enqueue(request)
    }
}

interface Downloader {
    fun downloadTorrent(url: String?, title: String?): Long
    fun downloadImage(url: String, title: String): Long
}