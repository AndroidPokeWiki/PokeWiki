package com.example.pokewiki.main.profile.resetPwd

import android.content.SharedPreferences

/**
 * created by DWF on 2022/5/28.
 */
data class ResetPwdViewState(
        val oldPassword: String = "",
        val newPassword: String = "",
        val changeState: Boolean = false
) {
    val canReset: Boolean = oldPassword.isNotBlank() && newPassword.isNotBlank()
}

sealed class ResetPwdViewAction {
    data class UpdateOldPassword(val oldPassword: String) : ResetPwdViewAction()
    data class UpdateNewPassword(val newPassword: String) : ResetPwdViewAction()
    data class ClickResetPWD(val sp: SharedPreferences) : ResetPwdViewAction()
}

sealed class ResetPwdViewEvent {
    object ShowLoadingDialog : ResetPwdViewEvent()
    object DismissLoadingDialog : ResetPwdViewEvent()
    data class ShowToast(val msg: String) : ResetPwdViewEvent()
}
