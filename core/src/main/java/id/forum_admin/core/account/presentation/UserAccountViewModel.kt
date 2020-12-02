package id.forum_admin.core.account.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum_admin.gowes.account.domain.usecase.AuthenticateUseCase
import id.forum_admin.gowes.account.domain.usecase.UpdateAccountCacheUseCase
import id.forum_admin.gowes.base.BaseViewModel
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.Status
import id.forum_admin.gowes.data.Status.*
import id.forum_admin.gowes.data.UserData
import id.forum_admin.gowes.user.domain.model.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserAccountViewModel(
    private val authenticateUseCase: AuthenticateUseCase,
    private val updateAccountCacheUseCase: UpdateAccountCacheUseCase,
    private var currentUser: User?

) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private var email: String = ""
    private var password: String = ""

    private val _userAccount = MutableLiveData<Resource<User>>()
    val userAccount: LiveData<Resource<User>>
        get() = _userAccount


    private val _messageSnackBar = MutableLiveData<String>()
    val messageSnackBar: LiveData<String>
        get() = _messageSnackBar


    init {
        val hasLoggedIn = checkUserHasLoggedIn()
        when {
            hasLoggedIn -> setCurrentUser()
            else -> _userAccount.postValue(Resource.success(null))
        }
    }

    private fun checkUserHasLoggedIn() = authenticateUseCase.isLoggedIn()

    fun getCurrentUser() = currentUser

    private fun setCurrentUser() {
        _userAccount.postValue(Resource.loading(currentUser))
        if (currentUser?.id.toString().isNotBlank()) {
            Log.d("UserAccountViewModel", "currentUser is not blank")
            _userAccount.postValue(Resource.success(currentUser))
        } else {
            if (email.isNotBlank()) {
                ioScope.launch(getJobErrorHandler() + supervisorJob) {
                    val userAccount = authenticateUseCase.getUserAccount(email, password)
                    currentUser = userAccount.data ?: currentUser
                    _userAccount.postValue(Resource.success(currentUser))
                    Log.d("UserAccountViewModel", "user account response data: ${userAccount.data}")
                    Log.d("UserAccountViewModel", "current user is ${_userAccount.value?.data}")
                }
            }
        }
    }

    fun setCurrentState(state: Status) {
        when (state) {
            LOADING -> _userAccount.postValue(Resource.loading(currentUser))
            SUCCESS -> _userAccount.postValue(Resource.success(currentUser))
            ERROR -> _userAccount.postValue(Resource.error("Something Went Wrong", currentUser))
        }
    }

    fun setUserInformationLogin(email: String, password: String) {
        // has logged in
        this.email = email
        this.password = password
        setCurrentUser()
    }

    fun logout(): LiveData<Resource<UserData>> {
        _userAccount.postValue(Resource.loading(currentUser))
        val userData = authenticateUseCase.logout()
        currentUser = User()
        currentUser?.let {
            updateAccountCacheUseCase.execute(it)
        }
        _userAccount.value = Resource.success(null)
        return userData
    }

    fun showSnackBar(message: String) {
        _messageSnackBar.postValue(message)
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(UserAccountViewModel::class.java.simpleName, "An error happened: $e")
    }

}