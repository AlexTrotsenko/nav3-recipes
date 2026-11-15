package com.example.nav3recipes.modularhilt.conversationdetail.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import com.example.nav3recipes.modularhilt.conversationdetail.api.ConversationDetail

@Composable
internal fun ConversationDetailScreen(
    conversationDetail: ConversationDetail,
    onProfileClicked: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(conversationColor(conversationDetail.id))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Conversation Detail Screen: ${conversationDetail.id}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = dropUnlessResumed(block = onProfileClicked)) {
            Text("Open Profile")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = dropUnlessResumed(block = onBack)) {
            Text("Back")
        }
    }
}

private val colors = listOf(
    Color(0xFFFFADAD), Color(0xFFFFD6A5), Color(0xFFFDFFB6), Color(0xFFCAFFBF),
    Color(0xFF9BF6FF), Color(0xFFA0C4FF), Color(0xFFBDB2FF), Color(0xFFFFC6FF),
)

private fun conversationColor(id: Int): Color = colors[Math.floorMod(id, colors.size)]
