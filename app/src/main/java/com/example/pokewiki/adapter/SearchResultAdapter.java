package com.example.pokewiki.adapter;

import static com.example.pokewiki.utils.StaticFunctionUtilsKt.dip2px;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokewiki.R;
import com.example.pokewiki.bean.PokemonSearchBean;
import com.example.pokewiki.detail.main.PokemonDetailActivity;
import com.example.pokewiki.utils.ColorDict;
import com.ruffian.library.widget.RTextView;
import com.ruffian.library.widget.helper.RBaseHelper;

import java.util.ArrayList;
import java.util.List;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private Context mContext;
    private List<PokemonSearchBean> mData;

    public SearchResultAdapter(Context mContext, List<PokemonSearchBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, parent, false);
        return new SearchResultViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position).getImg_url()).into(holder.image);
        holder.name.setText(mData.get(position).getPokemon_name());
        holder.itemId.setText("#" + mData.get(position).getPokemon_id());

        ArrayList<View> viewList = new ArrayList<>();
        for (String attr : mData.get(position).getPokemon_type()) {
            View attrView = LayoutInflater.from(mContext).inflate(R.layout.attr_item, null);
            RTextView attrContent = attrView.findViewById(R.id.attr_container);
            RBaseHelper helper = attrContent.getHelper();
            attrContent.setText(attr);

            int color = ColorDict.INSTANCE.getColor().get(attr);
            helper.setBackgroundColorNormal(mContext.getColor(color));

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            if (!attr.equals(mData.get(position).getPokemon_type().get(0))) {
                p.setMarginStart(dip2px(mContext, 10.0));
                attrView.setLayoutParams(p);
            }

            viewList.add(attrView);
        }

        if (holder.attribute.getChildCount() != 0) {
            holder.attribute.removeAllViews();
        }
        for (View view : viewList) {
            holder.attribute.addView(view);
        }

        holder.itemBtn.setOnClickListener(view -> {
            mContext.startActivity(
                    new Intent(mContext, PokemonDetailActivity.class).putExtra("id", mData.get(position).getPokemon_id())
            );
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class SearchResultViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private LinearLayout attribute;
        private CardView itemBtn;
        private RTextView itemId;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.search_result_item_img);
            name = itemView.findViewById(R.id.search_result_item_name);
            attribute = itemView.findViewById(R.id.search_result_item_type_container);
            itemBtn = itemView.findViewById(R.id.search_result_item_btn);
            itemId = itemView.findViewById(R.id.search_result_item_id);
        }


    }
}
