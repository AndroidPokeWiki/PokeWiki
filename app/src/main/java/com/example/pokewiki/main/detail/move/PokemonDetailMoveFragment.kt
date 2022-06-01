package com.example.pokewiki.main.detail.move

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R
import com.example.pokewiki.adapter.MoveAdapter
import com.example.pokewiki.bean.PokemonMoveBean
import com.example.pokewiki.utils.AppContext

class PokemonDetailMoveFragment : Fragment() {

    private lateinit var mLevelMoveContainer: RecyclerView

    private val mLevelMoveList = ArrayList<PokemonMoveBean>()
    private var isInit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pokemon_detail_move_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        mLevelMoveContainer = view.findViewById(R.id.pokemon_detail_move_level_container)

        val mLevelAdapter = MoveAdapter(mLevelMoveList, requireContext())
        mLevelMoveContainer.adapter = mLevelAdapter
        mLevelMoveContainer.layoutManager = LinearLayoutManager(requireContext())
        isInit = true

        refreshData()
    }

    fun refreshData() {
        if (isInit) {
            for (item in AppContext.pokeDetail.poke_moves.moves) {
                    mLevelMoveList.add(item)
            }
            (mLevelMoveContainer.adapter as MoveAdapter)
                .notifyItemRangeChanged(0, mLevelMoveList.size)
        }
    }
}