package com.example.nav3recipes.modularhilt.common

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

// Installers receive the navigator for the current Activity rather than retaining one in DI.
typealias NavEntryInstaller = EntryProviderScope<NavKey>.(Navigator) -> Unit

class Navigator(
    private val activity: Activity,
    private val legacyLaunchers: Map<Class<out AppNavKey>, LegacyActivityLauncher>,
    private val backStack: SnapshotStateList<NavKey>? = null,
) {
    fun goTo(key: AppNavKey) {
        val launcher = legacyLaunchers[key::class.java]
        when {
            launcher != null -> launcher.launch(activity, key)
            backStack != null -> backStack.add(key)
            else -> activity.startActivity(
                Intent(ACTION_OPEN_NAV3_HOST).apply {
                    setPackage(activity.packageName)
                    putExtra(EXTRA_NAV_KEY, key)
                }
            )
        }
    }

    fun goBack() {
        if (backStack != null && backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            activity.finish()
        }
    }

    companion object {
        const val EXTRA_NAV_KEY = "extra_nav_key"
        const val ACTION_OPEN_NAV3_HOST = "com.example.nav3recipes.ACTION_OPEN_NAV3_HOST"
    }
}
