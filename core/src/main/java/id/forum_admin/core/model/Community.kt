package id.forum_admin.gowes.request.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import id.forum_admin.gowes.user.domain.model.User
import id.forum_admin.gowes.vo.Address
import id.forum_admin.gowes.vo.Profile
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Community(
    val id: String = "",
    var profile: Profile = Profile(),
    var address: Address = Address(),
    var isPrivate: Boolean = false,
    var admins: List<User> = emptyList(),
    var memberCount: Int = 0,
    var createdAt: Date = Calendar.getInstance().time
) : Parcelable

object CommunityDiffCallback : DiffUtil.ItemCallback<Community>() {
    override fun areItemsTheSame(oldItem: Community, newItem: Community) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Community, newItem: Community) =
        oldItem == newItem
}