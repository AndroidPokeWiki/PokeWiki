package com.example.pokewiki.main.searchResult

import android.content.SharedPreferences
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.main.homeSearch.HomeSearchViewAction
import com.example.pokewiki.main.homeSearch.HomeSearchViewState

/**
 * created by DWF on 2022/5/25.
 */
data class SearchResultViewState(
        val keyword: String = "",
        val result: ArrayList<PokemonSearchBean> = ArrayList(),
        val page: Int = 0
)

sealed class SearchResultViewAction {
    data class UpdateKeyword(val keyword: String) : SearchResultViewAction()
    object ClickSearching : SearchResultViewAction()
    object ClickSearchingGen : SearchResultViewAction()
    object ClickSearchingType : SearchResultViewAction()
}

sealed class SearchResultViewEvent {
    object ShowLoadingDialog : SearchResultViewEvent()
    object DismissLoadingDialog : SearchResultViewEvent()
    data class ShowToast(val msg: String) : SearchResultViewEvent()
}