package com.example.pokewiki.main.homeSearch

import android.content.SharedPreferences
import com.example.pokewiki.bean.PokemonSearchBean

data class HomeSearchViewState(
    val loadingState: Int = FINISH,
    val refreshState: Int = FINISH,
    val pokemonList: ArrayList<PokemonSearchBean> = ArrayList(),
    val page: Int = 0
) {
    companion object {
        const val LOADING = 1
        const val FINISH = 0
        const val NO_MORE = -1
        const val ERROR = -2
    }
}

sealed class HomeSearchViewAction {
    data class LoadingNextPage(val sp: SharedPreferences) : HomeSearchViewAction()
    data class RefreshPage(val sp: SharedPreferences) : HomeSearchViewAction()
    data class GetCacheData(val sp: SharedPreferences) : HomeSearchViewAction()
    data class WriteToStorage(val path: String, val sp: SharedPreferences) : HomeSearchViewAction()
}

sealed class HomeSearchViewEvent {
    object ShowLoadingDialog : HomeSearchViewEvent()
    object DismissLoadingDialog : HomeSearchViewEvent()
    object WriteToStorage : HomeSearchViewEvent()
}