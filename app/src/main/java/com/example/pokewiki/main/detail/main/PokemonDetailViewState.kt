package com.example.pokewiki.main.detail.main

import android.content.SharedPreferences


data class PokemonDetailViewState(
    val id: String = "",
    val img: String = "",
    val name: String = "",
    val color: String = "",
    val attrs: ArrayList<String> = ArrayList(),
    val is_like: Boolean = false,
    val likeError: Boolean = false
)

sealed class PokemonDetailViewAction {
    data class SwitchLikeState(val sp: SharedPreferences) : PokemonDetailViewAction()
    data class GetInitData(val id: Int, val sp: SharedPreferences) : PokemonDetailViewAction()
    object RefreshData : PokemonDetailViewAction()
    object ResetError : PokemonDetailViewAction()
    data class WriteDataIntoStorage(
        val smallPath: String,
        val bigPath: String,
        val sp: SharedPreferences
    ) :
        PokemonDetailViewAction()
}


sealed class PokemonDetailViewEvent {
    object ShowLoadingDialog : PokemonDetailViewEvent()
    object DismissLoadingDialog : PokemonDetailViewEvent()
    data class ShowToast(val msg: String) : PokemonDetailViewEvent()
    object WriteDataIntoStorage : PokemonDetailViewEvent()
}