/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.forum_admin.request.presentation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import id.forum_admin.request.R as requestRes
import id.forum_admin.gowes.R
import id.forum_admin.gowes.util.lerp
import id.forum_admin.gowes.util.lerpArgb
import id.forum_admin.gowes.util.themeColor
import id.forum_admin.gowes.util.themeInterpolator
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sin

/**
 * A [Drawable] which handles drawing the background behind an email item preview and responds to
 * changes in the activated state. This includes:
 * - Drawing the star icon always
 * - On activation animating the star's color and scale
 * - On activation animating a colored circle background
 */
class RequestSwipeActionDrawable(val context: Context) : Drawable(), RequestAdapter.ViewHolder.SwipeAbleActionDrawable {

    private var circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.themeColor(R.attr.colorError)
        style = Paint.Style.FILL
    }

    // Rect to represent the circle used for the background/circular reveal animation.
    private val circle = RectF()
    private var cx = 0F
    private var cr = 0F

    private var icon = AppCompatResources.getDrawable(
            context,
        requestRes.drawable.ic_approve
    )!!
    private val iconMargin = context.resources.getDimension(R.dimen.grid_4)
    private val iconIntrinsicWidth = icon.intrinsicWidth
    private val iconIntrinsicHeight = icon.intrinsicHeight

    @ColorInt
    private val iconTint = context.themeColor(R.attr.colorOnBackground)

    @ColorInt
    private val iconTintActive = context.themeColor(R.attr.colorOnSecondary)

    // Amount that we should 'overshoot' the icon's scale by when animating.
    private val iconMaxScaleAddition = 0.5F

    private var progress = 0F
        set(value) {
            val constrained = value.coerceIn(0F, 1F)
            if (constrained != field) {
                field = constrained
                callback?.invalidateDrawable(this)
            }
        }
    private var progressAnim: ValueAnimator? = null
    private val dur = context.resources.getInteger(R.integer.gowes_motion_duration_medium)
    private val interp = context.themeInterpolator(R.attr.motionInterpolatorPersistent)

    override fun onBoundsChange(bounds: Rect?) {
        if (bounds == null) return
        update()
    }

    private fun update() {
        circle.set(
                bounds.left.toFloat(),
                bounds.top.toFloat(),
                bounds.right.toFloat(),
                bounds.bottom.toFloat()
        )

        val sideToIconCenter = iconMargin + (iconIntrinsicWidth / 2F)
//        cx = circle.right - iconMargin - (iconIntrinsicWidth / 2F)
        // Get the longest visible distance at which the circle will be displayed (the hypotenuse of
        // the triangle from the center of the icon, to the furthest side of the rect, to the top
        // corner of the rect.
        cr = hypot(circle.right - sideToIconCenter, (circle.height() / 2F))

        callback?.invalidateDrawable(this)
    }

    override fun isStateful(): Boolean = true

    override fun onStateChange(state: IntArray?): Boolean {
        val initialProgress = progress
        val newProgress = if (state?.contains(android.R.attr.state_activated) == true) {
            1F
        } else {
            0F
        }
        progressAnim?.cancel()
        progressAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener {
                progress = animatedValue as Float
            }
            interpolator = interp
            duration = (abs(newProgress - initialProgress) * dur).toLong()
        }
        progressAnim?.start()
        return newProgress == initialProgress
    }

    override fun draw(canvas: Canvas) {
        // Draw the circular reveal background.
        canvas.drawCircle(
                cx,
                circle.centerY(),
                cr * progress,
                circlePaint
        )

        // Map our progress range from 0-1 to 0-PI
        val range = lerp(
                0F,
                Math.PI.toFloat(),
                progress
        )
        // Take the sin of our ranged progress * our maxScaleAddition as what we should
        // increase the icon's scale by.
        val additive = (sin(range.toDouble()) * iconMaxScaleAddition).coerceIn(0.0, 1.0)
        val scaleFactor = 1 + additive
        icon.setBounds(
                (cx - (iconIntrinsicWidth / 2F) * scaleFactor).toInt(),
                (circle.centerY() - (iconIntrinsicHeight / 2F) * scaleFactor).toInt(),
                (cx + (iconIntrinsicWidth / 2F) * scaleFactor).toInt(),
                (circle.centerY() + (iconIntrinsicHeight / 2F) * scaleFactor).toInt()
        )

        // Draw/animate the color of the icon
        icon.setTint(
                lerpArgb(iconTint, iconTintActive, 0F, 0.15F, progress)
        )

        // Draw the icon
        icon.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        circlePaint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(filter: ColorFilter?) {
        circlePaint.colorFilter = filter
    }

    override fun changeColor(dX: Float) {
        val clr = if (dX < 0) R.attr.colorError else R.attr.colorSecondary
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.themeColor(clr)
            style = Paint.Style.FILL
        }

    }

    override fun changeIcon(dX: Float) {
        val drawable = if (dX < 0) requestRes.drawable.ic_reject else requestRes.drawable.ic_approve
        icon = AppCompatResources.getDrawable(context, drawable)!!
    }

    override fun changeSwipeDirection(dX: Float) {
        callback?.invalidateDrawable(this)
        cx = if (dX < 0) {
            Log.d("EmailViewHolder", "swipe left dX: $dX")
            circle.right - iconMargin - (iconIntrinsicWidth / 2F)
        } else {
            Log.d("EmailViewHolder", "swipe right dX: $dX")
            circle.left + iconMargin + (iconIntrinsicWidth / 2F)
        }

    }
}