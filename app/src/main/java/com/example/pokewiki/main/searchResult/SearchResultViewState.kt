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
        val loadingState: Int = HomeSearchViewState.FINISH,
        val refreshState: Int = HomeSearchViewState.FINISH,
        val result: ArrayList<PokemonSearchBean> = ArrayList(),
        val page: Int = 0
){
    companion object {
        const val LOADING = 1
        const val FINISH = 0
        const val NO_MORE = -1
        const val ERROR = -2
    }
}

sealed class SearchResultViewAction {
    data class UpdateKeyword(val keyword: String) : SearchResultViewAction()
    object ClickSearching : SearchResultViewAction()
    object ClickSearchingGen : SearchResultViewAction()
    object ClickSearchingType : SearchResultViewAction()

}

sealed class SearchResultViewEvent {
    object ShowLoadingDialog : SearchResultViewEvent()
    object DismissLoadingDialog : SearchResultViewEvent()
    object TransIntent : SearchResultViewEvent()
    data class ShowToast(val msg: String) : SearchResultViewEvent()
}