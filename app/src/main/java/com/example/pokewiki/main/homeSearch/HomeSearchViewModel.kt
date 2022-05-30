package com.example.pokewiki.main.homeSearch

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.repository.HomeSearchRepository
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

class HomeSearchViewModel : ViewModel() {
    private val repository = HomeSearchRepository.getInstance()
    private val _viewState = MutableStateFlow(HomeSearchViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<HomeSearchViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    private val pageData: ArrayList<PokemonSearchBean> = ArrayList()
    private val changeData: ArrayList<PokemonSearchBean> = ArrayList()

    fun dispatch(viewAction: HomeSearchViewAction) {
        when (viewAction) {
            is HomeSearchViewAction.LoadingNextPage -> loadingPage(viewAction.sp)
            is HomeSearchViewAction.RefreshPage -> refreshPage(viewAction.sp)
            is HomeSearchViewAction.GetCacheData -> getCache(viewAction.sp)
            is HomeSearchViewAction.WriteToStorage -> writeToStorage(viewAction.path, viewAction.sp)
        }
    }

    private fun getCache(sp: SharedPreferences) {
        AppContext.autoSave = sp.getBoolean(AUTO_SAVE, false)
        // 判断自动存储
        if (AppContext.autoSave) {
            // 获取缓存
            val listData = sp.getString(POKEMON_HOME_CACHE, null)
            val page = sp.getInt(POKEMON_CACHE_PAGE, 0)
            // 缓存不为空
            if (!listData.isNullOrBlank()) {
                viewModelScope.launch {
                    // 延迟确保主线程监听启动
                    delay(100)
                    // 解析json
                    try {
                        val list = Gson().fromJson<ArrayList<PokemonSearchBean>>(
                            listData, object : TypeToken<ArrayList<PokemonSearchBean>>() {}.type
                        )
                        // 列表为空
                        if (list.isEmpty())
                            loadingPage(sp)
                        else {
                            //清除缓存避免重复
                            changeData.clear()

                            changeData.addAll(list)
                            _viewState.setState { copy(pokemonList = list, page = page) }
                        }
                    } catch (e: JsonParseException) {
                        Log.e("ERROR!", "getCache: 无法解析存储json\n json:${listData}")
                        // 加载两页数据避免数组越界报错
                        loadingPage(sp)
                    }
                }
            } else
                loadingPage(sp)
        } else
            loadingPage(sp)
    }

    private fun loadingPage(sp: SharedPreferences) {
        val page = _viewState.value.page
        viewModelScope.launch {
            flow {
                getDataWithPageAndState(page, true, sp)
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

    private fun refreshPage(sp: SharedPreferences) {
        val page = _viewState.value.page
        viewModelScope.launch {
            flow {
                getDataWithPageAndState(page, false, sp)
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

    private suspend fun getDataWithPageAndState(
        page: Int,
        appendMode: Boolean,
        sp: SharedPreferences
    ) {
        val pokemonList = _viewState.value.pokemonList
        // 加载
        if (appendMode) {
            when (val result = repository.getAllWithPage(page + 1)) {
                is NetworkState.Success -> {
                    // 存储最新页面数据
                    pageData.clear()
                    pageData.addAll(result.data)
                    // 如果启动自动缓存
                    if (AppContext.autoSave) {
                        _viewEvent.setEvent(HomeSearchViewEvent.WriteToStorage)
                    }
                    // 将最新的页面数据添加
                    pokemonList.addAll(pageData)
                    _viewState.setState { copy(pokemonList = pokemonList, page = page + 1) }
                }
                is NetworkState.Error -> throw Exception(result.msg)
            }
        } else {
            // 刷新
            // 如果启动自动缓存
            if (AppContext.autoSave) {
                val cacheStr = sp.getString(POKEMON_HOME_CACHE, null)
                // 读取本地
                try {
                    val cacheList = Gson().fromJson<ArrayList<PokemonSearchBean>>(
                        cacheStr, object : TypeToken<ArrayList<PokemonSearchBean>>() {}.type
                    )
                    // 读取成功
                    if (!cacheList.isNullOrEmpty()) {
                        pokemonList.clear()
                        pokemonList.addAll(cacheList)
                    } else
                    // 否则读取第一页
                        getFirstData(pokemonList)
                } catch (e: JsonParseException) {
                    Log.e("ERROR!", "getDataWithPageAndState: 无法解析存储json\n json:${cacheStr}")
                    getFirstData(pokemonList)
                }
            } else
            // 否则读取第一页
                getFirstData(pokemonList)
        }
    }

    private suspend fun getFirstData(pokemonList: ArrayList<PokemonSearchBean>) {
        when (val result = repository.getAllWithPage(1)) {
            is NetworkState.Success -> {
                pokemonList.clear()
                pokemonList.addAll(result.data)
                _viewState.setState { copy(pokemonList = pokemonList, page = 1) }
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }

    // 写入内存 建立小图map
    private fun writeToStorage(path: String, sp: SharedPreferences) {
        val page = _viewState.value.page
        viewModelScope.launch {
            // 获取缩略图map
            val smallStr = sp.getString(POKEMON_SMALL_PIC, null)
            val smallMap: HashMap<Int, String> = try {
                if (smallStr == null) throw JsonParseException("小图JSON为空")
                Gson().fromJson(smallStr, object : TypeToken<HashMap<Int, String>>() {}.type)
            } catch (e: JsonParseException) {
                Log.e("ParseJson", "无法解析JSON: ${e.message}\n JSON: $smallStr")
                HashMap()
            }

            flow {
                for (item in pageData) {
                    val fileName = "${item.pokemon_id.toInt()}.png"
                    val dest = File(path, fileName)
                    writeToStorageLogic(dest, item)

                    //记录缩略图位置
                    smallMap[item.pokemon_id.toInt()] = dest.path
                }
                emit("下载完成")
            }.onEach {
                // 写入内存
                sp.edit()
                    .putString(POKEMON_HOME_CACHE, Gson().toJson(changeData))
                    .putInt(POKEMON_CACHE_PAGE, page)
                    .putString(POKEMON_SMALL_PIC, Gson().toJson(smallMap))
                    .apply()
            }.catch { e ->
                e.printStackTrace()
                Log.e("DOWNLOAD", "download failed")
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun writeToStorageLogic(
        dest: File,
        item: PokemonSearchBean
    ) {
        // 下载缩略图
        when (val result =
            repository.getImageWithTypeAndID(
                HomeSearchRepository.Companion.Type.Small,
                item.pokemon_id.toInt()
            )) {
            is NetworkState.Success -> {
                flow {
                    val sink = Okio.sink(dest)
                    val bufferedSink = Okio.buffer(sink)
                    bufferedSink.writeAll(result.data.source())
                    bufferedSink.close()

                    //切换储存方式
                    changeData.add(item.copy(img_path = dest.path))
                    emit("下载完成")
                }.flowOn(Dispatchers.IO).collect()
            }
            is NetworkState.Error -> throw(Exception(result.msg))
        }
    }
}