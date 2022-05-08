package com.example.pokewiki.login

data class LoginViewState(val email: String = "", val password: String = "") {
    val canLogin: Boolean = email.isNotBlank() && password.isNotBlank()
}

sealed class LoginViewAction {
    object ClickLogin : LoginViewAction()
    data class UpdateUsername(val username: String): LoginViewAction()
    data class UpdatePassword(val password: String): LoginViewAction()
}

sealed class LoginViewEvent {
    object ShowLoadingDialog : LoginViewEvent()
    object DismissLoadingDialog : LoginViewEvent()
    object TransIntent : LoginViewEvent()
    data class ShowToast(val msg: String) : LoginViewEvent()
}