package com.example.pokewiki.utils

/**
 * 响应体通用类
 * @param T 响应体包含的数据的类型
 * @param status 响应体返回的状态码
 * @param msg 响应体返回的信息
 */
data class ResponseData<T>(val status: Int, val data: T, val msg: String?)

/**
 * 响应处理包装通用类
 * 成功时返回 NetworkState.Success 包含响应返回的数据
 * 失败时返回 NetworkState.Error 包含响应返回的错误信息
 * @param T 响应体包含的数据的类型
 */
sealed class NetworkState<out T> {
    data class Success<T>(val data: T) : NetworkState<T>()
    data class Error<T>(val msg: String) : NetworkState<T>() {
        constructor(t: Throwable) : this(t.message ?: "")
    }
}