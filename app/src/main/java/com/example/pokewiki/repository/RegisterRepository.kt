package com.example.pokewiki.repository

import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.utils.NetworkState

/**
 * created by DWF on 2022/5/9.
 */
class RegisterRepository {

    companion object {
        @Volatile
        private var instance: RegisterRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: RegisterRepository().also { instance = it }
                }
    }

    suspend fun register(email: String, password: String): NetworkState<UserBean> {
        val result = try {
            ServerApi.create().register(email, password)
        } catch (e: Exception) {
            e.printStackTrace()
            return NetworkState.Error("未知错误，请联系管理员")
        }
        result.let {
            when (it.status) {
                200 -> return NetworkState.Success(it.data)
                else -> return NetworkState.Error(it.msg ?: "未知错误，请联系管理员")
            }
        }
    }

}