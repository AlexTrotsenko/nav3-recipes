package com.example.nav3recipes.modularhilt.conversation.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ConversationList : NavKey

@Serializable
data class ConversationDetail(val id: Int) : NavKey

const val CONVERSATION_URI = "https://www.nav3recipes.com/conversation"
