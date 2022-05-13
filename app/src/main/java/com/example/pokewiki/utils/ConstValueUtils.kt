package com.example.pokewiki.utils

import com.example.pokewiki.R

const val SERVER_URL = "http://192.168.128.80:9457/"

// 信号
const val NO_MORE_DATA = "NO_MORE_DATA"

object ColorDict{
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
    }
}