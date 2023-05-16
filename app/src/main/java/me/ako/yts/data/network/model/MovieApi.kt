package me.ako.yts.data.network.model

import android.util.Log

class Api {
    fun generateEndpoint(endpoint: Endpoint, vararg params: Endpoint.Parameter): String {
        var pre = "${endpoint.url}?"
        Log.d("Api", "generateEndpoint: size = ${params.size}")
        if (params.size == 1) {
            pre += params[0]
        } else if (params.size > 1) {
            params.forEach {
                pre += if (it == params.last()) {
                    it.param
                } else {
                    it.param + "&"
                }
            }
        }
        return pre
    }

    sealed class Endpoint(val url: String) {
        object ListMovies : Endpoint("list_movies.json")
        object SearchMovies : Endpoint("list_movies.json?sort_by=year&order_by=desc")

        sealed class Parameter(val param: String) {
            sealed class Order(val type: String) : Parameter("$value=$type") {
                companion object {
                    const val value = "order_by"
                }

                object Asc : Order("asc")
                object Desc : Order("desc")
            }

            sealed class Sort(val type: String) : Parameter("$value=$type") {
                companion object {
                    const val value = "sort_by"
                }

                object Title : Sort("title")
                object Year : Sort("year")
                object Rating : Sort("rating")
                object Peers : Sort("peers")
                object Seeds : Sort("seeds")
                object DownloadCount : Sort("download_count")
                object LikeCount : Sort("like_count")
                object DateAdded : Sort("date_added")
            }
        }
    }
}