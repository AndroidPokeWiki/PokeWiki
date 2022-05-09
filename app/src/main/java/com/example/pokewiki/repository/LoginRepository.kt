package com.example.pokewiki.repository

import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.utils.NetworkState

class LoginRepository {
    companion object {
        @Volatile
        private var instance: LoginRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: LoginRepository().also { instance = it }
            }
    }

    suspend fun getAuth(email: String, password: String): NetworkState<UserBean> {
        val result = try {
            ServerApi.create().getAuth(email, password)
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