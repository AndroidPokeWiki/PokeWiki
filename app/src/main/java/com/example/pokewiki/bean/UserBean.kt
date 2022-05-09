package com.example.pokewiki.bean

/**
 * 用户信息数据类
 * @param authId 用户类型 普通用户 -> 1 管理员 -> 2
 * @param email 用户邮箱
 * @param profile_photo 用户头像url
 * @param token
 * @param userId 用户id
 * @param username 用户名
 */
data class UserBean(
    val authId : Int = 1,
    val email: String = "",
    val profile_photo: String = "",
    val token: String = "",
    val userId : String = "",
    val username: String = ""
)
