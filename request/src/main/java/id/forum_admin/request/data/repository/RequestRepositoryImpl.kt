package id.forum_admin.request.data.repository

import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.request.data.service.RequestApolloService
import id.forum_admin.request.domain.repository.RequestRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject

class RequestRepositoryImpl : KoinComponent, RequestRepository {
    override fun getCommunityCreateRequest(isRefresh: Boolean): Flow<List<CommunityRequest>> {
        val requestService: RequestApolloService by inject()
        return requestService.getCommunityCreateRequest(isRefresh)
    }

    override suspend fun acceptCommunityRequest(
        requestId: String,
        requesterId: String,
        communityId: String,
        adminId: String
    ): Resource<CommunityRequest> {
        val requestService: RequestApolloService by inject()
        return requestService.updateRequest(
            requestId = requestId,
            requesterId = requesterId,
            communityId = communityId,
            adminId = adminId,
            isAccept = true
        )
    }

    override suspend fun rejectCommunityRequest(
        requestId: String,
        requesterId: String,
        communityId: String,
        adminId: String
    ): Resource<CommunityRequest> {
        val requestService: RequestApolloService by inject()
        return requestService.updateRequest(
            requestId = requestId,
            requesterId = requesterId,
            communityId = communityId,
            adminId = adminId,
            isAccept = false
        )
    }
}