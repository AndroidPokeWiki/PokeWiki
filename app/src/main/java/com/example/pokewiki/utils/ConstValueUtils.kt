package com.example.pokewiki.utils

import com.example.pokewiki.R
import com.example.pokewiki.bean.PokemonDetailBean
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.bean.UserBean

// 服务器地址
const val SERVER_URL = "http://114.116.49.217:9457/"
const val DOWNLOAD_URL = "http://114.116.49.217:9129/"

// 信号
const val NO_MORE_DATA = "NO_MORE_DATA"
const val SEARCH_GET_PERMISSION_FLAG = 1
const val PROFILE_GET_PERMISSION_FLAG = 2
const val PROFILE_EDIT_GET_PERMISSION_FLAG = 3
const val LOCAL_PIC = "LOCAL_PIC"
const val STATE_CHANGE = 0
const val NEED_RELOGIN = -9999
const val TOKEN_OUT_OF_DATE = "TOKEN_OUT_OF_DATE"

// 缓存
const val SHARED_NAME = "USER_INFO"
const val USER_DATA = "USER_DATA"
const val AUTO_SAVE = "AUTO_SAVE"
const val FIRST_ASK_AUTO_SAVE = "FIRST_ASK_AUTO_SAVE"
const val POKEMON_SMALL_PIC = "POKEMON_SMALL_PIC"
const val POKEMON_BIG_PIC = "POKEMON_BIG_PIC"
const val POKEMON_HOME_CACHE = "POKEMON_HOME_CACHE"
const val POKEMON_CACHE_PAGE = "POKEMON_CACHE_PAGE"
const val POKEMON_DETAIL_CACHE = "POKEMON_DETAIL_CACHE"

// 颜色字典
object ColorDict {
    val color = HashMap<String, Int>()

    init {
        color["一般"] = R.color.general_gray
        color["飞行"] = R.color.fly_blue
        color["火"] = R.color.fire_red
        color["超能力"] = R.color.psychic_pink
        color["水"] = R.color.water_blue
        color["虫"] = R.color.insect_green
        color["电"] = R.color.electric_yellow
        color["岩石"] = R.color.rock_brown
        color["草"] = R.color.grass_green
        color["幽灵"] = R.color.ghost_purple
        color["冰"] = R.color.ice_blue
        color["龙"] = R.color.dragon_purple
        color["格斗"] = R.color.fight_brown
        color["恶"] = R.color.evil_brown
        color["毒"] = R.color.poison_purple
        color["钢"] = R.color.steel_grey
        color["地面"] = R.color.ground_yellow
        color["妖精"] = R.color.fairy_pink
        color["第一世代"] = R.color.generation_1
        color["第二世代"] = R.color.generation_2
        color["第三世代"] = R.color.generation_3
        color["第四世代"] = R.color.generation_4
        color["第五世代"] = R.color.generation_5
        color["第六世代"] = R.color.generation_6
        color["第七世代"] = R.color.generation_7
        color["第八世代"] = R.color.generation_8
        color["black"] = R.color.pokemon_black
        color["blue"] = R.color.pokemon_blue
        color["brown"] = R.color.pokemon_brown
        color["gray"] = R.color.pokemon_gray
        color["green"] = R.color.pokemon_green
        color["pink"] = R.color.pokemon_pink
        color["purple"] = R.color.pokemon_purple
        color["red"] = R.color.pokemon_red
        color["white"] = R.color.pokemon_white
        color["yellow"] = R.color.pokemon_yellow
    }
}

// 用户信息
object AppContext {
    var userData: UserBean = UserBean()
    var autoSave: Boolean = false
    var pokeDetail : PokemonDetailBean = PokemonDetailBean()
    var searchData: ArrayList<PokemonSearchBean> = ArrayList()
}