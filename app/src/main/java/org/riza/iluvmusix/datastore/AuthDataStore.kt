package org.riza.iluvmusix.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.riza.iluvmusix.dataclass.response.auth.TokenData

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_auth_preference"
)

class AuthDataStore (private val context: Context) {
    companion object{
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val TOKEN_TYPE = stringPreferencesKey("token_type")
    }
    val userIdFlow: Flow<TokenData> = context.dataStore.data.map {
        TokenData(
            accessToken = it[ACCESS_TOKEN] ?: "",
            tokenType = it[TOKEN_TYPE] ?: "",
        )
    }
    suspend fun saveData(tokenData: TokenData){
        context.dataStore.edit {
            it[ACCESS_TOKEN] = tokenData.accessToken
            it[TOKEN_TYPE] = tokenData.tokenType
        }
    }

    suspend fun clearSession(){
        context.dataStore.edit {
            it.clear()
        }
    }
}