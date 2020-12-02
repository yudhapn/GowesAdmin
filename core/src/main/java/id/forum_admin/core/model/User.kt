package id.forum_admin.gowes.user.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import id.forum_admin.gowes.vo.Profile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val accountId: String = "",
    var profile: Profile = Profile(),
    val userName: String = ""
) : Parcelable

object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: User, newItem: User) =
        oldItem.profile.name == newItem.profile.name
                && oldItem.userName == newItem.userName
}
