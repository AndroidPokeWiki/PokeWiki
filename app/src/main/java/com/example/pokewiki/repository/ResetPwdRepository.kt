package com.example.pokewiki.repository

import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.utils.NetworkState

/**
 * created by DWF on 2022/5/28.
 */
class ResetPwdRepository {
    companion object {
        @Volatile
        private var instance: ResetPwdRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: ResetPwdRepository().also { instance = it }
                }
    }

    suspend fun updatePassword(oldPassword: String, newPassword: String, userID: String, token: String): NetworkState<UserBean> {
        val result = try {
            ServerApi.create().updatePassword(oldPassword,newPassword, userID, token)
        } catch (e: Exception) {
            e.printStackTrace()
            return NetworkState.Error("未知错误，请联系管理员")
        }
        result.let {
            return when (it.status) {
                200 -> NetworkState.Success(result.data)
                else -> NetworkState.Error(result.msg ?: "未知错误，请联系管理员")
            }
        }
    }
}