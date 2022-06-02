package com.example.pokewiki.main.profile.collection

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.PokemonDetailBean
import com.example.pokewiki.main.profile.collection.CollectionViewState.Companion.FAIL
import com.example.pokewiki.main.profile.collection.CollectionViewState.Companion.SUCCESS
import com.example.pokewiki.repository.CollectionRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.example.pokewiki.utils.POKEMON_DETAIL_CACHE
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * created by DWF on 2022/5/29.
 */
class CollectionViewModel : ViewModel() {
    private val repository = CollectionRepository.getInstance()
    private val _viewState = MutableStateFlow(CollectionViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<CollectionViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: CollectionViewAction) {
        when (viewAction) {
            is CollectionViewAction.CancelCollection -> cancelCollection(viewAction.pokemonID, viewAction.sp)
            is CollectionViewAction.GetMyCollection -> getMyCollection()
        }
    }

    private fun cancelCollection(pokemonID: Int, sp: SharedPreferences) {
        viewModelScope.launch {
            flow {
                unlike(pokemonID)
                emit("取消收藏成功")
            }.onStart {
                _viewEvent.setEvent(CollectionViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(CollectionViewEvent.DismissLoadingDialog)
                // 刷新缓存
                if (AppContext.autoSave) {
                    // 读取本地缓存
                    val detailStr = sp.getString(POKEMON_DETAIL_CACHE, null)
                    val detailMap = try {
                        if (detailStr == null) throw JsonParseException("详情JSON为空")
                        Gson().fromJson<HashMap<Int, PokemonDetailBean>>(
                            detailStr,
                            object : TypeToken<HashMap<Int, PokemonDetailBean>>() {}.type
                        )
                    } catch (e: JsonParseException) {
                        Log.e("ParseJson", "getInitData: fail to parse JSON: $e\n JSON: $detailStr")
                        HashMap()
                    }

                    if (detailMap[pokemonID] != null) {
                        detailMap[pokemonID]!!.is_star = 0
                        sp.edit().putString(POKEMON_DETAIL_CACHE, Gson().toJson(detailMap)).apply()
                    }
                }
            }.catch {
                _viewEvent.setEvent(
                    CollectionViewEvent.DismissLoadingDialog,
                    CollectionViewEvent.ShowToast(it.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private fun getMyCollection() {
        viewModelScope.launch {
            delay(100)
            flow {
                getCollections()
                emit("获取成功")
            }.onStart {
                _viewEvent.setEvent(CollectionViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(
                    CollectionViewEvent.DismissLoadingDialog,
                    CollectionViewEvent.NeedChange
                )
                Log.e("TAG", "getMyCollection: ${_viewState.value.data}")
            }.catch {
                _viewEvent.setEvent(
                    CollectionViewEvent.DismissLoadingDialog,
                    CollectionViewEvent.ShowToast(it.message ?: "")
                )
                _viewState.setState { copy(refreshState = FAIL) }
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun getCollections() {
        val userID = AppContext.userData.userId
        when (val data = repository.getMyCollection(userID)) {
            is NetworkState.Success -> {
                _viewState.setState { copy(data = data.data, refreshState = SUCCESS) }
                Log.e("TAG", "getCollections: ${_viewState.value.data}")
            }
            is NetworkState.Error -> throw Exception(data.msg)
        }
    }

    private suspend fun unlike(pokemon_id: Int) {
        val userID = AppContext.userData.userId

        when (val data = repository.unlike(pokemon_id, userID)) {
            is NetworkState.Success -> {
                getCollections()
            }
            is NetworkState.Error -> throw Exception(data.msg)
        }
    }


}