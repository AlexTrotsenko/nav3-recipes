package com.example.nav3recipes.modularhilt.common

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationBindingsModule {
    @Multibinds
    abstract fun bindNavEntryInstallers(): Set<NavEntryInstaller>

    @Multibinds
    abstract fun bindLegacyActivityLaunchers(): Map<Class<out AppNavKey>, LegacyActivityLauncher>

    @Multibinds
    abstract fun bindDeepLinkMatchers(): Set<BackStackMatcher<*, NavKey>>
}
