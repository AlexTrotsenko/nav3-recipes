package com.example.nav3recipes.modularhilt.conversationlist.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed

@Composable
internal fun ConversationListScreen(
    onConversationClicked: (Int) -> Unit,
    onProfileClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            Button(
                onClick = dropUnlessResumed(block = onProfileClicked),
                modifier = Modifier.padding(16.dp),
            ) {
                Text("Open Profile")
            }
        }
        items(10) { index ->
            val conversationId = index + 1
            val backgroundColor = conversationColor(conversationId)
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = dropUnlessResumed {
                        onConversationClicked(conversationId)
                    }),
                headlineContent = {
                    Text(
                        text = "Conversation $conversationId",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = backgroundColor
                )
            )
        }
    }
}

private val colors = listOf(
    Color(0xFFFFADAD), Color(0xFFFFD6A5), Color(0xFFFDFFB6), Color(0xFFCAFFBF),
    Color(0xFF9BF6FF), Color(0xFFA0C4FF), Color(0xFFBDB2FF), Color(0xFFFFC6FF),
)

private fun conversationColor(id: Int): Color = colors[Math.floorMod(id, colors.size)]
