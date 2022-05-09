package com.example.pokewiki.register

/**
 * created by DWF on 2022/5/9.
 */
data class RegisterViewState(
        val email: String = "",
        val password: String = "",
        val confirm: String = "",
        val error: Boolean = false
) {
    companion object {
        const val SUCCESS = 0
        const val EMPTY = -1
        const val NOT_SAME = -2
    }

    val canRegister: Int =
            if (!(email.isNotBlank() && password.isNotBlank() && confirm.isNotBlank())) EMPTY
            else if (password != confirm) NOT_SAME
            else SUCCESS
    val errorText: String =
            if (error && canRegister == EMPTY) "所有参数不能为空"
            else if (error && canRegister == NOT_SAME) "两次密码不一致"
            else ""
}

sealed class RegisterViewAction {
    object ClickRegister : RegisterViewAction()
    data class UpdateUsername(val email: String) : RegisterViewAction()
    data class UpdatePassword(val password: String) : RegisterViewAction()
    data class UpdateConfirm(val confirm: String) : RegisterViewAction()
    data class ChangeErrorState(val error: Boolean) : RegisterViewAction()
}

sealed class RegisterViewEvent {
    object ShowLoadingDialog : RegisterViewEvent()
    object DismissLoadingDialog : RegisterViewEvent()
    object TransIntent : RegisterViewEvent()
    data class ShowToast(val msg: String) : RegisterViewEvent()
}