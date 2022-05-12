package com.example.pokewiki.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R
import com.example.pokewiki.bean.SearchMainItemBean
import com.example.pokewiki.utils.ColorDict
import com.example.pokewiki.utils.dip2px
import com.ruffian.library.widget.RTextView
import com.ruffian.library.widget.helper.RBaseHelper


class SearchMainAdapter(
    private val context: Context,
    private val itemList: ArrayList<SearchMainItemBean>
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

    override fun onBindViewHolder(holder: SearchMainViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemImg.setImageDrawable(item.drawable)
        holder.itemName.text = item.name
        holder.itemId.text = item.id
        for (attr in item.attrList) {
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
            holder.itemAttrContainer.addView(attrV)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}