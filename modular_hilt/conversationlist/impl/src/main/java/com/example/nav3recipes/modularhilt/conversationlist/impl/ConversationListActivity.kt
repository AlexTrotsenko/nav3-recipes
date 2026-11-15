package com.example.nav3recipes.modularhilt.conversationlist.impl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.nav3recipes.modularhilt.common.Navigator
import com.example.nav3recipes.modularhilt.conversationdetail.api.ConversationDetail
import com.example.nav3recipes.modularhilt.profile.api.Profile
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConversationListActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    ConversationListScreen(
                        onConversationClicked = { id -> navigator.goTo(ConversationDetail(id)) },
                        onProfileClicked = { navigator.goTo(Profile) },
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}
