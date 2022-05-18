package com.example.pokewiki.detail.info

import com.example.pokewiki.bean.PokemonIntroBean


data class PokemonDetailInfoViewState(
    val pokemonInfo: PokemonIntroBean = PokemonIntroBean()
)

sealed class PokemonDetailInfoViewAction {
    data class ChangeData<T>(val id: T) : PokemonDetailInfoViewAction()
    object InitData : PokemonDetailInfoViewAction()
}


sealed class PokemonDetailInfoViewEvent {
    object ShowLoadingDialog : PokemonDetailInfoViewEvent()
    object DismissLoadingDialog : PokemonDetailInfoViewEvent()
    data class ShowToast(val msg: String) : PokemonDetailInfoViewEvent()
}