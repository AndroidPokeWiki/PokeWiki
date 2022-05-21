package com.example.pokewiki.repository

import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.utils.NetworkState

/**
 * created by DWF on 2022/5/21.
 */
class SearchingRepository {

    companion object {
        @Volatile
        private var instance: SearchingRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: SearchingRepository().also { instance = it }
                }
    }

    suspend fun searchByName(keyword: String): NetworkState<ArrayList<PokemonSearchBean>> {
        val result = try {
            ServerApi.create().getPokemonByName(keyword)
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

    suspend fun searchByGen(keyword: String): NetworkState<ArrayList<PokemonSearchBean>> {
        val result = try {
            ServerApi.create().getPokemonByGen(keyword)
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

    suspend fun searchByType(keyword: String): NetworkState<ArrayList<PokemonSearchBean>> {
        val result = try {
            ServerApi.create().getPokemonByType(keyword)
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

}