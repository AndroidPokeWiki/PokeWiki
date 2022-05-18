package com.example.pokewiki.detail.main


data class PokemonDetailViewState(
    val id: String = "",
    val img: String = "",
    val name: String = "",
    val color: String = "",
    val attrs: ArrayList<String> = ArrayList(),
    val is_like : Boolean = false
)

sealed class PokemonDetailViewAction{
    object SwitchLikeState : PokemonDetailViewAction()
    data class GetInitData<T>(val id: T) : PokemonDetailViewAction()
}


sealed class PokemonDetailViewEvent{
    object ShowLoadingDialog : PokemonDetailViewEvent()
    object DismissLoadingDialog : PokemonDetailViewEvent()
    data class ShowToast(val msg: String) : PokemonDetailViewEvent()
}