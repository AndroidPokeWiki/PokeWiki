package com.example.pokewiki.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R
import com.example.pokewiki.bean.PokemonMoveBean
import com.example.pokewiki.utils.ColorDict
import com.ruffian.library.widget.RTextView
import com.ruffian.library.widget.RView

class MoveAdapter(private val itemList: ArrayList<PokemonMoveBean>, val context: Context) :
    RecyclerView.Adapter<MoveAdapter.MoveViewHolder>() {

    class MoveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val levelTv: RTextView = itemView.findViewById(R.id.pokemon_move_item_level)
        val typeTv: TextView = itemView.findViewById(R.id.pokemon_move_item_type)
        val typeBg: RView = itemView.findViewById(R.id.pokemon_move_item_type_bg)
        val nameTv: TextView = itemView.findViewById(R.id.pokemon_move_item_name)
        val powerTv: TextView = itemView.findViewById(R.id.pokemon_move_item_power_value)
        val ppTv: TextView = itemView.findViewById(R.id.pokemon_move_item_pp_value)
        val acuTv: TextView = itemView.findViewById(R.id.pokemon_move_item_acu_value)
        val classBg: RTextView = itemView.findViewById(R.id.pokemon_move_item_class)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.pokemon_move_item,
            parent, false
        )
        return MoveViewHolder(v)
    }

    override fun onBindViewHolder(holder: MoveViewHolder, position: Int) {
        val item = itemList[position]
        holder.levelTv.text = if (item.level == null) "暂无" else "LV ${item.level}"
        holder.typeTv.text = item.type_name
        holder.powerTv.text = if (item.power == null) "——" else item.power.toString()
        holder.ppTv.text = item.pp.toString()
        holder.acuTv.text = if (item.accuracy == null) "——" else item.accuracy.toString()
        holder.nameTv.text = item.move_name

        val typeColor = ColorDict.color[item.type_name]
        val typeHelper = holder.typeBg.helper
        if (typeColor != null)
            typeHelper.backgroundColorNormal = context.resources.getColor(typeColor, context.theme)

        val classHelper = holder.classBg.helper
        when (item.damage_type) {
            "物理" -> classHelper.backgroundColorNormal =
                context.resources.getColor(R.color.poke_ball_red, context.theme)
            "特殊" -> classHelper.backgroundColorNormal = Color.parseColor("#2266CC")
            "变化" -> classHelper.backgroundColorNormal =
                context.resources.getColor(R.color.general_gray, context.theme)
        }
        holder.classBg.text = item.damage_type
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}