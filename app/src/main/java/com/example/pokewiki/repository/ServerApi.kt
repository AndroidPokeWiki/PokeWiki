package com.example.pokewiki.repository

import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.interceptor.LoggingInterceptor
import com.example.pokewiki.utils.ResponseData
import com.example.pokewiki.utils.SERVER_URL
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * 用于连接后台API的接口类，使用create函数获取实例调用对接方法
 */
interface ServerApi {

    @POST("login")
    @FormUrlEncoded
    suspend fun getAuth(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseData<UserBean>

    @POST("reg")
    @FormUrlEncoded
    suspend fun register(
            @Field("email") email: String,
            @Field("password") password: String
    ): ResponseData<UserBean>

    @POST("selectAllPokeIntro")
    @FormUrlEncoded
    suspend fun getAllWithPage(
        @Field("page") page: Int
    ): ResponseData<ArrayList<PokemonSearchBean>>

    @Streaming
    @GET
    suspend fun downloadFromUrl(@Url url: String): Response<ResponseBody>

    companion object {
        /**
         * 获取接口实例用于调用对接方法
         * @return ServerApi
         */
        fun create(): ServerApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor())
                .build()
            return Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ServerApi::class.java)
        }
    }
}