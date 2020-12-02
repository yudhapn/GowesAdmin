package id.forum_admin.request.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum_admin.gowes.base.BaseViewModel
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.data.Status
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.gowes.user.domain.model.User
import id.forum_admin.request.domain.usecase.AcceptCommunityRequestUseCase
import id.forum_admin.request.domain.usecase.GetCommunityCreateRequestUseCase
import id.forum_admin.request.domain.usecase.RejectCommunityRequestUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CommunityRequestViewModel(
    private val getCommunityCreateRequestUseCase: GetCommunityCreateRequestUseCase,
    private val acceptCommunityRequestUseCase: AcceptCommunityRequestUseCase,
    private val rejectCommunityRequestUseCase: RejectCommunityRequestUseCase,
    val currentUser: User

) :
    BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _communityRequest = MutableLiveData<Resource<List<CommunityRequest>>>()
    val communityRequest: LiveData<Resource<List<CommunityRequest>>>
        get() = _communityRequest

    init {
        if (currentUser.id.isNotBlank()) {
            getCommunityRequest()
        }
    }

    fun getCommunityRequest(isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getCommunityCreateRequestUseCase.execute(isRefresh)
                .onStart {
                    _communityRequest.postValue(Resource.loading(_communityRequest.value?.data))
                }
                .catch { cause ->
                    _communityRequest.postValue(
                        Resource.error(cause.message.toString(), _communityRequest.value?.data)
                    )
                }
                .collect {
                    _communityRequest.postValue(Resource.success(it))
                }
        }
    }

    fun acceptRequest(communityRequest: CommunityRequest, admin: User) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _communityRequest.postValue(Resource.loading(_communityRequest.value?.data))
            var communityRequests =
                _communityRequest.value?.data?.toMutableList() ?: mutableListOf()
            communityRequests = removeRequest(communityRequests, communityRequest)

            val requestResource =
                acceptCommunityRequestUseCase.execute(
                    requestId = communityRequest.id,
                    requesterId = communityRequest.user.id,
                    communityId = communityRequest.community.id,
                    adminId = admin.id
                )
            val result = when (requestResource.status) {
                Status.SUCCESS -> Resource.success(communityRequests)
                else -> Resource.error("Something went wrong", communityRequests)
            }
            _communityRequest.postValue(result)
        }
    }

    fun rejectRequest(communityRequest: CommunityRequest, admin: User) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _communityRequest.postValue(Resource.loading(_communityRequest.value?.data))
            var communityRequests =
                _communityRequest.value?.data?.toMutableList() ?: mutableListOf()
            communityRequests = removeRequest(communityRequests, communityRequest)

            val requestResource =
                rejectCommunityRequestUseCase.execute(
                    requestId = communityRequest.id,
                    requesterId = communityRequest.user.id,
                    communityId = communityRequest.community.id,
                    adminId = admin.id
                )
            val result = when (requestResource.status) {
                Status.SUCCESS -> Resource.success(communityRequests)
                else -> Resource.error("Something went wrong", communityRequests)
            }
            _communityRequest.postValue(result)
        }
    }

    private fun removeRequest(
        requestList: MutableList<CommunityRequest>,
        request: CommunityRequest
    ) =
        requestList.let { list ->
            list.find { it.id == request.id }?.let { list.remove(it) }
            list
        }


    fun refreshRequest() {
        getCommunityRequest(true)
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(CommunityRequestViewModel::class.java.simpleName, "An error happened: $e")
    }
}