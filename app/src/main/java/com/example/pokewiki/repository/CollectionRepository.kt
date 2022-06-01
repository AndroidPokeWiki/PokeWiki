package com.example.pokewiki.repository

import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.utils.NetworkState

/**
 * created by DWF on 2022/6/1.
 */
class CollectionRepository {

    companion object {
        @Volatile
        private var instance: CollectionRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: CollectionRepository().also { instance = it }
                }
    }

    suspend fun getMyCollection(userID: String): NetworkState<ArrayList<PokemonSearchBean>> {
        val result = try {
            ServerApi.create().getMyCollection(userID)
        } catch (e: Exception) {
            e.printStackTrace()
            return NetworkState.Error("未知错误，请联系管理员")
        }
        result.let {
            return when (it.status) {
                200 -> NetworkState.Success(it.data)
                else -> NetworkState.Error(it.msg ?: "未知错误，请联系管理员")
            }
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

}