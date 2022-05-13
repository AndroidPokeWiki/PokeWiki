package com.example.pokewiki.repository

import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.utils.NO_MORE_DATA
import com.example.pokewiki.utils.NetworkState

class HomeSearchRepository {
    companion object {
        @Volatile
        private var instance: HomeSearchRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: HomeSearchRepository().also { instance = it }
            }
    }

    suspend fun getAllWithPage(page: Int): NetworkState<ArrayList<PokemonSearchBean>> {
        val result = try {
            ServerApi.create().getAllWithPage(page)
        } catch (e: Exception) {
            e.printStackTrace()
            return NetworkState.Error("未知错误，请联系管理员")
        }
        result.let {
            when (it.status) {
                200 -> return NetworkState.Success(it.data)
                201 -> return NetworkState.Error(NO_MORE_DATA)
                else -> return NetworkState.Error(it.msg ?: "未知错误，请联系管理员")
            }
        }
    }
}