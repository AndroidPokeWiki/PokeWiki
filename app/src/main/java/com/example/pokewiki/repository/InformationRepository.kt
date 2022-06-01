package com.example.pokewiki.repository

import android.util.Log
import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.utils.NetworkState
import com.example.pokewiki.utils.TOKEN_OUT_OF_DATE
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * created by DWF on 2022/5/28.
 */
class InformationRepository {
    companion object {
        @Volatile
        private var instance: InformationRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: InformationRepository().also { instance = it }
            }
    }

    suspend fun updateUsername(
        username: String,
        userID: String,
        token: String
    ): NetworkState<UserBean> {
        val result = try {
            ServerApi.create().updateUsername(username, userID, token)
        } catch (e: Exception) {
            e.printStackTrace()
            return NetworkState.Error("未知错误，请联系管理员")
        }
        result.let {
            return when (it.status) {
                200 -> NetworkState.Success(result.data)
                201 -> NetworkState.Error(TOKEN_OUT_OF_DATE)
                else -> NetworkState.Error(result.msg ?: "未知错误，请联系管理员")
            }
        }
    }

    suspend fun updateUserIcon(userID: String, profilePicture: File): NetworkState<String> {
        val iconRequestBody = RequestBody.create(MediaType.parse("image/*"), profilePicture)

        val builder = MultipartBody.Builder()
        builder.addFormDataPart("userId", userID)
            .addFormDataPart("profilePicture", profilePicture.name, iconRequestBody)
        builder.setType(MultipartBody.FORM)

        Log.e("TAG", "updateUserIcon: ${profilePicture.name}")
        val multipartBody = builder.build()

        val result = try {
            ServerApi.create().updateUserIcon(multipartBody)
        } catch (e: Exception) {
            e.printStackTrace()
            return NetworkState.Error("发生错误: $e")
        }
        result.let {
            return when (it.status) {
                200 -> NetworkState.Success(result.data)
                else -> NetworkState.Error(result.msg ?: "未知错误，请联系管理员")
            }
        }
    }

}