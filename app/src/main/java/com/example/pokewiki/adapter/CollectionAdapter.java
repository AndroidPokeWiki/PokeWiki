package com.example.pokewiki.adapter;

import static com.example.pokewiki.utils.StaticFunctionUtilsKt.dip2px;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokewiki.R;
import com.example.pokewiki.bean.PokemonSearchBean;
import com.example.pokewiki.main.detail.main.PokemonDetailActivity;
import com.example.pokewiki.main.profile.collection.CollectionViewAction;
import com.example.pokewiki.main.profile.collection.CollectionViewModel;
import com.example.pokewiki.utils.ColorDict;
import com.ruffian.library.widget.RImageView;
import com.ruffian.library.widget.RTextView;
import com.ruffian.library.widget.helper.RBaseHelper;

import java.util.ArrayList;

/**
 * created by DWF on 2022/6/1.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private Context mContext;
    private ArrayList<PokemonSearchBean> mData;
    private CollectionViewModel mViewModel;

    public CollectionAdapter(Context mContext, ArrayList<PokemonSearchBean> mData, CollectionViewModel viewModel) {
        this.mContext = mContext;
        this.mData = mData;
        this.mViewModel = viewModel;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.collection_item, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position).getImg_url()).into(holder.image);
        holder.name.setText(mData.get(position).getPokemon_name());

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

        if (position == mData.size() - 1) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.itemBtn.getLayoutParams();
            p.bottomMargin = dip2px(mContext, 20.0);
            holder.itemBtn.setLayoutParams(p);
        }

        int pokemonID = Integer.parseInt(mData.get(position).getPokemon_id());
        holder.unlike.setOnClickListener(view -> mViewModel.dispatch(new CollectionViewAction.CancelCollection(pokemonID)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {

        private RImageView image;
        private TextView name;
        private LinearLayout attribute;
        private ImageButton unlike;
        private CardView itemBtn;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.collection_item_img);
            name = itemView.findViewById(R.id.collection_item_name);
            attribute = itemView.findViewById(R.id.collection_item_attribute);
            unlike = itemView.findViewById(R.id.collection_item_unlike);
            itemBtn = itemView.findViewById(R.id.profile_main_collection);
        }
    }

}
