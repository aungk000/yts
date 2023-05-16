package me.ako.yts.domain.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.round

class Utils(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")
    private val MOVIES_SIZE = intPreferencesKey("movies_size")
    private val LAST_SCROLL_POSITION = intPreferencesKey("last_scroll_position")
    val moviesSizeFlow: Flow<Int> = context.dataStore.data.map {
        it[MOVIES_SIZE] ?: 0
    }
    val lastScrollPosition: Flow<Int> = context.dataStore.data.map {
        it[LAST_SCROLL_POSITION] ?: 0
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
                if(position >= 0) {
                    it[LAST_SCROLL_POSITION] = position
                }
                Log.d("Utils", "updateLastScrollPosition: ${it[LAST_SCROLL_POSITION]}")
            }
        }
    }

    fun getScrollPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        return layoutManager.findFirstVisibleItemPosition()
    }

    fun shortenNumber(count: Int, decimal: Int): String {
        var short = ""
        if (count in 1000..999999) {
            val k = count.toDouble() / 1000
            short = "${ceil(k * decimal) / decimal} K"
        } else if (count >= 1000000) {
            val m = count.toDouble() / 1000000
            short = "${ceil(m * decimal) / decimal} M"
        } else {
            short = count.toString()
        }

        return short
    }

    fun shortenNumber(count: Long, decimal: Int): String {
        var short = ""
        if (count in 1000..999999) {
            val k = count.toDouble() / 1000
            short = "${ceil(k * decimal) / decimal} K"
        } else if (count >= 1000000) {
            val m = count.toDouble() / 1000000
            short = "${ceil(m * decimal) / decimal} M"
        } else {
            short = count.toString()
        }

        return short
    }

    fun url(url: String) {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun imdbTitle(code: String?) {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$code/"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun imdbName(code: String?) {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/name/nm$code/"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun youtubeAppUri(url: String): Uri {
        return Uri.parse("vnd.youtube:" + Uri.parse(url).getQueryParameter("v"))
    }

    fun youtubeVideoCode(url: String): String {
        var code = url.substringAfter("watch?v=")
        if (url.contains("youtu.be")) {
            code = url.substringAfterLast("/")
        }
        return code
    }

    fun youtubeThumbnail(code: String): String {
        return "https://img.youtube.com/vi/$code/0.jpg"
    }

    fun youtube(code: String?) {
        // https://www.youtube.com/watch?v=Euy4Yu6B3nU
        // https://youtu.be/Euy4Yu6B3nU

        val url = "https://www.youtube.com/watch?v=$code"

        var intent = Intent(Intent.ACTION_VIEW, youtubeAppUri(url))
        if (intent.resolveActivity(context.packageManager) == null) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
        context.startActivity(intent)
    }
}