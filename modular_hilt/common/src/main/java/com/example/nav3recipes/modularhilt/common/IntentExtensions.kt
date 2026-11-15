package com.example.nav3recipes.modularhilt.common

import android.content.Intent
import androidx.core.content.IntentCompat

inline fun <reified T : AppNavKey> Intent.extractNavKey(): T? =
    IntentCompat.getSerializableExtra(this, Navigator.EXTRA_NAV_KEY, T::class.java)
