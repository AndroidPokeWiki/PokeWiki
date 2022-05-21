package com.example.pokewiki.bean

data class PokemonMovesBean(
    val moves: ArrayList<PokemonMoveBean> = ArrayList()
)

data class PokemonMoveBean(
    val accuracy: Int? = 0,
    val damage_type: String = "",
    val level: Int? = null,
    val move_id: Int = 0,
    val move_name: String = "",
    val power: Int? = 0,
    val pp: Int = 0,
    val type_name: String = ""
)