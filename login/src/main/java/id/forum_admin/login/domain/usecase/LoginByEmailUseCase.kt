package id.forum_admin.login.domain.usecase

import androidx.lifecycle.LiveData
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.UserData
import id.forum_admin.login.domain.repository.LoginRepository

class LoginByEmailUseCase(private val loginRepository: LoginRepository) {
    fun invoke(email: String, password: String): LiveData<Resource<UserData>> =
        loginRepository.loginByEmail(email, password)
}