package id.forum_admin.gowes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum_admin.gowes.account.domain.usecase.AuthenticateUseCase
import id.forum_admin.gowes.base.BaseViewModel
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.user.domain.model.User

class AuthenticationViewModel(
    private val authenticateUseCase: AuthenticateUseCase,
    private var currentUser: User?

) : BaseViewModel() {

    private val _userAccount = MutableLiveData<Resource<User>>()
    val userAccount: LiveData<Resource<User>>
        get() = _userAccount

    init {
        Log.d("AuthenticationViewModel", "AuthenticationViewModel created")
        when (checkUserHasLoggedIn()) {
            true -> {
                Log.d("AuthenticationViewModel", "has logged in yet")
                _userAccount.postValue(Resource.success(currentUser))
            }
            false -> {
                Log.d("AuthenticationViewModel", "not logged in yet")
                _userAccount.postValue(Resource.success(null))
            }
        }
    }

    private fun checkUserHasLoggedIn(): Boolean {
        Log.d("AuthenticationViewModel", "checkUserHasLoggedIn called")
        return authenticateUseCase.isLoggedIn()
    }
}