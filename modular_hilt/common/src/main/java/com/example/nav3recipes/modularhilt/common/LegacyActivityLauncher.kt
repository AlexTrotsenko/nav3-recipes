package com.example.nav3recipes.modularhilt.common

import android.app.Activity
import android.content.Intent
import dagger.MapKey
import kotlin.reflect.KClass

fun interface LegacyActivityLauncher {
    fun launch(activity: Activity, key: AppNavKey)
}

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NavKeyClass(val value: KClass<out AppNavKey>)

inline fun <reified T : Activity> legacyActivityLauncher(
    flags: Int? = null,
): LegacyActivityLauncher = LegacyActivityLauncher { activity, key ->
    val intent = Intent(activity, T::class.java).apply {
        putExtra(Navigator.EXTRA_NAV_KEY, key)
        flags?.let { this.flags = it }
    }
    activity.startActivity(intent)
}
