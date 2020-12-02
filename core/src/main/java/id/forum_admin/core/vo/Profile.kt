package id.forum_admin.gowes.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Profile(
    val name: String = "",
    var avatar: String = "",
    val biodata: String = "",
    val createdOn: Date = Calendar.getInstance().time
) : Parcelable