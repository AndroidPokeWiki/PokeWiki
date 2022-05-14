package com.example.pokewiki.bean

import java.io.File

data class PokemonSearchBean(
    val img_path : String = "",
    val img_url: String = "",
    val pokemon_id: String = "",
    val pokemon_name: String = "",
    val pokemon_type: ArrayList<String> = ArrayList()
)
