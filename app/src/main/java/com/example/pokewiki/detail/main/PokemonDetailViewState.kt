package com.example.pokewiki.detail.main


data class PokemonDetailViewState(
    val id: Int = 0,
    val img: String = "",
    val name: String = "",
    val attrs: ArrayList<String> = ArrayList(),
    val is_like : Boolean = false
)

sealed class PokemonDetailViewAction{
    object SwitchLikeState : PokemonDetailViewAction()
    data class GetInitData(val id: Int) : PokemonDetailViewAction()
}


sealed class PokemonDetailViewEvent{
    object ShowLoadingDialog : PokemonDetailViewEvent()
    object DismissLoadingDialog : PokemonDetailViewEvent()
    data class ShowToast(val msg: String) : PokemonDetailViewEvent()
}