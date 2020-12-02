package id.forum_admin.login.data.repository

import androidx.lifecycle.MutableLiveData
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.UserData
import id.forum_admin.login.data.service.LoginRealmService
import id.forum_admin.login.domain.repository.LoginRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class LoginRepositoryImpl(
    private val loginRealmService: LoginRealmService
) : LoginRepository {

    override fun loginByEmail(
        email: String,
        password: String
    ): MutableLiveData<Resource<UserData>> {
        return loginRealmService.loginByEmail(email, password)
    }
}
