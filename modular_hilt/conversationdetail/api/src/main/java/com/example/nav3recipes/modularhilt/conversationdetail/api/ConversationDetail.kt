package com.example.nav3recipes.modularhilt.conversationdetail.api

import com.example.nav3recipes.modularhilt.common.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class ConversationDetail(val id: Int) : AppNavKey

const val CONVERSATION_URI = "https://www.nav3recipes.com/conversation"
