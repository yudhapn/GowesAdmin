package id.forum_admin.request.data.service

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import id.forum_admin.core.AcceptRequestMutation
import id.forum_admin.core.CommunityRequestsQuery
import id.forum_admin.core.RejectRequestMutation
import id.forum_admin.core.type.RequestAcceptInputMutation
import id.forum_admin.core.type.RequestRejectInputMutation
import id.forum_admin.gowes.data.Resource
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.gowes.util.apolloResponseFetchers
import id.forum_admin.request.data.mapper.mapToDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject

class RequestApolloService : KoinComponent {
    private val apolloClient: ApolloClient by inject()

    fun getCommunityCreateRequest(isRefresh: Boolean): Flow<List<CommunityRequest>> = apolloClient
        .query(CommunityRequestsQuery())
        .responseFetcher(isRefresh.apolloResponseFetchers())
        .watcher()
        .toFlow()
        .map { response ->
            Log.d(
                "RequestApolloService",
                "request size: ${
                    response.data?.communityRequests?.size
                }"
            )
            response.data?.communityRequests?.map {
                it?.fragments?.communityRequestDetails?.mapToDomain() ?: CommunityRequest()
            } ?: emptyList()
        }

    suspend fun updateRequest(
        requestId: String,
        requesterId: String,
        communityId: String,
        adminId: String,
        isAccept: Boolean
    ): Resource<CommunityRequest> {
        try {
            val mutation = if (isAccept) {
                AcceptRequestMutation(
                    RequestAcceptInputMutation(
                        requestId = requestId,
                        requesterId = requesterId,
                        communityId = communityId,
                        adminId = adminId
                    )
                )
            } else {
                RejectRequestMutation(
                    RequestRejectInputMutation(
                        requestId = requestId,
                        requesterId = requesterId,
                        communityId = communityId,
                        adminId = adminId
                    )
                )
            }

            val response = apolloClient
                .mutate(mutation)
                .toDeferred().await()

            if (response.hasErrors()) {
                return Resource.error(
                    response.errors?.get(0)?.message ?: "Something wrong, please try again later",
                    null
                )
            }
            return Resource.success(null)
        } catch (e: ApolloException) {
            throw Exception("PostApolloService apollo, error happened: ${e.message}")
        }
    }
}