package com.example.pokewiki.login

import android.content.SharedPreferences

data class LoginViewState(
    val email: String = "",
    val password: String = "",
    val error: Boolean = false
) {
    val canLogin: Boolean = email.isNotBlank() && password.isNotBlank()
    val errorText: String = if (!error) "" else "邮箱及密码不能为空"
}

sealed class LoginViewAction {
    data class CheckLoginInfo(val sp : SharedPreferences) : LoginViewAction()
    data class ClickLogin(val sp : SharedPreferences) : LoginViewAction()
    data class UpdateUsername(val email: String) : LoginViewAction()
    data class UpdatePassword(val password: String) : LoginViewAction()
    data class ChangeErrorState(val error: Boolean) : LoginViewAction()
}

sealed class SearchResultViewEvent {
    object ShowLoadingDialog : SearchResultViewEvent()
    object DismissLoadingDialog : SearchResultViewEvent()
    object TransIntent : SearchResultViewEvent()
    data class ShowToast(val msg: String) : SearchResultViewEvent()
}