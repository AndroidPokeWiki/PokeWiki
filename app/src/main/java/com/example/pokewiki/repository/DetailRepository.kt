package com.example.pokewiki.repository

import com.example.pokewiki.bean.PokemonDetailBean
import com.example.pokewiki.utils.NetworkState
import okhttp3.ResponseBody

class DetailRepository {
    companion object {
        @Volatile
        private var instance: DetailRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: DetailRepository().also { instance = it }
            }

        enum class Type {
            Big, Small
        }
    }

    suspend fun getInitData(
        pokemon_id: Int,
        user_id: String
    ): NetworkState<PokemonDetailBean> {
        val result = ServerApi.create().getPokemonDetail(pokemon_id, user_id)

        return when (result.status) {
            200 -> NetworkState.Success(result.data)
            else -> NetworkState.Error(result.msg ?: "未知错误，请联系管理员")
        }
    }

    suspend fun like(
        pokemon_id: Int,
        user_id: String
    ): NetworkState<Any> {
        val result = ServerApi.create().like(user_id, pokemon_id)

        return when (result.status) {
            200 -> NetworkState.Success(result.data)
            else -> NetworkState.Error(result.msg ?: "未知错误，请联系管理员")
        }
    }

    suspend fun unlike(
        pokemon_id: Int,
        user_id: String
    ): NetworkState<Any> {
        val result = ServerApi.create().unlike(user_id, pokemon_id)

        return when (result.status) {
            200 -> NetworkState.Success(result.data)
            else -> NetworkState.Error(result.msg ?: "未知错误，请联系管理员")
        }
    }

    suspend fun getImageWithTypeAndID(
        type: Type,
        id: Int
    ): NetworkState<ResponseBody> {
        val result = try {
            when (type) {
                Type.Big -> DownloadApi.create().getBigPic(id)
                Type.Small -> DownloadApi.create().getSmallPic(id)
            }
        } catch (e: java.lang.Exception) {
            return NetworkState.Error(e)
        }

        result.let {
            if (it.body() != null) {
                return NetworkState.Success(it.body()!!)
            } else {
                return NetworkState.Error("服务器无回应")
            }
        }
    }
}