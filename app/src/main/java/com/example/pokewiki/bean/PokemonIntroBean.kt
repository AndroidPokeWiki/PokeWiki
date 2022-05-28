package com.example.pokewiki.bean

data class PokemonIntroBean(
    val intro_text: String? = "",
    val poke_evolution: ArrayList<PokemonEvolutionBean> = ArrayList(),
    val general_abilities: ArrayList<String> = ArrayList(),
    val hidden_abilities: ArrayList<String>? = ArrayList(),
    val genus: String = "",
    val habitat: String? = "",
    val shape: String = ""
)

data class PokemonEvolutionBean(
    val id: Int = 0,
    val img_url: String = "",
    val min_level: Int = 0,
    val name: String = ""
)