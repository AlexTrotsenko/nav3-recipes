package com.example.nav3recipes.modularhilt.profile.impl

import com.example.nav3recipes.modularhilt.common.LegacyActivityLauncher
import com.example.nav3recipes.modularhilt.common.NavKeyClass
import com.example.nav3recipes.modularhilt.common.legacyActivityLauncher
import com.example.nav3recipes.modularhilt.profile.api.Profile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
internal object ProfileNavigationModule {
    @Provides
    @IntoMap
    @NavKeyClass(Profile::class)
    fun provideProfileLauncher(): LegacyActivityLauncher =
        legacyActivityLauncher<ProfileActivity>()
}
