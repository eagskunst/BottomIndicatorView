package com.eagskunst.libraries.bottomindicatorview

import android.graphics.Rect
import android.util.Log
import androidx.annotation.Keep

/**
 * Created by eagskunst in 25/11/2019.
 */
interface IndicatorView {

    var rect: Rect

    companion object {
        const val TAG = "BottomNavIndicatorView"
    }

    fun updateRectByIndex(index: Int, animated: Boolean)
    fun startUpdateRectAnimation(rect: Rect)
    fun updateRect(rect: Rect)
    fun attachedError(message: String) {
        Log.e(TAG, message)
    }

    @Keep fun setRectLeft(left: Int) = updateRect(rect.apply { this.left = left })
    @Keep fun setRectRight(right: Int) = updateRect(rect.apply { this.right = right })
    @Keep fun setRectTop(top: Int) = updateRect(rect.apply { this.top = top })
    @Keep fun setRectBottom(bottom: Int) = updateRect(rect.apply { this.bottom = bottom })
}