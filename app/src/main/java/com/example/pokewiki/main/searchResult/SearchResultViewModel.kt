package com.example.pokewiki.main.searchResult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.repository.SearchResultRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * created by DWF on 2022/5/25.
 */
class SearchResultViewModel : ViewModel() {

    private val repository = SearchResultRepository.getInstance()
    private val _viewState = MutableStateFlow(SearchResultViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<SearchResultViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: SearchResultViewAction) {
        when (viewAction) {
            is SearchResultViewAction.UpdateKeyword -> updateKeyword(viewAction.keyword)
            is SearchResultViewAction.ClickSearching -> searching("name")
            is SearchResultViewAction.ClickSearchingGen -> searching("gen")
            is SearchResultViewAction.ClickSearchingType -> searching("type")
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
                emit("搜索成功")
            }.onStart {
                _viewEvent.setEvent(SearchResultViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(SearchResultViewEvent.DismissLoadingDialog)
            }.catch {
                _viewEvent.setEvent(
                    SearchResultViewEvent.DismissLoadingDialog,
                    SearchResultViewEvent.ShowToast(it.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun searchingLogic(keyword: String, type: String) {
        val result: NetworkState<ArrayList<PokemonSearchBean>>? = when (type) {
            "name" -> repository.searchByName(keyword)
            "gen" -> repository.searchByGen(keyword)
            "type" -> repository.searchByType(keyword)
            else -> null
        }

        when (result) {
            is NetworkState.Success -> {
                AppContext.searchData = result.data
                _viewState.setState { copy(result = result.data) }
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}