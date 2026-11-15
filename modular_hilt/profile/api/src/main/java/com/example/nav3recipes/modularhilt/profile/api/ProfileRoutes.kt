package com.example.nav3recipes.modularhilt.profile.api

import com.example.nav3recipes.modularhilt.common.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data object Profile : AppNavKey

const val PROFILE_URI = "https://www.nav3recipes.com/profile"
