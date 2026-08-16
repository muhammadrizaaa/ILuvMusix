package org.riza.iluvmusix.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.riza.iluvmusix.dataclass.response.auth.TokenData
import org.riza.iluvmusix.datastore.AuthDataStore
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.network.MainApi
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel: ViewModel() {
    val tokenStatus = MutableStateFlow(ApiStatus.NONE)
    private val _tokenErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val tokenErrMsg = _tokenErrMsg.asStateFlow()

    fun getToken(
        dataStore: AuthDataStore
    ){
        viewModelScope.launch(Dispatchers.IO) {
            tokenStatus.value = ApiStatus.LOADING
            try{
                val result = MainApi.services.getToken()
                if(result.isSuccessful){
                    val token = result.body()
                    if(token != null){
                        dataStore.saveData(
                            TokenData(
                                accessToken = token.accessToken,
                                tokenType = token.tokenType
                            )
                        )
                    }
                    tokenStatus.value = ApiStatus.SUCCESS
                }
                else{
                    _tokenErrMsg.value = "Err"
                    tokenStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("LOGIN_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("LOGIN_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _tokenErrMsg.value = "conn_err"
                    else -> _tokenErrMsg.value = e.message ?: "Unknown error"
                }

                tokenStatus.value = ApiStatus.NONE
            }
        }
    }
}