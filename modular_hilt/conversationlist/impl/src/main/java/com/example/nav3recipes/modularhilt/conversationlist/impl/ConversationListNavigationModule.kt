package com.example.nav3recipes.modularhilt.conversationlist.impl

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import com.example.nav3recipes.modularhilt.common.NavEntryInstaller
import com.example.nav3recipes.modularhilt.conversationdetail.api.CONVERSATION_URI
import com.example.nav3recipes.modularhilt.conversationdetail.api.ConversationDetail
import com.example.nav3recipes.modularhilt.conversationlist.api.ConversationList
import com.example.nav3recipes.modularhilt.profile.api.Profile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal object ConversationListNavigationModule {

    @IntoSet
    @Provides
    fun provideNavEntryInstaller(): NavEntryInstaller =
        { navigator ->
            entry<ConversationList> {
                ConversationListScreen(
                    onConversationClicked = { id -> navigator.goTo(ConversationDetail(id)) },
                    onProfileClicked = { navigator.goTo(Profile) },
                )
            }
        }

    @IntoSet
    @Provides
    fun provideDeepLinkMatcher(): BackStackMatcher<*, NavKey> =
        UriDeepLinkMatcher(
            uriPattern = DeepLinkUri("$CONVERSATION_URI/{id}"),
            serializer = ConversationDetail.serializer(),
        ).withBackStack { matchResult ->
            listOf(ConversationList, matchResult.key)
        }
}

