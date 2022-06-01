package com.example.pokewiki.main.profile.collection

import com.example.pokewiki.bean.PokemonSearchBean

/**
 * created by DWF on 2022/5/29.
 */
data class CollectionViewState(
        val data: ArrayList<PokemonSearchBean> = ArrayList(),
        var error: Boolean = false
)

sealed class CollectionViewAction {
    data class CancelCollection(val pokemonID: Int) : CollectionViewAction()
    object GetMyCollection : CollectionViewAction()
}

sealed class CollectionViewEvent {
    object ShowLoadingDialog : CollectionViewEvent()
    object DismissLoadingDialog : CollectionViewEvent()
    data class ShowToast(val msg: String) : CollectionViewEvent()
}
