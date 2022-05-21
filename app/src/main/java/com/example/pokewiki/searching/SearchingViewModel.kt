package com.example.pokewiki.searching

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.repository.SearchingRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * created by DWF on 2022/5/21.
 */
class SearchingViewModel : ViewModel() {
    private val repository = SearchingRepository.getInstance()
    private val _viewState = MutableStateFlow(SearchingViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<SearchingViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()


    fun dispatch(viewAction: SearchingViewAction) {
        when (viewAction) {
            is SearchingViewAction.UpdateKeyword -> updateKeyword(viewAction.keyword)
            is SearchingViewAction.ClickSearching -> searching("name")
            is SearchingViewAction.ClickSearchingGen -> searching("gen")
            is SearchingViewAction.ClickSearchingType -> searching("type")
        }
    }

    private fun updateKeyword(keyword: String) {
        _viewState.setState { copy(keyword = keyword) }
    }

    private fun searching(type: String) {
        val keyword = _viewState.value.keyword
        viewModelScope.launch {
            flow {
                searchingLogic(keyword, type)
                emit("sb")
            }.onStart {
                _viewEvent.setEvent(SearchingViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(SearchingViewEvent.DismissLoadingDialog)
            }.catch {
                _viewEvent.setEvent(
                        SearchingViewEvent.DismissLoadingDialog,
                        SearchingViewEvent.ShowToast(it.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun searchingLogic(keyword: String, type: String) {
        var result: NetworkState<ArrayList<PokemonSearchBean>>? = null

        when (type) {
            "name" -> result = repository.searchByName(keyword)
            "gen" -> result = repository.searchByGen(keyword)
            "type" -> result = repository.searchByType(keyword)
        }

        when (result) {
            is NetworkState.Success -> {
                _viewEvent.setEvent(SearchingViewEvent.TransIntent)
                AppContext.searchData = result.data
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}