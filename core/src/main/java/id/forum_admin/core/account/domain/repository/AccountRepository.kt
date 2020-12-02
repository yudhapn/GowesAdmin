package id.forum_admin.core.account.domain.repository

import androidx.lifecycle.LiveData
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.Token
import id.forum_admin.gowes.data.UserData
import id.forum_admin.gowes.user.domain.model.User

interface AccountRepository {
    fun checkIsLoggedIn(): Boolean
    fun updateAccountCache(user: User): User
    fun updateTokenCache(token: Token)
    fun logout(): LiveData<Resource<UserData>>
    suspend fun getUserAccount(email: String, password: String): Resource<User>
}