package com.example.pokewiki.register

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.repository.RegisterRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.example.pokewiki.utils.USER_DATA
import com.example.pokewiki.utils.md5
import com.google.gson.Gson
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * created by DWF on 2022/5/9.
 */
class RegisterViewModel : ViewModel() {

    private val repository = RegisterRepository.getInstance()
    private val _viewState = MutableStateFlow(RegisterViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<RegisterViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: RegisterViewAction) {
        when (viewAction) {
            is RegisterViewAction.UpdateUsername -> updateEmail(viewAction.email)
            is RegisterViewAction.UpdatePassword -> updatePassword(viewAction.password)
            is RegisterViewAction.UpdateConfirm -> updateConfirm(viewAction.confirm)
            is RegisterViewAction.ChangeErrorState -> updateErrorState(viewAction.error)
            is RegisterViewAction.ClickRegister -> register(viewAction.sp)
        }
    }

    private fun updateErrorState(error: Boolean) {
        _viewState.setState { copy(error = error) }
    }

    private fun updateEmail(email: String) {
        _viewState.setState { copy(email = email) }
    }

    private fun updatePassword(password: String) {
        _viewState.setState { copy(password = password) }
    }

    private fun updateConfirm(confirm: String) {
        _viewState.setState { copy(confirm = confirm) }
    }

    private fun register(sp: SharedPreferences) {
        viewModelScope.launch {
            flow {
                registerLogic(sp)
                emit("注册成功")
            }.onStart {
                _viewEvent.setEvent(RegisterViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(RegisterViewEvent.DismissLoadingDialog)
            }.catch {
                _viewEvent.setEvent(
                    RegisterViewEvent.DismissLoadingDialog,
                    RegisterViewEvent.ShowToast(it.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun registerLogic(sp: SharedPreferences) {
        val email = _viewState.value.email
        val password = _viewState.value.password

        when (val result = repository.register(email, md5(password))) {
            is NetworkState.Success -> {
                _viewEvent.setEvent(RegisterViewEvent.TransIntent)

                Log.e("TAG", "registerLogic: ${result.data}")

                //写入全局
                AppContext.userData = result.data
                //写入内存
                sp.edit().putString(USER_DATA, Gson().toJson(result.data)).apply()
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}