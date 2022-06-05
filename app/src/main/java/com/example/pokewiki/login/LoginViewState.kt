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
    data class ClickLogin(val sp : SharedPreferences) : LoginViewAction()
    data class UpdateUsername(val email: String) : LoginViewAction()
    data class UpdatePassword(val password: String) : LoginViewAction()
    data class ChangeErrorState(val error: Boolean) : LoginViewAction()
}

sealed class LoginViewEvent {
    object ShowLoadingDialog : LoginViewEvent()
    object DismissLoadingDialog : LoginViewEvent()
    object TransIntent : LoginViewEvent()
    data class ShowToast(val msg: String) : LoginViewEvent()
}