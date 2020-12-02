package id.forum_admin.request.presentation

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.forum_admin.gowes.request.domain.model.Community
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.gowes.request.domain.model.CommunityRequestDiffCallback
import id.forum_admin.request.R
import id.forum_admin.request.databinding.ItemListRequstLayoutBinding
import id.forum_admin.request.presentation.ReboundingSwipeActionCallback.ReboundableViewHolder
import id.forum_admin.request.presentation.RequestAdapter.ViewHolder.Companion.from
import kotlin.math.abs


class RequestAdapter(private val listener: RequestListener) :
    ListAdapter<CommunityRequest, RequestAdapter.ViewHolder>(CommunityRequestDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ViewHolder private constructor(
        private val binding: ItemListRequstLayoutBinding,
        private val requestListener: RequestListener
    ) : RecyclerView.ViewHolder(binding.root), ReboundableViewHolder {

        override val reboundableView: View = binding.cardView

        private var swipeActionDrawable: SwipeAbleActionDrawable

        init {
            binding.run {
                this.listener = requestListener
                val emailSwipeActionDrawable = RequestSwipeActionDrawable(root.context)
                swipeActionDrawable = emailSwipeActionDrawable
                root.background = emailSwipeActionDrawable
            }
        }

        var dX = 0F
        private lateinit var swipeListener: () -> Unit
        override fun onReboundOffsetChanged(
            currentSwipePercentage: Float,
            swipeThreshold: Float,
            currentTargetHasMetThresholdOnce: Boolean,
            dX: Float
        ) {
            swipeListener = if (dX < 0) {
                { requestListener.rejectListener(communityRequest) }
            } else {
                { requestListener.approveListener(communityRequest) }
            }
            swipeActionDrawable.changeColor(dX)
            swipeActionDrawable.changeIcon(dX)
            swipeActionDrawable.changeSwipeDirection(dX)
            // Only alter shape and activation in the forward direction once the swipe
            // threshold has been met. Undoing the swipe would require releasing the item and
            // re-initiating the swipe.
            if (currentTargetHasMetThresholdOnce) return

            val isStarred = false

            // Animate the top left corner radius of the email card as swipe happens.
            val interpolation = (currentSwipePercentage / swipeThreshold).coerceIn(0F, 1F)
            val adjustedInterpolation = abs((if (isStarred) 1F else 0F) - interpolation)
            binding.cardView.progress = adjustedInterpolation

            // Start the background animation once the threshold is met.
            val thresholdMet = currentSwipePercentage >= swipeThreshold
            val shouldStar = when {
                thresholdMet && isStarred -> false
                thresholdMet && !isStarred -> true
                else -> return
            }
            binding.root.isActivated = shouldStar
        }

        override fun onRebounded() {
            binding.root.isActivated = false
            swipeListener.invoke()
        }

        interface SwipeAbleActionDrawable {
            fun changeColor(dX: Float)
            fun changeIcon(dX: Float)
            fun changeSwipeDirection(dX: Float)
        }
        private lateinit var communityRequest: CommunityRequest
        fun bind(request: CommunityRequest) {
            communityRequest = request
                binding.apply {
                this.communityRequest = request
                cardView.progress = 0F
                tvContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(
                        tvContent.context.getString(
                            R.string.content_request,
                            request.user.userName,
                            request.community.profile.name
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    Html.fromHtml(
                        tvContent.context.getString(
                            R.string.content_request,
                            request.user.userName,
                            request.community.profile.name
                        )
                    )

                }
                executePendingBindings()
            }
        }


        companion object {
            fun from(parent: ViewGroup, listener: RequestListener) =
                ViewHolder(
                    ItemListRequstLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), listener
                )
        }
    }
}

class RequestListener(
    val clickListener: (community: Community) -> Unit,
    val approveListener: (communityRequest: CommunityRequest) -> Unit,
    val rejectListener: (communityRequest: CommunityRequest) -> Unit
) {
    fun onClick(community: Community) = clickListener(community)
    fun onApproved(communityRequest: CommunityRequest) = approveListener(communityRequest)
    fun onRejected(communityRequest: CommunityRequest) = rejectListener(communityRequest)
}