package com.example.nav3recipes.modularhilt.conversationlist.impl

import com.example.nav3recipes.modularhilt.common.LegacyActivityLauncher
import com.example.nav3recipes.modularhilt.common.NavKeyClass
import com.example.nav3recipes.modularhilt.common.legacyActivityLauncher
import com.example.nav3recipes.modularhilt.conversationlist.api.ConversationList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
internal object ConversationListNavigationModule {
    @Provides
    @IntoMap
    @NavKeyClass(ConversationList::class)
    fun provideConversationListLauncher(): LegacyActivityLauncher =
        legacyActivityLauncher<ConversationListActivity>()
}
