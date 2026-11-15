package com.example.nav3recipes.modularhilt

import androidx.navigation3.runtime.NavKey
import com.example.nav3recipes.modularhilt.conversation.api.ConversationList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppModule {

    @Provides
    fun provideStartDestination(): NavKey = ConversationList
}
