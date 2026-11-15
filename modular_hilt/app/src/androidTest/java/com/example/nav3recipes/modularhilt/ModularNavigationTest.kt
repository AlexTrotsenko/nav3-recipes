package com.example.nav3recipes.modularhilt

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.net.toUri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nav3recipes.modularhilt.conversation.api.CONVERSATION_URI
import com.example.nav3recipes.modularhilt.profile.api.PROFILE_URI
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModularNavigationTest {
    @get:Rule
    val compose = createEmptyComposeRule()

    @Test
    fun conversationCanNavigateToProfileThroughItsApi() {
        ActivityScenario.launch<HiltModularActivity>(intent()).use {
            compose.onNodeWithText("Conversation 1").performClick()
            compose.onNodeWithText("Conversation Detail Screen: 1").assertIsDisplayed()
            compose.onNodeWithText("View Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation Detail Screen: 1").assertIsDisplayed()
        }
    }

    @Test
    fun conversationDeepLinkCreatesListParentAndSurvivesRecreation() {
        ActivityScenario.launch<HiltModularActivity>(intent("$CONVERSATION_URI/7")).use { scenario ->
            compose.onNodeWithText("Conversation Detail Screen: 7").assertIsDisplayed()
            compose.onNodeWithText("View Profile").performClick()
            scenario.recreate()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation Detail Screen: 7").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    @Test
    fun profileDeepLinkCreatesListParent() {
        ActivityScenario.launch<HiltModularActivity>(intent(PROFILE_URI)).use {
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    private fun intent(uri: String? = null) = Intent(
        ApplicationProvider.getApplicationContext(),
        HiltModularActivity::class.java,
    ).apply {
        if (uri != null) {
            action = Intent.ACTION_VIEW
            data = uri.toUri()
        }
    }
}
