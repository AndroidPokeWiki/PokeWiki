package com.example.pokewiki.main.detail.info

import android.content.SharedPreferences
import com.example.pokewiki.bean.PokemonIntroBean


data class PokemonDetailInfoViewState(
    val pokemonInfo: PokemonIntroBean = PokemonIntroBean()
)

sealed class PokemonDetailInfoViewAction {
    data class ChangeData(val id: Int, val sp: SharedPreferences) : PokemonDetailInfoViewAction()
    object InitData : PokemonDetailInfoViewAction()
    data class WriteDataIntoStorage(
        val smallPath: String,
        val bigPath: String,
        val sp: SharedPreferences
    ) :
        PokemonDetailInfoViewAction()
}


sealed class PokemonDetailInfoViewEvent {
    object ShowLoadingDialog : PokemonDetailInfoViewEvent()
    object DismissLoadingDialog : PokemonDetailInfoViewEvent()
    data class ShowToast(val msg: String) : PokemonDetailInfoViewEvent()
    object WriteDataIntoStorage : PokemonDetailInfoViewEvent()
}