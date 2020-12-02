package id.forum_admin.login.domain.repository

import androidx.lifecycle.LiveData
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.UserData

interface LoginRepository {
    fun loginByEmail(email: String, password: String): LiveData<Resource<UserData>>
}