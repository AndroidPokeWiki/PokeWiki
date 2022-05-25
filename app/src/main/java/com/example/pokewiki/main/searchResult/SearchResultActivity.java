package com.example.pokewiki.main.searchResult;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokewiki.R;

/**
 * created by DWF on 2022/5/25.
 */
public class SearchResultActivity extends AppCompatActivity {

    private EditText mInput;
    private ImageButton mSearchBtn;
    private RecyclerView mItemContainer;
    private ImageButton mBackBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);
        initView();
    }

    private void initView() {
        mInput = findViewById(R.id.search_result_input);
        mSearchBtn = findViewById(R.id.search_result_search_btn);
        mItemContainer = findViewById(R.id.search_result_item_container);
        mBackBtn = findViewById(R.id.search_result_back_btn);
        mBackBtn.setOnClickListener(view -> finish()) ;

    }
}
