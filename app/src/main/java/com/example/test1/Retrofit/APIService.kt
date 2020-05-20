package com.example.test1.Retrofit

import com.example.test1.model.Post
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @get:GET("notis?t=information,order,order_n_busi,wallet,business,info_n_busi,info_n_wallet,busi_n_wallet,order_n_wallet,info_n_busi_n_wallet,order_n_busi_n_wallet&u=5bd2ec89a7262a092eb062f7&l=50")
    val post: Call<Post?>?

}