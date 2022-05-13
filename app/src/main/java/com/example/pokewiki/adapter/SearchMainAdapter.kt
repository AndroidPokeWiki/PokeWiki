package com.example.pokewiki.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pokewiki.R
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.utils.ColorDict
import com.example.pokewiki.utils.dip2px
import com.ruffian.library.widget.RTextView
import com.ruffian.library.widget.helper.RBaseHelper


class SearchMainAdapter(
    private val context: Context,
    private val itemList: ArrayList<PokemonSearchBean>
) :
    RecyclerView.Adapter<SearchMainAdapter.SearchMainViewHolder>() {

    class SearchMainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImg: ImageView = itemView.findViewById(R.id.search_main_item_img)
        val itemName: TextView = itemView.findViewById(R.id.search_main_item_name)
        val itemId: RTextView = itemView.findViewById(R.id.search_main_item_id)
        val itemAttrContainer: LinearLayout =
            itemView.findViewById(R.id.search_main_item_attr_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMainViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.search_main_item,
            parent, false
        )
        return SearchMainViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchMainViewHolder, position: Int) {
        val item = itemList[position]
        //模拟用户请求
        Glide.with(context).load(item.img_url).diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.itemImg)
        holder.itemName.text = item.pokemon_name
        holder.itemId.text = "#${item.pokemon_id}"

        //添加属性标签
        val viewList = ArrayList<View>()
        for (attr in item.pokemon_type) {
            val attrV =
                LayoutInflater.from(context).inflate(R.layout.attr_item, null)
            val attrC = attrV.findViewById<RTextView>(R.id.attr_container)
            val helper: RBaseHelper<*> = attrC.helper
            attrC.text = attr

            val color = ColorDict.color[attr]
            if (color != null)
                helper.backgroundColorNormal = context.getColor(color)

            val p = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            p.marginStart = dip2px(context, 10.0)
            attrV.layoutParams = p

            viewList.add(attrV)
        }
        // 刷新存档避免重复
        if (holder.itemAttrContainer.childCount != 0)
            holder.itemAttrContainer.removeAllViews()
        for (v in viewList)
            holder.itemAttrContainer.addView(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}