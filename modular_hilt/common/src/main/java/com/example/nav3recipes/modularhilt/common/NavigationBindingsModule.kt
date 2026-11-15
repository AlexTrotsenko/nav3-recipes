package com.example.nav3recipes.modularhilt.common

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationBindingsModule {
    @Multibinds
    abstract fun bindLegacyActivityLaunchers(): Map<Class<out AppNavKey>, LegacyActivityLauncher>
}
