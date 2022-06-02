package com.example.pokewiki.bean

data class PokemonDetailBean(
    val img_url: String = "",
    var is_star: Int = 0,
    val pokemon_color: String = "",
    val pokemon_id : String = "",
    val pokemon_name : String = "",
    val pokemon_type : ArrayList<String> = ArrayList(),

    val poke_intro : PokemonIntroBean = PokemonIntroBean(),
    val poke_stat : PokemonStateBean = PokemonStateBean(),
    val poke_moves : PokemonMovesBean = PokemonMovesBean()
)