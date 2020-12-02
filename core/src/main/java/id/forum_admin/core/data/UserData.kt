package id.forum_admin.gowes.data

import id.forum_admin.gowes.user.domain.model.User

data class UserData(
//    val stitch: Task<StitchUser>?,
    val user: User = User(),
    val token: Token = Token()
)