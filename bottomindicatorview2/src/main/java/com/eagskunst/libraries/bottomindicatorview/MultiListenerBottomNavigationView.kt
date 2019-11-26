package com.eagskunst.libraries.bottomindicatorview

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import androidx.core.view.forEachIndexed
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Created by eagskunst in 25/11/2019.
 * A BottomNavigationView wrapper to successfully use multiple listener. The main listener is itself
 */

class MultiListenerBottomNavigationView @JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): BottomNavigationView(context, attrs, defStyleAttr),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private val listeners = mutableListOf<OnNavigationItemSelectedListener>()

    init {
        super.setOnNavigationItemSelectedListener(this)
    }

    override fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        if(listener != null) listeners.add(listener)
    }

    fun addOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener){
        listeners.add(listener)
    }

    inline fun addOnNavigationItemSelectedListener(crossinline listener: (Int) -> Unit){
        addOnNavigationItemSelectedListener(OnNavigationItemSelectedListener {
            menu.forEachIndexed { index, item -> if (item == it) listener(index) }
            false
        })
    }

    override fun onNavigationItemSelected(item: MenuItem) = listeners.map { it.onNavigationItemSelected(item) }
        .fold(false){ acc, it -> acc || it }

}