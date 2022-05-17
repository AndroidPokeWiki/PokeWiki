package com.example.pokewiki.detail.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.repository.DetailRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class PokemonDetailViewModel : ViewModel() {
    private val repository = DetailRepository.getInstance()
    private val _viewState = MutableStateFlow(PokemonDetailViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<PokemonDetailViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: PokemonDetailViewAction) {
        when (viewAction) {
            is PokemonDetailViewAction.GetInitData -> getInitData(viewAction.id)
        }
    }

    private fun getInitData(id: Int) {
        viewModelScope.launch {
            flow {
                //延迟确保主线程监听事件
                kotlinx.coroutines.delay(100)

                getInitDataLogic(id)
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
            is NetworkState.Success -> AppContext.pokeDetail = result.data
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}