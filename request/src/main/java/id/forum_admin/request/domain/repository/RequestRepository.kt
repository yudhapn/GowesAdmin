package id.forum_admin.request.domain.repository

import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    fun getCommunityCreateRequest(isRefresh: Boolean): Flow<List<CommunityRequest>>
    suspend fun acceptCommunityRequest(
        requestId: String,
        requesterId: String,
        communityId: String,
        adminId: String
    ): Resource<CommunityRequest>

    suspend fun rejectCommunityRequest(
        requestId: String,
        requesterId: String,
        communityId: String,
        adminId: String
    ): Resource<CommunityRequest>
}