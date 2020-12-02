package id.forum_admin.login.data.service

import androidx.lifecycle.MutableLiveData
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.UserData
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LoginRealmService(private val realmAppClient: App) {
    fun loginByEmail(email: String, password: String): MutableLiveData<Resource<UserData>> {
        val result = MutableLiveData<Resource<UserData>>()
        result.postValue(Resource.loading(null))
        realmAppClient.loginAsync(Credentials.emailPassword(email, password)) {
            when {
                it.isSuccess -> result.postValue(Resource.success(UserData()))
                else -> result.postValue(Resource.error(it.error.toString(), null))
            }
        }
        return result
    }
}