package me.ako.yts.domain.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.ako.yts.data.network.model.Api
import kotlin.math.ceil

class Utils(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")
    private val MOVIES_SIZE = intPreferencesKey("movies_size")
    private val LAST_SCROLL_POSITION = intPreferencesKey("last_scroll_position")
    private val SORT = stringPreferencesKey("sort")
    private val ORDER = stringPreferencesKey("order")

    val moviesSizeFlow: Flow<Int> = context.dataStore.data.map {
        it[MOVIES_SIZE] ?: 0
    }
    val lastScrollPosition: Flow<Int> = context.dataStore.data.map {
        it[LAST_SCROLL_POSITION] ?: 0
    }

    val sortFlow: Flow<String> = context.dataStore.data.map {
        it[SORT] ?: Api.Endpoint.Parameter.Sort.DownloadCount.type
    }

    val orderFlow: Flow<String> = context.dataStore.data.map {
        it[ORDER] ?: Api.Endpoint.Parameter.Order.Desc.type
    }

    suspend fun updateMoviesSize(size: Int) {
        context.dataStore.edit {
            it[MOVIES_SIZE] = size
            Log.d("Utils", "updateMoviesSize: ${it[MOVIES_SIZE]}")
        }
    }

    suspend fun updateLastScrollPosition(position: Int) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit {
                if (position >= 0) {
                    it[LAST_SCROLL_POSITION] = position
                }
                Log.d("Utils", "updateLastScrollPosition: ${it[LAST_SCROLL_POSITION]}")
            }
        }
    }

    suspend fun updateSort(sort: String) {
        context.dataStore.edit {
            it[SORT] = sort
            Log.d("Utils", "sort: ${it[SORT]}")
        }
    }

    suspend fun updateOrder(order: String) {
        context.dataStore.edit {
            it[ORDER] = order
            Log.d("Utils", "order: ${it[ORDER]}")
        }
    }

    fun setTheme(value: String?) {
        when (value) {
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            "system" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun deleteCache() {
        Log.d("Utils", "cacheDir: ${context.cacheDir.absolutePath}")
        context.cacheDir.deleteRecursively()
    }

    /*fun deleteCache(): Boolean {
        return try {
            val dir = context.cacheDir
            deleteDir(dir)
            true
        } catch (e: IOException) {
            Log.e("Utils", "deleteCache: ${e.message}")
            false
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if(dir != null && dir.isDirectory) {
            val children = dir.list()
            if(children != null) {
                for(i in 0..children.size) {
                    deleteDir(File(dir, children[i]))
                }
            }
            dir.delete()
        } else if(dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }*/

    fun shortenNumber(count: Long, decimal: Int): String {
        val short = if (count in 1000..999999) {
            val k = count.toDouble() / 1000
            "${ceil(k * decimal) / decimal}K"
        } else if (count >= 1000000) {
            val m = count.toDouble() / 1000000
            "${ceil(m * decimal) / decimal}M"
        } else {
            count.toString()
        }

        return short
    }

    fun url(url: String) {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun shareText(extra: String?): Intent = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, extra)
    }, null)

    fun shareTextWithPreview(extra: String, extraTitle: String, contentUri: Uri, title: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_TEXT, extra)
            putExtra(Intent.EXTRA_TITLE, extraTitle)
            data = contentUri
        }

        context.startActivity(Intent.createChooser(intent, title))
    }

    fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun snack(layout: View, text: String) {
        Snackbar.make(layout, text, Snackbar.LENGTH_LONG).show()
    }

    fun imdbTitle(code: String?): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$code/"))

    fun imdbName(code: String?): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/name/nm$code/"))

    private fun youtubeAppUri(url: String): Uri {
        return Uri.parse("vnd.youtube:" + Uri.parse(url).getQueryParameter("v"))
    }

    private fun youtubeVideoCode(url: String): String {
        var code = url.substringAfter("watch?v=")
        if (url.contains("youtu.be")) {
            code = url.substringAfterLast("/")
        }
        return code
    }

    fun youtubeThumbnail(code: String): String {
        return "https://img.youtube.com/vi/$code/0.jpg"
    }

    fun youtube(code: String?): Intent {
        // https://www.youtube.com/watch?v=Euy4Yu6B3nU
        // https://youtu.be/Euy4Yu6B3nU

        val url = "https://www.youtube.com/watch?v=$code"

        var intent = Intent(Intent.ACTION_VIEW, youtubeAppUri(url))
        if (intent.resolveActivity(context.packageManager) == null) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
        return intent
    }
}