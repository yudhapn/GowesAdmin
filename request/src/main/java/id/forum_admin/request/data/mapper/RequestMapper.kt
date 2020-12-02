package id.forum_admin.request.data.mapper

import id.forum_admin.core.fragment.CommunityRequestDetails
import id.forum_admin.gowes.vo.Profile
import id.forum_admin.gowes.request.domain.model.Community
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.gowes.user.domain.model.User
import id.forum_admin.gowes.vo.Address
import java.util.*

fun CommunityRequestDetails.mapToDomain(): CommunityRequest =
    CommunityRequest(
        id = _id ?: "",
        user = User(
            id = user?._id ?: "",
            userName = user?.username ?: "",
            profile = Profile(
                avatar = user?.avatar ?: ""
            )
        ),
        community = Community(
            id = community?._id ?: "",
            profile = Profile(
                name = community?.name ?: "",
                avatar = community?.avatar ?: "",
                biodata = community?.biodata ?: ""
            ),
            address = Address(
                city = community?.city ?: "",
                province = community?.province ?: ""
            )
        ),
        requestedAt = requestedAt ?: Calendar.getInstance().time
    )

