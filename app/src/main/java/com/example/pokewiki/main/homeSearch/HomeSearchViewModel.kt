package com.example.pokewiki.main.homeSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.repository.HomeSearchRepository
import com.example.pokewiki.utils.NO_MORE_DATA
import com.example.pokewiki.utils.NetworkState
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeSearchViewModel : ViewModel() {
    private val repository = HomeSearchRepository.getInstance()
    private val _viewState = MutableStateFlow(HomeSearchViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<HomeSearchViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: HomeSearchViewAction) {
        when (viewAction) {
            is HomeSearchViewAction.InitPage, HomeSearchViewAction.LoadingNextPage -> loadingPage()
            is HomeSearchViewAction.RefreshPage -> refreshPage()
        }
    }

    private fun loadingPage() {
        val page = _viewState.value.page
        viewModelScope.launch {
            flow {
                getDataWithPageAndState(page, true)
                emit("初始化成功")
            }.onStart {
                _viewState.setState { copy(loadingState = HomeSearchViewState.LOADING) }
                _viewEvent.setEvent(HomeSearchViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewState.setState { copy(loadingState = HomeSearchViewState.FINISH) }
                _viewEvent.setEvent(HomeSearchViewEvent.DismissLoadingDialog)
            }.catch { e ->
                when (e.message) {
                    NO_MORE_DATA ->
                        _viewState.setState { copy(loadingState = HomeSearchViewState.NO_MORE) }
                    else ->
                        _viewState.setState { copy(loadingState = HomeSearchViewState.ERROR) }
                }
                _viewEvent.setEvent(HomeSearchViewEvent.DismissLoadingDialog)
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private fun refreshPage() {
        val page = _viewState.value.page
        viewModelScope.launch {
            flow {
                getDataWithPageAndState(page, false)
                emit("初始化成功")
            }.onStart {
                _viewState.setState { copy(refreshState = HomeSearchViewState.LOADING) }
                _viewEvent.setEvent(HomeSearchViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewState.setState { copy(refreshState = HomeSearchViewState.FINISH) }
                _viewEvent.setEvent(HomeSearchViewEvent.DismissLoadingDialog)
            }.catch { e ->
                when (e.message) {
                    NO_MORE_DATA ->
                        _viewState.setState { copy(refreshState = HomeSearchViewState.NO_MORE) }
                    else ->
                        _viewState.setState { copy(refreshState = HomeSearchViewState.ERROR) }
                }
                _viewEvent.setEvent(HomeSearchViewEvent.DismissLoadingDialog)
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun getDataWithPageAndState(page: Int, appendMode: Boolean) {
        val pokemonList = _viewState.value.pokemonList

        if (appendMode) {
            when (val result = repository.getAllWithPage(page + 1)) {
                is NetworkState.Success -> {
                    pokemonList.addAll(result.data)
                    _viewState.setState { copy(pokemonList = pokemonList, page = page + 1) }
                }
                is NetworkState.Error -> throw Exception(result.msg)
            }
        } else {
            when (val result = repository.getAllWithPage(1)) {
                is NetworkState.Success -> {
                    pokemonList.clear()
                    pokemonList.addAll(result.data)
                    _viewState.setState { copy(pokemonList = pokemonList, page = 1) }
                }
                is NetworkState.Error -> throw Exception(result.msg)
            }
        }
    }
}