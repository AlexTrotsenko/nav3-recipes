package com.example.nav3recipes.modularhilt.profile.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Profile : NavKey

const val PROFILE_URI = "https://www.nav3recipes.com/profile"
