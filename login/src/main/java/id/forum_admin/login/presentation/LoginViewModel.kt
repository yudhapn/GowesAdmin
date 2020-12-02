package id.forum_admin.login.presentation

import androidx.lifecycle.LiveData
import id.forum_admin.login.domain.usecase.LoginByEmailUseCase
import id.forum_admin.gowes.base.BaseViewModel
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.UserData

class LoginViewModel(private val loginByEmailUseCase: LoginByEmailUseCase) : BaseViewModel() {
    fun loginByEmail(email: String, password: String): LiveData<Resource<UserData>> {
        return loginByEmailUseCase.invoke(email, password)
    }
}
