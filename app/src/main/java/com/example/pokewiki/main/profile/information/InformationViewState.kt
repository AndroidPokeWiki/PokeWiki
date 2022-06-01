package com.example.pokewiki.main.profile.information

import android.content.SharedPreferences
import java.io.File

/**
 * created by DWF on 2022/5/28.
 */
data class InformationViewState(
    val icon: String? = null,
    val name: String = "",
    val changeState: Boolean = false,
    val state: Int = NORMAL
) {
    companion object {
        const val NORMAL = 0
        const val SUCCESS = 1
        const val FAIL = -1
        const val TOKEN_OUT_OF_DATE = -9999
    }

    val isChanged: Boolean = state == SUCCESS || state == FAIL
}

sealed class InformationViewAction {
    object InitData : InformationViewAction()
    object SwitchState : InformationViewAction()
    data class ChangeIcon(val file: File, val sp: SharedPreferences) : InformationViewAction()
    data class UpdateUsername(val name: String) : InformationViewAction()
    data class ClickToChangeUsername(val sp: SharedPreferences) : InformationViewAction()
}

sealed class InformationViewEvent {
    object ShowLoadingDialog : InformationViewEvent()
    object DismissLoadingDialog : InformationViewEvent()

    data class ShowToast(val msg: String) : InformationViewEvent()
}