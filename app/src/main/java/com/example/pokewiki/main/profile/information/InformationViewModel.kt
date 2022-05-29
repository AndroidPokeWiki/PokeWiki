package com.example.pokewiki.main.profile.information

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.repository.InformationRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.example.pokewiki.utils.USER_DATA
import com.google.gson.Gson
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

/**
 * created by DWF on 2022/5/28.
 */
class InformationViewModel : ViewModel() {
    private val repository = InformationRepository.getInstance()

    private val _viewState = MutableStateFlow(InformationViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<InformationViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: InformationViewAction) {
        when (viewAction) {
            is InformationViewAction.InitData -> initData()
            is InformationViewAction.ChangeIcon -> changeIcon(viewAction.file)
            is InformationViewAction.UpdateUsername -> updateUsername(viewAction.name)
            is InformationViewAction.ClickToChangeUsername -> clickToChangeUsername(viewAction.sp)
            is InformationViewAction.ChangeButtonToConfirm -> changeButtonToConfirm()
        }
    }

    private fun initData() {
        _viewState.setState { copy(name = AppContext.userData.username) }
    }

    private fun changeIcon(file: File) {
        TODO("更改头像")
    }

    private fun updateUsername(name: String) {
        _viewState.setState { copy(name = name) }
    }

    private fun clickToChangeUsername(sp: SharedPreferences) {
        viewModelScope.launch {
            flow {
                changeNameLogic(sp)
                emit("修改成功")
            }.onStart {
                _viewEvent.setEvent(InformationViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(InformationViewEvent.DismissLoadingDialog)
            }.catch {
                _viewEvent.setEvent(
                    InformationViewEvent.DismissLoadingDialog,
                    InformationViewEvent.ShowToast(it.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }


    private fun changeButtonToConfirm() {
        TODO("将按钮更改为确定")
    }

    private suspend fun changeNameLogic(sp: SharedPreferences) {
        val userID = AppContext.userData.userId
        val token = AppContext.userData.token
        val username = _viewState.value.name
        when (val result: NetworkState<UserBean> =
            repository.updateUsername(username, userID, token)) {
            is NetworkState.Success -> {
                AppContext.userData = result.data
                sp.edit().putString(USER_DATA, Gson().toJson(result.data)).apply()
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }

}