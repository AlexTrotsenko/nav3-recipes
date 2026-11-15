package com.example.nav3recipes.modularhilt.common

import android.app.Activity
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class Navigator @Inject constructor(
    private val activity: Activity,
    private val legacyLaunchers: Map<Class<out AppNavKey>, @JvmSuppressWildcards LegacyActivityLauncher>,
) {
    fun goTo(key: AppNavKey) {
        val launcher = legacyLaunchers[key::class.java]
            ?: error("No LegacyActivityLauncher registered for ${key::class.java.name}")
        launcher.launch(activity, key)
    }

    fun goBack() {
        activity.finish()
    }

    companion object {
        const val EXTRA_NAV_KEY = "extra_nav_key"
    }
}
