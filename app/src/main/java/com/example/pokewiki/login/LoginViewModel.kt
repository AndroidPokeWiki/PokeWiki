package com.example.pokewiki.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.repository.LoginRepository
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.NetworkState
import com.example.pokewiki.utils.USER_DATA
import com.example.pokewiki.utils.md5
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

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository.getInstance()
    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEvent = SharedFlowEvents<SearchResultViewEvent>()
    val viewEvent = _viewEvent.asSharedFlow()

    fun dispatch(viewAction: LoginViewAction) {
        when (viewAction) {
            is LoginViewAction.CheckLoginInfo -> checkLoginInfo(viewAction.sp)
            is LoginViewAction.UpdateUsername -> updateEmail(viewAction.email)
            is LoginViewAction.UpdatePassword -> updatePassword(viewAction.password)
            is LoginViewAction.ChangeErrorState -> updateErrorState(viewAction.error)
            is LoginViewAction.ClickLogin -> login(viewAction.sp)
        }
    }

    private fun checkLoginInfo(sp: SharedPreferences) {
        val data = sp.getString(USER_DATA, null)
        if (!data.isNullOrBlank()) {
            try {
                val userInfo =
                    Gson().fromJson<UserBean>(data, object : TypeToken<UserBean>() {}.type)
                AppContext.userData = userInfo
                // 自动跳转
                viewModelScope.launch {
                    // 等待主线程监听启动
                    delay(100)
                    _viewEvent.setEvent(SearchResultViewEvent.TransIntent)
                }
            } catch (e: JsonParseException) {
                Log.e("ERROR!", "checkLoginInfo: 无法解析存储json\n json:${data}")
            }
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

    private fun login(sp: SharedPreferences) {
        viewModelScope.launch {
            flow {
                loginLogic(sp)
                emit("登陆成功")
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

    private suspend fun loginLogic(sp: SharedPreferences) {
        val email = _viewState.value.email
        val password = _viewState.value.password

        when (val result = repository.getAuth(email, md5(password))) {
            is NetworkState.Success -> {
                _viewEvent.setEvent(SearchResultViewEvent.TransIntent)

                //写入全局
                AppContext.userData = result.data
                //写入内存
                sp.edit().putString(USER_DATA, Gson().toJson(result.data)).apply()
            }
            is NetworkState.Error -> throw Exception(result.msg)
        }
    }
}