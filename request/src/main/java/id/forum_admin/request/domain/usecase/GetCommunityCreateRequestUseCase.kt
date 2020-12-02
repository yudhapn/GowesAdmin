package id.forum_admin.request.domain.usecase

import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.request.domain.repository.RequestRepository
import kotlinx.coroutines.flow.Flow

class GetCommunityCreateRequestUseCase(private val requestRepository: RequestRepository) {
    fun execute(isRefresh: Boolean): Flow<List<CommunityRequest>> =
        requestRepository.getCommunityCreateRequest(isRefresh)
}