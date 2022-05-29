package com.example.pokewiki.main.profile.collection

import com.example.pokewiki.bean.PokemonSearchBean

/**
 * created by DWF on 2022/5/29.
 */
data class CollectionViewState(
        var data: List<PokemonSearchBean>,
        var error: Boolean
)

sealed class InformationViewAction {

}

sealed class InformationViewEvent {
    object ShowLoadingDialog : InformationViewEvent()
    object DismissLoadingDialog : InformationViewEvent()
    data class ShowToast(val msg: String) : InformationViewEvent()
}
