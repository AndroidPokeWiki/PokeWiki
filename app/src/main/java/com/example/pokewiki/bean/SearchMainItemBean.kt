package com.example.pokewiki.bean

import android.graphics.drawable.Drawable

data class SearchMainItemBean(
    val drawable: Drawable,
    val name: String,
    val attrList: ArrayList<String>,
    val id: String
)
