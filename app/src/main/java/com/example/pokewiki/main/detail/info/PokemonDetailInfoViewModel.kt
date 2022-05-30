package com.example.pokewiki.main.detail.info

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.PokemonDetailBean
import com.example.pokewiki.main.detail.main.PokemonDetailViewEvent
import com.example.pokewiki.repository.DetailRepository
import com.example.pokewiki.utils.*
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
import okio.Okio
import java.io.File

class PokemonDetailInfoViewModel : ViewModel() {
    private val repository = DetailRepository.getInstance()
    private val _viewState = MutableStateFlow(PokemonDetailInfoViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<PokemonDetailInfoViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: PokemonDetailInfoViewAction) {
        when (viewAction) {
            is PokemonDetailInfoViewAction.ChangeData -> changeData(viewAction.id, viewAction.sp)
            is PokemonDetailInfoViewAction.InitData -> initData()
            is PokemonDetailInfoViewAction.WriteDataIntoStorage -> writeData(
                viewAction.smallPath,
                viewAction.bigPath,
                viewAction.sp
            )
        }
    }

    private fun initData() {
        _viewState.setState { copy(pokemonInfo = AppContext.pokeDetail.poke_intro) }
    }

    private fun changeData(id: Int, sp: SharedPreferences) {
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

        if (AppContext.autoSave && detailMap[id] != null) {
            AppContext.pokeDetail = detailMap[id]!!
            _viewState.setState { copy(pokemonInfo = AppContext.pokeDetail.poke_intro) }
        } else
            viewModelScope.launch {
                //延迟确保主线程监听事件
                delay(100)

                flow {
                    changeDataLogic(id)
                    emit("获取成功")
                }.onStart {
                    _viewEvent.setEvent(PokemonDetailInfoViewEvent.ShowLoadingDialog)
                }.onEach {
                    // 如果开启缓存则通知UI层启动缓存
                    if (AppContext.autoSave)
                        _viewEvent.setEvent(PokemonDetailInfoViewEvent.WriteDataIntoStorage)

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

    private fun writeData(smallPath: String, bigPath: String, sp: SharedPreferences) {
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
        // 读取本地大图map
        val bigImgStr = sp.getString(POKEMON_BIG_PIC, null)
        val bigImgMap = try {
            if (bigImgStr == null) throw JsonParseException("大图JSON为空")
            Gson().fromJson<HashMap<Int, String>>(
                bigImgStr,
                object : TypeToken<HashMap<Int, String>>() {}.type
            )
        } catch (e: JsonParseException) {
            Log.e("ParseJson", "getInitData: fail to parse JSON: $e\n JSON: $bigImgStr")
            HashMap()
        }
        // 读取本地小图map
        val smallImgStr = sp.getString(POKEMON_SMALL_PIC, null)
        val smallImgMap = try {
            if (smallImgStr == null) throw JsonParseException("小图JSON为空")
            Gson().fromJson<HashMap<Int, String>>(
                smallImgStr,
                object : TypeToken<HashMap<Int, String>>() {}.type
            )
        } catch (e: JsonParseException) {
            Log.e("ParseJson", "getInitData: fail to parse JSON: $e\n JSON: $smallImgStr")
            HashMap()
        }

        // 下载图片
        viewModelScope.launch {
            flow {
                // 下载大图
                val bigFileName = "${AppContext.pokeDetail.pokemon_id}.png"
                val bigDest = File(bigPath, bigFileName)
                flow {

                    writeDataLogic(DetailRepository.Companion.Type.Big, bigDest)
                    emit("缓存成功")
                }.onEach {
                    // 存入大图缓存
                    bigImgMap[AppContext.pokeDetail.pokemon_id.toInt()] = bigDest.path
                    // 写入本地
                    sp.edit().putString(POKEMON_BIG_PIC, Gson().toJson(bigImgMap)).apply()
                }.catch { e ->
                    _viewEvent.setEvent(PokemonDetailInfoViewEvent.ShowToast(e.message ?: ""))
                }.flowOn(Dispatchers.IO).collect()

                // 判断小图是否下载
                for (item in AppContext.pokeDetail.poke_intro.poke_evolution){
                    if (smallImgMap[item.id] == null){
                        val smallFileName = "${item.id}.png"
                        val smallDest = File(smallPath, smallFileName)
                        // 下载小图
                        flow {
                            writeDataLogic(DetailRepository.Companion.Type.Small, smallDest, item.id)
                            emit("缓存成功")
                        }.onEach {
                            // 存入小图图缓存
                            smallImgMap[item.id] = smallDest.path
                            // 写入本地
                            sp.edit().putString(POKEMON_BIG_PIC, Gson().toJson(smallImgMap)).apply()
                        }.catch { e ->
                            _viewEvent.setEvent(PokemonDetailInfoViewEvent.ShowToast(e.message ?: ""))
                        }.flowOn(Dispatchers.IO).collect()
                    }
                }

                emit("图片缓存完成")
            }.onEach {
                // 写入内存
                detailMap[AppContext.pokeDetail.pokemon_id.toInt()] = AppContext.pokeDetail
                sp.edit().putString(POKEMON_DETAIL_CACHE, Gson().toJson(detailMap)).apply()
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun writeDataLogic(type: DetailRepository.Companion.Type, dest: File, id: Int? = null) {
        when (val result = when (type) {
            DetailRepository.Companion.Type.Big -> repository.getImageWithTypeAndID(
                DetailRepository.Companion.Type.Big,
                AppContext.pokeDetail.pokemon_id.toInt()
            )
            DetailRepository.Companion.Type.Small -> repository.getImageWithTypeAndID(
                DetailRepository.Companion.Type.Small,
                id!!.toInt()
            )
        }) {
            is NetworkState.Success -> {
                flow {
                    val sink = Okio.sink(dest)
                    val bufferedSink = Okio.buffer(sink)
                    bufferedSink.writeAll(result.data.source())
                    bufferedSink.close()

                    emit("下载完成")
                }.flowOn(Dispatchers.IO).collect()
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}