package com.example.nav3recipes.modularhilt

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.example.nav3recipes.modularhilt.common.extractNavKey
import com.example.nav3recipes.modularhilt.conversationdetail.api.CONVERSATION_URI
import com.example.nav3recipes.modularhilt.conversationdetail.api.ConversationDetail
import com.example.nav3recipes.modularhilt.conversationdetail.impl.ConversationDetailActivity
import com.example.nav3recipes.modularhilt.conversationlist.impl.ConversationListActivity
import com.example.nav3recipes.modularhilt.profile.api.PROFILE_URI
import com.example.nav3recipes.modularhilt.profile.api.Profile
import com.example.nav3recipes.modularhilt.profile.impl.ProfileActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModularNavigationTest {
    @get:Rule
    val compose = createEmptyComposeRule()

    @After
    fun finishRemainingActivities() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val monitor = ActivityLifecycleMonitorRegistry.getInstance()
            listOf(Stage.RESUMED, Stage.PAUSED, Stage.STARTED, Stage.STOPPED, Stage.CREATED)
                .flatMap { monitor.getActivitiesInStage(it).toList() }
                .filter { it.packageName == "com.example.nav3recipes.modularhilt" }
                .forEach { it.finish() }
        }
    }

    @Test
    fun listToProfileLaunchesActivityAndSystemBackReturnsToList() {
        ActivityScenario.launch(ConversationListActivity::class.java).use {
            val listActivity = resumedActivity()
            compose.onNodeWithText("Open Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertActivity<ProfileActivity>()
            assertNotSame(listActivity, resumedActivity())
            assertEquals(Profile, resumedActivity().intent.extractNavKey<Profile>())
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            assertEquals(listActivity, resumedActivity())
        }
    }

    @Test
    fun listDetailProfileSystemBackUnwindsThreeActivities() {
        ActivityScenario.launch(ConversationListActivity::class.java).use {
            val listActivity = resumedActivity()
            compose.onNodeWithText("Conversation 1").performClick()
            compose.onNodeWithText("Conversation Detail Screen: 1").assertIsDisplayed()
            assertActivity<ConversationDetailActivity>()
            val detailActivity = resumedActivity()
            assertEquals(ConversationDetail(1), detailActivity.intent.extractNavKey<ConversationDetail>())
            compose.onNodeWithText("Open Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertActivity<ProfileActivity>()
            pressBack()
            compose.onNodeWithText("Conversation Detail Screen: 1").assertIsDisplayed()
            assertEquals(detailActivity, resumedActivity())
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            assertEquals(listActivity, resumedActivity())
        }
    }

    @Test
    fun explicitBackButtonsFinishProfileAndDetail() {
        ActivityScenario.launch(ConversationListActivity::class.java).use {
            compose.onNodeWithText("Conversation 1").performClick()
            compose.onNodeWithText("Open Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            compose.onNodeWithText("Back").performClick()
            compose.onNodeWithText("Conversation Detail Screen: 1").assertIsDisplayed()
            compose.onNodeWithText("Back").performClick()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    @Test
    fun demoButtonsLaunchListDetailAndProfile() {
        ActivityScenario.launch(HiltDeepLinkActivity::class.java).use {
            compose.onNodeWithText("Conversation List").performClick()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            assertActivity<ConversationListActivity>()
            pressBack()
            compose.onNodeWithText("Conversation Number").performTextInput("7")
            closeSoftKeyboard()
            compose.onNodeWithText("Deep Link to Conversation").performClick()
            compose.onNodeWithText("Conversation Detail Screen: 7").assertIsDisplayed()
            assertActivity<ConversationDetailActivity>()
            pressBack()
            compose.onNodeWithText("Deep Link to Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertActivity<ProfileActivity>()
            pressBack()
            compose.onNodeWithText("Deep Link to Profile").assertIsDisplayed()
        }
    }

    @Test
    fun conversationViewIntentRoutesToDetailAndFinishesRouter() {
        ActivityScenario.launch(ConversationListActivity::class.java).use { scenario ->
            scenario.onActivity { it.startActivity(viewIntent(it, "$CONVERSATION_URI/7")) }
            compose.onNodeWithText("Conversation Detail Screen: 7").assertIsDisplayed()
            assertActivity<ConversationDetailActivity>()
            assertEquals(ConversationDetail(7), resumedActivity().intent.extractNavKey<ConversationDetail>())
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            assertActivity<ConversationListActivity>()
        }
    }

    @Test
    fun profileViewIntentRoutesToProfileAndFinishesRouter() {
        ActivityScenario.launch(ConversationListActivity::class.java).use { scenario ->
            scenario.onActivity { it.startActivity(viewIntent(it, PROFILE_URI)) }
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertActivity<ProfileActivity>()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            assertActivity<ConversationListActivity>()
        }
    }

    @Test
    fun newViewIntentRoutesFromExistingDemoActivity() {
        ActivityScenario.launch(HiltDeepLinkActivity::class.java).use { scenario ->
            compose.onNodeWithText("Deep Link to Profile").assertIsDisplayed()
            scenario.onActivity { demo ->
                demo.startActivity(viewIntent(demo, PROFILE_URI).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertActivity<ProfileActivity>()
            compose.waitUntil(timeoutMillis = 5_000) { scenario.state == Lifecycle.State.DESTROYED }
            assertEquals(Lifecycle.State.DESTROYED, scenario.state)
        }
    }

    @Test
    fun invalidConversationUriDoesNotLaunchDetailOrFinishDemo() {
        val intent = viewIntent(ApplicationProvider.getApplicationContext(), "$CONVERSATION_URI/not-an-int")
        ActivityScenario.launch<HiltDeepLinkActivity>(intent).use { scenario ->
            compose.onNodeWithText("Deep Link to Profile").assertIsDisplayed()
            assertActivity<HiltDeepLinkActivity>()
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
        }
    }

    @Test
    fun recreatedProfileUsesNavigatorForCurrentActivity() {
        ActivityScenario.launch(ConversationListActivity::class.java).use {
            compose.onNodeWithText("Open Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            val previousActivity = resumedActivity()
            InstrumentationRegistry.getInstrumentation().runOnMainSync { previousActivity.recreate() }
            compose.waitUntil(timeoutMillis = 5_000) {
                val current = resumedActivityOrNull()
                current is ProfileActivity && current !== previousActivity
            }
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertNotSame(previousActivity, resumedActivity())
            compose.onNodeWithText("Back").performClick()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    private inline fun <reified T : Activity> assertActivity() {
        assertEquals(T::class.java, resumedActivity().javaClass)
    }

    private fun resumedActivity(): Activity = checkNotNull(resumedActivityOrNull())

    private fun resumedActivityOrNull(): Activity? {
        var activity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activity = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED).singleOrNull()
        }
        return activity
    }

    private fun viewIntent(context: Context, uri: String) =
        Intent(context, HiltDeepLinkActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = uri.toUri()
        }
}
