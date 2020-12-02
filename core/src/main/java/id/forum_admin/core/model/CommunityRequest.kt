package id.forum_admin.gowes.request.domain.model

import androidx.recyclerview.widget.DiffUtil
import id.forum_admin.gowes.user.domain.model.User
import java.util.*

data class CommunityRequest(
    val id: String = "",
    val community: Community = Community(),
    val user: User = User(),
    val requestedAt: Date = Calendar.getInstance().time
)

object CommunityRequestDiffCallback : DiffUtil.ItemCallback<CommunityRequest>() {
    override fun areItemsTheSame(oldItem: CommunityRequest, newItem: CommunityRequest) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CommunityRequest, newItem: CommunityRequest) =
        oldItem == newItem
}