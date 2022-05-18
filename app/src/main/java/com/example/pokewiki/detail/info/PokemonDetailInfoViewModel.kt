package com.example.pokewiki.detail.info

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

class PokemonDetailInfoViewModel : ViewModel() {
    private val repository = DetailRepository.getInstance()
    private val _viewState = MutableStateFlow(PokemonDetailInfoViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<PokemonDetailInfoViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: PokemonDetailInfoViewAction) {
        when (viewAction) {
            is PokemonDetailInfoViewAction.ChangeData<*> -> changeData(viewAction.id)
            is PokemonDetailInfoViewAction.InitData -> initData()
        }
    }

    private fun initData() {
        _viewState.setState { copy(pokemonInfo = AppContext.pokeDetail.poke_intro) }
    }

    private fun <T> changeData(id: T) {
        viewModelScope.launch {
            //延迟确保主线程监听事件
            delay(100)

            flow {
                when (id) {
                    is Int -> changeDataLogic(id)
                    is String -> changeDataLogic(id.toInt())
                    else -> throw Exception("传入宝可梦id错误, 请联系管理员")
                }
                emit("获取成功")
            }.onStart {
                _viewEvent.setEvent(PokemonDetailInfoViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(PokemonDetailInfoViewEvent.DismissLoadingDialog)
            }.catch { e ->
                _viewEvent.setEvent(
                    PokemonDetailInfoViewEvent.DismissLoadingDialog,
                    PokemonDetailInfoViewEvent.ShowToast(e.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun changeDataLogic(id: Int) {
        val userId = AppContext.userData.userId
        when (val result = repository.getInitData(id, userId)) {
            is NetworkState.Success -> {
                AppContext.pokeDetail = result.data
                _viewState.setState {
                    copy(pokemonInfo = result.data.poke_intro)
                }
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}