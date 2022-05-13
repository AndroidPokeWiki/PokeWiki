package com.example.pokewiki.bean

data class PokemonSearchBean(
    val img_url: String = "",
    val pokemon_id: String = "",
    val pokemon_name: String = "",
    val pokemon_type: ArrayList<String> = ArrayList()
)
