package com.example.pokewiki.main.detail.states

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pokewiki.R
import com.example.pokewiki.utils.AppContext
import com.skydoves.progressview.ProgressView

class PokemonDetailStatesFragment : Fragment() {
    private lateinit var mHPTv: TextView
    private lateinit var mHPBar: ProgressView
    private lateinit var mATKTv: TextView
    private lateinit var mATKBar: ProgressView
    private lateinit var mDEFTv: TextView
    private lateinit var mDEFBar: ProgressView
    private lateinit var mSATKTv: TextView
    private lateinit var mSATKBar: ProgressView
    private lateinit var mSDEFTv: TextView
    private lateinit var mSDEFBar: ProgressView
    private lateinit var mSPDTv: TextView
    private lateinit var mSPDBar: ProgressView

    private var mPokeStates = AppContext.pokeDetail.poke_stat
    private var hasInit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pokemon_detial_state_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        mHPTv = view.findViewById(R.id.pokemon_detail_state_hp_value)
        mATKTv = view.findViewById(R.id.pokemon_detail_state_atk_value)
        mDEFTv = view.findViewById(R.id.pokemon_detail_state_def_value)
        mSATKTv = view.findViewById(R.id.pokemon_detail_state_satk_value)
        mSDEFTv = view.findViewById(R.id.pokemon_detail_state_sdef_value)
        mSPDTv = view.findViewById(R.id.pokemon_detail_state_spd_value)

        mHPBar = view.findViewById(R.id.pokemon_detail_state_hp_bar)
        mATKBar = view.findViewById(R.id.pokemon_detail_state_atk_bar)
        mDEFBar = view.findViewById(R.id.pokemon_detail_state_def_bar)
        mSATKBar = view.findViewById(R.id.pokemon_detail_state_satk_bar)
        mSDEFBar = view.findViewById(R.id.pokemon_detail_state_sdef_bar)
        mSPDBar = view.findViewById(R.id.pokemon_detail_state_spd_bar)
        hasInit = true

        refreshData()
    }

    fun refreshData() {
        if (hasInit) {
            mPokeStates = AppContext.pokeDetail.poke_stat

            mHPTv.text = mPokeStates.HP.toString()
            mHPBar.progress = mPokeStates.HP.toFloat()
            mATKTv.text = mPokeStates.ATK.toString()
            mATKBar.progress = mPokeStates.ATK.toFloat()
            mDEFTv.text = mPokeStates.DEF.toString()
            mDEFBar.progress = mPokeStates.DEF.toFloat()
            mSATKTv.text = mPokeStates.SATK.toString()
            mSATKBar.progress = mPokeStates.SATK.toFloat()
            mSDEFTv.text = mPokeStates.SDEF.toString()
            mSDEFBar.progress = mPokeStates.SDEF.toFloat()
            mSPDTv.text = mPokeStates.SPD.toString()
            mSPDBar.progress = mPokeStates.SPD.toFloat()
        }
    }

    fun clearData() {
        if (hasInit) {
            mHPBar.progress = 0f
            mATKBar.progress = 0f
            mDEFBar.progress = 0f
            mSATKBar.progress = 0f
            mSDEFBar.progress = 0f
            mSPDBar.progress = 0f
        }
    }
}