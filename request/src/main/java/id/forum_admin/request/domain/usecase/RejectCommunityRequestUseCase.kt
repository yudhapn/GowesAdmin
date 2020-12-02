package id.forum_admin.request.domain.usecase

import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.request.domain.repository.RequestRepository

class RejectCommunityRequestUseCase(private val requestRepository: RequestRepository) {
    suspend fun execute(
        requestId: String,
        requesterId: String,
        communityId: String,
        adminId: String
    ): Resource<CommunityRequest> =
        requestRepository.rejectCommunityRequest(requestId, requesterId, communityId, adminId)
}