package com.example.test1.model.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.test1.Retrofit.APIClient
import com.example.test1.constant.Constants.Companion.LIST_KEY
import com.example.test1.database.NotiDao
import com.example.test1.model.notify.Hit
import com.example.test1.model.notify.NotifyResponse
import com.example.test1.utils.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifyDataSource() : PageKeyedDataSource<String, Hit>() {
    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    //private val mutableLiveData = MutableLiveData<List<Hit>>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Hit>) {

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        val userDataService = APIClient.client
        val call = userDataService.post

        call?.enqueue(object : Callback<NotifyResponse?> {
            override fun onResponse(call: Call<NotifyResponse?>, response: Response<NotifyResponse?>) {
                var data = listOf<Hit>()
                response.body()?.hits?.hits.let {
                    data.forEach {
                        if (LIST_KEY.containsKey(it.source?.iv104))
                            it.source.title =
                                    it.source.fi101[0].iv102 + LIST_KEY.containsKey(it.source?.iv104)

                    }

                    if (it != null) {
                        Log.i("Loading Init", "Size :" + response.body())
                        callback.onResult(it,null, makeSort(data.lastOrNull()?.sort))
                    }

                }
            }
            override fun onFailure(call: Call<NotifyResponse?>, t: Throwable) {
                Log.d("+++","ERROR : " + t.message)

            }
        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Hit>) {
        Log.i("Loading After", "Loading After " + params.key + " Count " + params.requestedLoadSize);
        networkState.postValue(NetworkState.LOADING)
        val userDataService = APIClient.client
        val call = userDataService.post

        call?.enqueue(object : Callback<NotifyResponse?> {
            override fun onResponse(call: Call<NotifyResponse?>, response: Response<NotifyResponse?>) {
                response.body()?.hits?.hits.let {
                    var data = listOf<Hit>()
                    data.forEach {
                        if (LIST_KEY.containsKey(it.source?.iv104))
                            it.source.title =
                                    it.source.fi101[0].iv102 + LIST_KEY.containsKey(it.source?.iv104)
                    }
                    if (it != null) {
                        callback.onResult(it, makeSort(data.lastOrNull()?.sort))
                        networkState.postValue(NetworkState.LOADED)
                    }

                }
            }
            override fun onFailure(call: Call<NotifyResponse?>, t: Throwable) {
                Log.d("Error","ERROR : " + t.message)
            }
        })
    }
    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Hit>) {
    }
    private fun makeSort(objects: List<Any>?): String {
        return try {
            objects?.run {
                val stringBuilder = StringBuilder("[")
                for (i in objects.indices) {
                    when (val o = objects[i]) {
                        is Long -> {
                            stringBuilder.append((o as Long).toLong())
                        }
                        is Double -> {
                            stringBuilder.append((o as Double).toDouble())
                        }
                        is String -> {
                            stringBuilder.append("\"")
                            stringBuilder.append(o)
                            stringBuilder.append("\"")
                        }
                    }
                    if (i < objects.size - 1) {
                        stringBuilder.append(",")
                    }
                }
                stringBuilder.append("]")
                stringBuilder.toString()
            } ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    private suspend fun SaveDataToDB(dao: NotiDao?, hits: List<Hit>) {
        dao?.deleteAll()
         dao?.insert(hits)
    }
}