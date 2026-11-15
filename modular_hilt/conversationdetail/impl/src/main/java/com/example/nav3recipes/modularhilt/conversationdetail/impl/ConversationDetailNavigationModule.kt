package com.example.nav3recipes.modularhilt.conversationdetail.impl

import com.example.nav3recipes.modularhilt.common.LegacyActivityLauncher
import com.example.nav3recipes.modularhilt.common.NavKeyClass
import com.example.nav3recipes.modularhilt.common.legacyActivityLauncher
import com.example.nav3recipes.modularhilt.conversationdetail.api.ConversationDetail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
internal object ConversationDetailNavigationModule {
    @Provides
    @IntoMap
    @NavKeyClass(ConversationDetail::class)
    fun provideConversationDetailLauncher(): LegacyActivityLauncher =
        legacyActivityLauncher<ConversationDetailActivity>()
}
