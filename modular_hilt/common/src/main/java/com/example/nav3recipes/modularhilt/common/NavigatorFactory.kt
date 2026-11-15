package com.example.nav3recipes.modularhilt.common

import android.app.Activity
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject

class NavigatorFactory @Inject constructor(
    private val legacyLaunchers: Map<Class<out AppNavKey>, @JvmSuppressWildcards LegacyActivityLauncher>,
) {
    fun create(
        activity: Activity,
        backStack: SnapshotStateList<NavKey>? = null,
    ): Navigator = Navigator(activity, legacyLaunchers, backStack)
}
