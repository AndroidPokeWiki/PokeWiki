package com.example.pokewiki.searching

import android.content.SharedPreferences
import com.example.pokewiki.login.LoginViewAction

/**
 * created by DWF on 2022/5/21.
 */
data class SearchingViewState(
        val keyword: String = "",
        val error: Boolean = false
){
}

sealed class SearchingViewAction {
    data class UpdateKeyword(val keyword: String) : SearchingViewAction()
    object ClickSearching: SearchingViewAction()
    object ClickSearchingGen: SearchingViewAction()
    object ClickSearchingType: SearchingViewAction()
}

sealed class SearchingViewEvent {
    object ShowLoadingDialog : SearchingViewEvent()
    object DismissLoadingDialog : SearchingViewEvent()
    object TransIntent : SearchingViewEvent()
    data class ShowToast(val msg: String) : SearchingViewEvent()
}