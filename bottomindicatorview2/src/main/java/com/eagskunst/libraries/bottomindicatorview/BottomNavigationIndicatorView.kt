package com.eagskunst.libraries.bottomindicatorview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

/**
 * Created by eagskunst in 25/11/2019.
 */

class BottomNavigationIndicatorView  @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0, override var rect: Rect = Rect()):
        View(context, attributeSet, defStyleAttr), IndicatorView {

    companion object {
        private const val NO_ID = -1
        private const val indicatorLastIndex = "INDICATOR_LAST_INDEX"
    }

    private val targetId: Int
    private val indicatorDrawable: Drawable
    private var bottomNav: BottomNavigationMenuView? = null
    private var animator: AnimatorSet? = null
    private var index = 0

    init {
        if(attributeSet == null){
            targetId = NO_ID
            indicatorDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.colorIndicator_indicatorView))
        }
        else{
            with(context.obtainStyledAttributes(attributeSet, R.styleable.BottomNavigationIndicatorView)){
                val backgroundId = getResourceId(R.styleable.BottomNavigationIndicatorView_customIndicatorBackground, NO_ID)
                indicatorDrawable = if(backgroundId == NO_ID) ColorDrawable(ContextCompat.getColor(context, R.color.colorIndicator_indicatorView))
                else getDrawable(backgroundId) ?: ColorDrawable(ContextCompat.getColor(context, R.color.colorIndicator_indicatorView))

                targetId = getResourceId(R.styleable.BottomNavigationIndicatorView_targetBottomNavigation, NO_ID)
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.clipRect(rect)
        canvas?.let{ indicatorDrawable.draw(it) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if(targetId == NO_ID) return attachedError("Invalid targetId $targetId. Set the app:targetBottomNavigation value")
        val parentView = parent as? View ?: return attachedError("Impossible to find the view using $parent")
        val child = parentView.findViewById<View?>(targetId)
        if(child !is MultiListenerBottomNavigationView) return attachedError("Invalid view child or could not find MultiListenerBottomNavigationView")

        for(i in 0..child.childCount){
            val subView = child.getChildAt(i)
            if(subView is BottomNavigationMenuView) {
                bottomNav = subView
                break
            }
        }

        child.addOnNavigationItemSelectedListener { updateRectByIndex(it, true) }
        post { updateRectByIndex(index, false) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bottomNav = null
    }

    override fun updateRectByIndex(index: Int, animated: Boolean) {
        this.index = index
        bottomNav?.apply {
            if (childCount < 1 || index >= childCount) return
            val reference = getChildAt(index)

            val start = reference.left + left
            val end = reference.right + left

            indicatorDrawable.setBounds(left, top, right, bottom)
            val newRect = Rect(start, 0, end, height)
            if (animated) startUpdateRectAnimation(newRect) else updateRect(newRect)
        }
    }

    override fun startUpdateRectAnimation(rect: Rect) {
        animator?.cancel()
        animator = AnimatorSet().also {
            it.playTogether(
                ObjectAnimator.ofInt(this, "rectLeft", this.rect.left, rect.left),
                ObjectAnimator.ofInt(this, "rectRight", this.rect.right, rect.right),
                ObjectAnimator.ofInt(this, "rectTop", this.rect.top, rect.top),
                ObjectAnimator.ofInt(this, "rectBottom", this.rect.bottom, rect.bottom)
            )
            it.interpolator = FastOutSlowInInterpolator()
            it.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            it.start()
        }
    }

    override fun updateRect(rect: Rect) {
        this.rect = rect
        postInvalidate()
    }

}