package com.example.pokewiki.repository

import com.example.pokewiki.interceptor.LoggingInterceptor
import com.example.pokewiki.utils.DOWNLOAD_URL
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface DownloadApi {
    @Streaming
    @GET("small/{pokemonID}.png")
    suspend fun getSmallPic(@Path("pokemonID") pokemonID: Int): Response<ResponseBody>

    @Streaming
    @GET("big/{pokemonID}.png")
    suspend fun getBigPic(@Path("pokemonID") pokemonID: Int): Response<ResponseBody>

    companion object {
        /**
         * 获取接口实例用于调用下载方法
         * @return ServerApi
         */
        fun create(): DownloadApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor())
                .build()
            return Retrofit.Builder()
                .baseUrl(DOWNLOAD_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(DownloadApi::class.java)
        }
    }
}