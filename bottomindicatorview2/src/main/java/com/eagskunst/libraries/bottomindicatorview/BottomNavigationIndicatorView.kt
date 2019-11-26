package com.eagskunst.libraries.bottomindicatorview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

/**
 * Created by eagskunst in 25/11/2019.
 * BottomIndicator for a [MultiListenerBottomNavigationView]. Could use a color or a custom drawable.
 * If no drawable is provided, the default color is used. If you don't want to provide a color through the XML,
 * you could add it in your own app file and use the 'tools:override=true' option. The name for the color would be 'colorIndicator_indicatorView'
 * The params that this view receives are the same as any View.
 *
 */

open class BottomNavigationIndicatorView  @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0):
        View(context, attributeSet, defStyleAttr) {

    companion object {
        protected const val NO_ID = -1
        protected const val INDICATOR_LAST_INDEX = "INDICATOR_LAST_INDEX"
        protected const val TAG = "BottomNavIndicatorView"
        protected const val SAVED_STATE = "SAVED_STATE"
    }

    private val targetId: Int //The [MultiListenerBottomNavigationView] view ID.
    private val indicatorDrawable: Drawable //The Drawable to use as indicator.
    private var bottomNav: BottomNavigationMenuView? = null
    private var animator: AnimatorSet? = null
    private var rect = Rect()
    private var index = 0 //The current index selected of the BottomNav. This is also used to restore the view state.

    init {
        if(attributeSet == null){
            targetId = NO_ID
            indicatorDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.colorIndicator_indicatorView))
        }
        else{
            with(context.obtainStyledAttributes(attributeSet, R.styleable.BottomNavigationIndicatorView)){
                targetId = getResourceId(R.styleable.BottomNavigationIndicatorView_targetBottomNavigation_indicatorView, NO_ID)

                val backgroundId = getResourceId(R.styleable.BottomNavigationIndicatorView_customIndicatorBackground_indicatorView, NO_ID)
                indicatorDrawable = if(backgroundId == NO_ID) ColorDrawable(ContextCompat.getColor(context, R.color.colorIndicator_indicatorView))
                else ContextCompat.getDrawable(context, backgroundId) ?: ColorDrawable(ContextCompat.getColor(context, R.color.colorIndicator_indicatorView))
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.clipRect(rect)
        canvas?.let { indicatorDrawable.draw(it) }
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

    override fun onSaveInstanceState(): Parcelable?  =
        Bundle().apply {
            putParcelable(SAVED_STATE, super.onSaveInstanceState())
            putInt(INDICATOR_LAST_INDEX, index)
        }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var savedState: Parcelable? = null
        if(state != null && state is Bundle){
            index = state.getInt(INDICATOR_LAST_INDEX)
            post { updateRectByIndex(index, false) }
            savedState = state.getParcelable(SAVED_STATE)
        }
        super.onRestoreInstanceState(savedState)
    }

    /**
    * Updates the rect position thanks to the bottomNavigationView that is attached trough the xml
    * @param index The navigation item is index
    * @param animated Animate the rect with or without animation
    * */
    open fun updateRectByIndex(index: Int, animated: Boolean) {
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

    /**
    * Makes the view animation. You could override this to make your own implementation.
    * @param rect: The rect that is going to animate.
    * */
    open fun startUpdateRectAnimation(rect: Rect) {
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

    /*
    * Updates the Rect that the view uses
    * */
    open fun updateRect(rect: Rect) {
        this.rect = rect
        postInvalidate()
    }

    private fun attachedError(message: String) {
        Log.e(TAG, message)
    }

    /**
    * Functions called trough reflection.
    * */
    @Keep fun setRectLeft(left: Int) = updateRect(rect.apply { this.left = left })
    @Keep fun setRectRight(right: Int) = updateRect(rect.apply { this.right = right })
    @Keep fun setRectTop(top: Int) = updateRect(rect.apply { this.top = top })
    @Keep fun setRectBottom(bottom: Int) = updateRect(rect.apply { this.bottom = bottom })

}