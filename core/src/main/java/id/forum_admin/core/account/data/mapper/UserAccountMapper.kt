package id.forum_admin.core.account.data.mapper

import id.forum_admin.core.fragment.UserDetails
import id.forum_admin.gowes.user.domain.model.User
import id.forum_admin.gowes.vo.Profile

fun UserDetails.mapToDomain(): User =
    User(
        id = _id ?: "",
        accountId = accountId ?: "",
        profile = Profile(
            name = name ?: "",
            avatar = avatar ?: "",
            biodata = biodata ?: ""
        ),
        userName = username ?: ""
    )


