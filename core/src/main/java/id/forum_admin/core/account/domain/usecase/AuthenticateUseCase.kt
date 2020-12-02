package id.forum_admin.gowes.account.domain.usecase

import androidx.lifecycle.LiveData
import id.forum_admin.core.account.domain.repository.AccountRepository
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.UserData

class AuthenticateUseCase(private val accountRepository: AccountRepository) {
    fun isLoggedIn(): Boolean = accountRepository.checkIsLoggedIn()

    fun logout(): LiveData<Resource<UserData>> = accountRepository.logout()

    suspend fun getUserAccount(email: String, password: String) =
        accountRepository.getUserAccount(email, password)
}