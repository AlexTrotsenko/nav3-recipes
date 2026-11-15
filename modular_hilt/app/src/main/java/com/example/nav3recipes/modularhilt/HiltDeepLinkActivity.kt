package com.example.nav3recipes.modularhilt

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.dropUnlessResumed
import com.example.nav3recipes.modularhilt.common.AppNavKey
import com.example.nav3recipes.modularhilt.common.Navigator
import com.example.nav3recipes.modularhilt.conversationdetail.api.CONVERSATION_URI
import com.example.nav3recipes.modularhilt.conversationdetail.api.ConversationDetail
import com.example.nav3recipes.modularhilt.conversationlist.api.ConversationList
import com.example.nav3recipes.modularhilt.profile.api.PROFILE_URI
import com.example.nav3recipes.modularhilt.profile.api.Profile
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HiltDeepLinkActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && handleDeepLink(intent)) return
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(
                            onClick = dropUnlessResumed {
                                navigator.goTo(ConversationList)
                            }
                        ) {
                            Text("Conversation List")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(24.dp))

                        var conversationId by rememberSaveable { mutableStateOf("") }
                        OutlinedTextField(
                            value = conversationId,
                            onValueChange = { conversationId = it },
                            label = { Text("Conversation Number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            enabled = conversationId.toIntOrNull() != null,
                            onClick = dropUnlessResumed {
                                conversationId.toIntOrNull()?.let { id ->
                                    navigator.goTo(ConversationDetail(id))
                                }
                            }
                        ) {
                            Text("Deep Link to Conversation")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = dropUnlessResumed {
                                navigator.goTo(Profile)
                            }
                        ) {
                            Text("Deep Link to Profile")
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent): Boolean {
        if (intent.action != Intent.ACTION_VIEW) return false
        val uri = intent.data ?: return false
        val conversationUri = CONVERSATION_URI.toUri()
        val key: AppNavKey = when {
            uri == PROFILE_URI.toUri() -> Profile
            uri.scheme == conversationUri.scheme &&
                uri.authority == conversationUri.authority &&
                uri.pathSegments.size == conversationUri.pathSegments.size + 1 &&
                uri.pathSegments.dropLast(1) == conversationUri.pathSegments ->
                uri.lastPathSegment?.toIntOrNull()?.let(::ConversationDetail)
            else -> null
        } ?: return false
        navigator.goTo(key)
        finish()
        return true
    }
}
