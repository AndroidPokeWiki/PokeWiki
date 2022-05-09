package com.example.pokewiki.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.repository.LoginRepository
import com.example.pokewiki.utils.NetworkState
import com.zj.mvi.core.SharedFlowEvents
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository.getInstance()
    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<LoginViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: LoginViewAction) {
        when (viewAction) {
            is LoginViewAction.UpdateUsername -> updateEmail(viewAction.email)
            is LoginViewAction.UpdatePassword -> updatePassword(viewAction.password)
            is LoginViewAction.ChangeErrorState -> updateErrorState(viewAction.error)
            is LoginViewAction.ClickLogin -> login()
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

    private fun login() {
        viewModelScope.launch {
            flow {
                loginLogic()
                emit("登陆成功")
            }.onStart {
                _viewEvent.setEvent(LoginViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewEvent.setEvent(LoginViewEvent.DismissLoadingDialog, LoginViewEvent.TransIntent)
            }.catch {
                _viewEvent.setEvent(
                    LoginViewEvent.DismissLoadingDialog,
                    LoginViewEvent.ShowToast(it.message ?: "")
                )
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun loginLogic() {
        val email = _viewState.value.email
        val password = _viewState.value.password

        when (val result = repository.getAuth(email, password)) {
            is NetworkState.Success -> TODO("获取成功处理")
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}