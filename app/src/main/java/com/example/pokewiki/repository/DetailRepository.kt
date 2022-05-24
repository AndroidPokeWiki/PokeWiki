package com.example.pokewiki.repository

import com.example.pokewiki.bean.PokemonDetailBean
import com.example.pokewiki.utils.NetworkState

class DetailRepository {
    companion object {
        @Volatile
        private var instance: DetailRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: DetailRepository().also { instance = it }
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
}