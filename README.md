# BottomIndicatorView
<p float="left">
    <img src="https://i.imgur.com/M7CnepR.png" width=200px>
    <img src="https://i.imgur.com/5ecjdTc.png" width=200px>
    <img src="https://i.imgur.com/8fLrJFG.png" width=200px>
</p>

An indicator for a MultiListenerBottomNavigationView.
Before anything, this library is heavily based on Ademar Oliveira is article. Please go to his [article](https://medium.com/@ademar111190/writing-a-custom-view-to-use-as-bottom-navigation-indicator-8cf63a737dab) and follow him.

## Including in your project
[![](https://jitpack.io/v/eagskunst/BottomIndicatorView.svg)](https://jitpack.io/#eagskunst/BottomIndicatorView)
### Gradle
Add the JitPack repository in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Add the dependency on your module's build.gradle file

	dependencies {
	        implementation 'com.github.eagskunst:BottomIndicatorView:1.0.0'
	}

## Usage
You must use Google's [Material components library](https://github.com/material-components/material-components-android) since this library uses it's BottomNavigationView implementation. Also, your project must be an androidx project.
The indicator heavily depends that the target BottomNavigation is a MultiListenerBottomNavigationView, which is a custom class that servers as a wrapper for a BottomNavigationView but could handle multiple listeners. You could customnize it as the original BottomNavigationView (iconTint, menu, etc.).
The BottomNavigationIndicatorView is the indicator itself. 
Here's an example:

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/container"
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">

    ... 
        <com.eagskunst.libraries.bottomindicatorview.MultiListenerBottomNavigationView
            android:id="@+id/bottomNavView"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginEnd="0dp"
            android:layout_marginStart="0dp"
            app:backgroundTint="@android:color/white"
            app:itemIconTint="@color/colorPrimary"
            app:elevation="5dp"
            app:menu="@menu/test_menu"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"/>

        <com.eagskunst.libraries.bottomindicatorview.BottomNavigationIndicatorView
            android:id="@+id/indicatorView"
            android:layout_width="0dp"
            android:layout_height="4dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            android:elevation="8dp"
            app:layout_constraintTop_toTopOf="@id/bottomNavView"
            app:targetBottomNavigation_indicatorView="@id/bottomNavView" //The multilistenerBottomNavView that would be attached to this indicator
            app:customIndicatorBackground_indicatorView="@drawable/custom_background_indicator" //An specific color/drawable file
            />


    </androidx.constraintlayout.widget.ConstraintLayout>
The `targetBottomNavigation` is an obligatory parameter, otherwise the indicator won't work since it doesn't know who to attach. The `customIndicatorBackground` is optional. If you don't specify a custom color/drawable, the View would use the color named `colorIndicator_indicatorView`. You could configure the color from your own module `colors.xml` file like this:

    <resources xmlns:tools="http://schemas.android.com/tools">
        <color name="colorPrimary">#008577</color>
        <color name="colorPrimaryDark">#00574B</color>
        <color name="colorAccent">#D81B60</color>
        <color name="colorIndicator_indicatorView" tools:override="true">#fff</color>
    </resources>

If you want a custom animation, inherit your custom indicator from BottomNavigationIndicatorView an override the animation APIS.

## Todo
* Wrap the two views in a single view
* Add programatic implementations for the views (setters, getters)
* Another way to add a custom animation (eg: a AnimatorSet as a param)

## License
    Copyright 2019 eagskunst (Emmanuel Guerra)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.