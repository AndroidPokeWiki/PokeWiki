package com.example.pokewiki.detail.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.repository.DetailRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokemonDetailViewModel : ViewModel() {
    private val repository = DetailRepository.getInstance()
    private val _viewState = MutableStateFlow(PokemonDetailViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<PokemonDetailViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: PokemonDetailViewAction) {
        when (viewAction) {
            is PokemonDetailViewAction.GetInitData<*> -> getInitData(viewAction.id)
            is PokemonDetailViewAction.RefreshData -> refreshData()
        }
    }

    private fun refreshData(){
        _viewState.setState {
            copy(
                id = "#${AppContext.pokeDetail.pokemon_id}",
                img = AppContext.pokeDetail.img_url,
                name = AppContext.pokeDetail.pokemon_name,
                color = AppContext.pokeDetail.pokemon_color,
                attrs =AppContext.pokeDetail.pokemon_type,
                is_like = AppContext.pokeDetail.is_star != 0
            )
        }
    }

    private fun <T> getInitData(id: T) {
        viewModelScope.launch {
            //延迟确保主线程监听事件
            delay(100)

            flow {
                when (id) {
                    is Int -> getInitDataLogic(id)
                    is String -> getInitDataLogic(id.toInt())
                    else -> throw Exception("传入宝可梦id错误, 请联系管理员")
                }
                emit("获取成功")
            }.onStart {
                _viewEvent.setEvent(PokemonDetailViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(PokemonDetailViewEvent.DismissLoadingDialog)
            }.catch { e ->
                _viewEvent.setEvent(
                    PokemonDetailViewEvent.DismissLoadingDialog,
                    PokemonDetailViewEvent.ShowToast(e.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun getInitDataLogic(id: Int) {
        val userId = AppContext.userData.userId
        when (val result = repository.getInitData(id, userId)) {
            is NetworkState.Success -> {
                AppContext.pokeDetail = result.data
                _viewState.setState {
                    copy(
                        id = "#${result.data.pokemon_id}",
                        img = result.data.img_url,
                        name = result.data.pokemon_name,
                        color = result.data.pokemon_color,
                        attrs = result.data.pokemon_type,
                        is_like = result.data.is_star != 0
                    )
                }
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}