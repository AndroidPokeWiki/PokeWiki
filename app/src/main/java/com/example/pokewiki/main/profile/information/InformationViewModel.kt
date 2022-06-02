package com.example.pokewiki.main.profile.information

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.main.profile.information.InformationViewState.Companion.NORMAL
import com.example.pokewiki.main.profile.information.InformationViewState.Companion.SUCCESS
import com.example.pokewiki.repository.InformationRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.example.pokewiki.utils.TOKEN_OUT_OF_DATE
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
            is InformationViewAction.ChangeIcon -> changeIcon(viewAction.file, viewAction.sp)
            is InformationViewAction.UpdateUsername -> updateUsername(viewAction.name)
            is InformationViewAction.ClickToChangeUsername -> clickToChangeUsername(viewAction.sp)
            is InformationViewAction.SwitchState -> switchState()
        }
    }

    private fun switchState() {
        _viewState.setState { copy(changeState = !changeState) }
    }

    private fun initData() {
        _viewState.setState {
            copy(
                name = AppContext.userData.username,
                icon = AppContext.userData.profile_photo
            )
        }
    }

    private fun changeIcon(file: File, sp: SharedPreferences) {
        viewModelScope.launch {
            flow {
                changeIconLogic(file, sp)
                emit("修改成功")
            }.onStart {
                _viewState.setState { copy(state = NORMAL) }
                _viewEvent.setEvent(InformationViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(InformationViewEvent.DismissLoadingDialog)
                _viewState.setState { copy(state = SUCCESS) }
            }.catch { e ->
                _viewEvent.setEvent(
                    InformationViewEvent.DismissLoadingDialog,
                    InformationViewEvent.ShowToast(e.message ?: "未知错误,请联系管理员")
                )
                _viewState.setState { copy(state = InformationViewState.FAIL) }
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun changeIconLogic(file: File, sp: SharedPreferences) {
        val userID = AppContext.userData.userId

        when (val result = repository.updateUserIcon(userID, file)) {
            is NetworkState.Success -> {
                AppContext.userData.profile_photo = result.data
                _viewState.setState { copy(icon = AppContext.userData.profile_photo) }
                sp.edit().putString(USER_DATA, Gson().toJson(AppContext.userData)).apply()
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
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
                _viewState.setState { copy(state = NORMAL) }
                _viewEvent.setEvent(InformationViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewState.setState { copy(state = SUCCESS) }
                _viewEvent.setEvent(InformationViewEvent.DismissLoadingDialog)
            }.catch {
                if (it.message == TOKEN_OUT_OF_DATE) {
                    _viewEvent.setEvent(
                        InformationViewEvent.DismissLoadingDialog
                    )
                    _viewState.setState { copy(state = InformationViewState.TOKEN_OUT_OF_DATE) }
                } else {
                    _viewEvent.setEvent(
                        InformationViewEvent.DismissLoadingDialog,
                        InformationViewEvent.ShowToast(it.message ?: "")
                    )
                    _viewState.setState { copy(state = InformationViewState.FAIL) }
                }
            }.flowOn(Dispatchers.IO).collect()
        }
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