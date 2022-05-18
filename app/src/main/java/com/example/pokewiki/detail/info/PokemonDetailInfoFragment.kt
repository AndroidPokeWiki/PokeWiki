package com.example.pokewiki.detail.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.pokewiki.R
import com.example.pokewiki.bean.PokemonIntroBean
import com.example.pokewiki.detail.main.PokemonDetailActivity
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.ColorDict
import com.example.pokewiki.utils.LoadingDialogUtils
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState

class PokemonDetailInfoFragment : Fragment() {
    private val viewModel by viewModels<PokemonDetailInfoViewModel>()
    private lateinit var mEvolutionContainer: LinearLayout
    private lateinit var mClassTv: TextView
    private lateinit var mHabitatTv: TextView
    private lateinit var mIntroTv: TextView
    private lateinit var mShapeTv: TextView
    private lateinit var mLine1: View
    private lateinit var mLine2: View
    private lateinit var mCharContainer: LinearLayout
    private lateinit var loading: LoadingDialogUtils

    private var pokeIntro = AppContext.pokeDetail.poke_intro

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pokemon_detail_info, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
        initViewModel()
        initViewEvent()
    }

    fun refreshData() {
        viewModel.dispatch(PokemonDetailInfoViewAction.InitData)
    }

    private fun initView(view: View) {
        loading = LoadingDialogUtils(requireContext())

        mEvolutionContainer = view.findViewById(R.id.pokemon_detail_evu_container)
        mCharContainer = view.findViewById(R.id.pokemon_detail_character_container)
        mClassTv = view.findViewById(R.id.pokemon_detail_class)
        mHabitatTv = view.findViewById(R.id.pokemon_detail_habitat)
        mIntroTv = view.findViewById(R.id.pokemon_detail_intro)
        mShapeTv = view.findViewById(R.id.pokemon_detail_shape)
        mLine1 = view.findViewById(R.id.pokemon_detail_class_line1)
        mLine2 = view.findViewById(R.id.pokemon_detail_class_line2)
    }

    private fun initViewModel() {
        viewModel.viewState.let { states ->
            states.observeState(this, PokemonDetailInfoViewState::pokemonInfo) {
                if (it != PokemonIntroBean()) {
                    pokeIntro = it
                    initEvo()
                    mClassTv.text = pokeIntro.genus
                    mHabitatTv.text = pokeIntro.habitat
                    if (pokeIntro.intro_text.isNullOrBlank())
                        mIntroTv.visibility = View.GONE
                    else
                        mIntroTv.text = pokeIntro.intro_text?.replace("\n", "")
                    mShapeTv.text = pokeIntro.shape

                    val color =
                        ColorDict.color[AppContext.pokeDetail.pokemon_type[0]]?.let {
                            resources.getColor(it, requireActivity().theme)
                        }
                    if (color != null) {
                        mClassTv.setTextColor(color)
                        mHabitatTv.setTextColor(color)
                        mShapeTv.setTextColor(color)
                        mLine1.setBackgroundColor(color)
                        mLine2.setBackgroundColor(color)
                    }

                    (activity as PokemonDetailActivity).refresh()
                }
            }
        }
    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is PokemonDetailInfoViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(requireContext(), "正在获取...")
                is PokemonDetailInfoViewEvent.DismissLoadingDialog -> loading.dismiss()
                is PokemonDetailInfoViewEvent.ShowToast ->
                    ToastUtils.getInstance(requireContext())?.showLongToast(it.msg)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initEvo() {
        val pokeEvo = pokeIntro.poke_evolution
        if (mEvolutionContainer.size > 0) mEvolutionContainer.removeAllViews()
        for (i in 0 until pokeEvo.size) {
            val view: View =
                LayoutInflater.from(requireContext()).inflate(R.layout.pokemon_evolution_item, null)

            val pokeId = pokeEvo[i].id
            val imgUrl = pokeEvo[i].img_url
            val pokeName = pokeEvo[i].name
            val pokeLevel = pokeEvo[i].min_level

            val nameTv: TextView = view.findViewById(R.id.pokemon_detail_evu_item_name)
            val levelTv: TextView = view.findViewById(R.id.pokemon_detail_evu_item_level)
            val bgSelected: ImageView = view.findViewById(R.id.pokemon_detail_evu_item_bg_selected)
            val bgUnselect: ImageView = view.findViewById(R.id.pokemon_detail_evu_item_bg_unselect)
            val imgIv: ImageView = view.findViewById(R.id.pokemon_detail_evu_item_img)
            val line: View = view.findViewById(R.id.pokemon_detail_evu_item_line)

            nameTv.text = pokeName
            if (pokeLevel != 0)
                levelTv.text = "LV $pokeLevel"
            else
                levelTv.text = "特殊进化"
            Glide.with(requireActivity()).load(imgUrl).into(imgIv)
            if (pokeId != AppContext.pokeDetail.pokemon_id.toInt()) {
                bgSelected.visibility = View.GONE
                bgUnselect.visibility = View.VISIBLE

                // 设置点击事件
                imgIv.setOnClickListener {
                    viewModel.dispatch(PokemonDetailInfoViewAction.ChangeData(pokeId))
                }
            }
            if (i != pokeEvo.size - 1)
                line.visibility = View.VISIBLE

            mEvolutionContainer.addView(view)
        }

        val pokeChar = pokeIntro.general_abilities
        val pokeHideChar = pokeIntro.hidden_abilities
        if (mCharContainer.size > 0) mCharContainer.removeAllViews()
        for (chara in pokeChar) {
            val view: View =
                LayoutInflater.from(requireContext()).inflate(R.layout.character_item, null)

            val charName: TextView = view.findViewById(R.id.pokemon_detail_character_item)
            val line: View = view.findViewById(R.id.pokemon_detail_character_line)

            charName.text = chara
            val color =
                ColorDict.color[AppContext.pokeDetail.pokemon_type[0]]?.let {
                    resources.getColor(it, requireActivity().theme)
                }
            if (color != null) {
                charName.setTextColor(color)
                line.setBackgroundColor(color)
            }

            if (pokeHideChar.size == 0)
                line.visibility = View.GONE

            mCharContainer.addView(view)
        }
        for (hide in pokeHideChar) {
            val view: View =
                LayoutInflater.from(requireContext()).inflate(R.layout.character_item, null)

            val charName: TextView = view.findViewById(R.id.pokemon_detail_character_item)
            val line: View = view.findViewById(R.id.pokemon_detail_character_line)
            val hideHint: TextView = view.findViewById(R.id.pokemon_detail_hide_character_text)

            hideHint.text = resources.getText(R.string.hide_character)
            charName.text = hide
            val color =
                ColorDict.color[AppContext.pokeDetail.pokemon_type[0]]?.let {
                    resources.getColor(it, requireActivity().theme)
                }
            if (color != null) {
                charName.setTextColor(color)
                line.setBackgroundColor(color)
            }
            if (hide == pokeHideChar.last())
                line.visibility = View.GONE

            mCharContainer.addView(view)
        }
    }


}