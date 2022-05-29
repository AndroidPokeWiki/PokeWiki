package com.example.pokewiki.main.profile.information

import android.content.SharedPreferences
import java.io.File

/**
 * created by DWF on 2022/5/28.
 */
data class InformationViewState(
        val icon: String = "",
        val name: String = "",
        val error: Boolean = false
)

sealed class InformationViewAction {
    data class ChangeIcon(val file: File) : InformationViewAction()
    data class UpdateUsername(val name: String) : InformationViewAction()
    data class ClickToChangeUsername(val sp: SharedPreferences) : InformationViewAction()
    object ChangeButtonToConfirm : InformationViewAction()
}

sealed class InformationViewEvent {
    object ShowLoadingDialog : InformationViewEvent()
    object DismissLoadingDialog : InformationViewEvent()
    data class ShowToast(val msg: String) : InformationViewEvent()
}