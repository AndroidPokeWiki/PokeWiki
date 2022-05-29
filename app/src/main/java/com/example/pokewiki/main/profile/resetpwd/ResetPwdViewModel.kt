package com.example.pokewiki.main.profile.resetpwd

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.repository.ResetPwdRepository
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

/**
 * created by DWF on 2022/5/28.
 */
class ResetPwdViewModel : ViewModel() {
    private val repository = ResetPwdRepository.getInstance()
    private val _viewState = MutableStateFlow(ResetPwdViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<ResetPwdViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: ResetPwdViewAction) {
        when (viewAction) {
            is ResetPwdViewAction.UpdateOldPassword -> updateOldPassword(viewAction.oldPassword)
            is ResetPwdViewAction.UpdateNewPassword -> updateNewPassword(viewAction.newPassword)
            is ResetPwdViewAction.ClickResetPWD -> clickResetPWD(viewAction.sp)
        }
    }

    private fun updateOldPassword(oldPassword: String) {
        _viewState.setState { copy(oldPassword = oldPassword) }
    }

    private fun updateNewPassword(newPassword: String) {
        _viewState.setState { copy(newPassword = newPassword) }
    }

    private fun clickResetPWD(sp: SharedPreferences) {
        viewModelScope.launch {
            flow {
                resetLogic(sp)
                emit("修改成功")
            }.onStart {
                _viewEvent.setEvent(ResetPwdViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(ResetPwdViewEvent.DismissLoadingDialog)
            }.catch {
                _viewEvent.setEvent(
                        ResetPwdViewEvent.DismissLoadingDialog,
                        ResetPwdViewEvent.ShowToast(it.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun resetLogic(sp: SharedPreferences) {
        val userID = AppContext.userData.userId
        val token = AppContext.userData.token
        val oldPassword = _viewState.value.oldPassword
        val newPassword = _viewState.value.newPassword
        when (val result: NetworkState<UserBean> = repository.updatePassword(oldPassword, newPassword, userID, token)) {
            is NetworkState.Success -> {
                AppContext.userData = result.data
                sp.edit().putString(USER_DATA, Gson().toJson(result.data)).apply()
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }

}