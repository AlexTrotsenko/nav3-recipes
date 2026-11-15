package com.example.nav3recipes.modularhilt

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.core.net.toUri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.example.nav3recipes.modularhilt.common.Navigator
import com.example.nav3recipes.modularhilt.profile.api.PROFILE_URI
import com.example.nav3recipes.modularhilt.profile.api.Profile
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
    fun listToProfileStaysInHostAndSurvivesRecreation() {
        ActivityScenario.launch<HiltModularActivity>(intent()).use { scenario ->
            val originalHost = resumedActivity()
            compose.onNodeWithText("Open Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertEquals(originalHost, resumedActivity())
            scenario.recreate()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    @Test
    fun legacyDetailToProfileOpensNewHostAndBackUnwindsBothActivities() {
        ActivityScenario.launch<HiltModularActivity>(intent()).use {
            val originalHost = resumedActivity()
            compose.onNodeWithText("Conversation 1").performClick()
            compose.onNodeWithText("Conversation Detail Screen: 1").assertIsDisplayed()
            assertEquals(
                "com.example.nav3recipes.modularhilt.conversationdetail.impl.ConversationDetailActivity",
                resumedActivity().javaClass.name,
            )
            compose.onNodeWithText("Open Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertEquals(HiltModularActivity::class.java, resumedActivity().javaClass)
            assertNotSame(originalHost, resumedActivity())
            pressBack()
            compose.onNodeWithText("Conversation Detail Screen: 1").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            assertEquals(originalHost, resumedActivity())
        }
    }

    @Test
    fun explicitBackButtonsFinishNewHostAndLegacyDetail() {
        ActivityScenario.launch<HiltModularActivity>(intent()).use {
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
    fun profileDeepLinkCreatesListParent() {
        ActivityScenario.launch<HiltModularActivity>(intent(PROFILE_URI)).use {
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    @Test
    fun deepLinkExampleActivityStillOpensProfile() {
        ActivityScenario.launch(HiltDeepLinkActivity::class.java).use {
            compose.onNodeWithText("Deep Link to Profile").performClick()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Deep Link to Profile").assertIsDisplayed()
        }
    }

    @Test
    fun conversationDeepLinkLaunchesLegacyDetailAboveList() {
        ActivityScenario.launch(HiltDeepLinkActivity::class.java).use {
            compose.onNodeWithText("Conversation Number").performTextInput("7")
            closeSoftKeyboard()
            compose.onNodeWithText("Deep Link to Conversation").performClick()
            compose.onNodeWithText("Conversation Detail Screen: 7").assertIsDisplayed()
            assertEquals(
                "com.example.nav3recipes.modularhilt.conversationdetail.impl.ConversationDetailActivity",
                resumedActivity().javaClass.name,
            )
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Deep Link to Conversation").assertIsDisplayed()
        }
    }

    @Test
    fun newUriIntentAppendsProfileToExistingHost() {
        ActivityScenario.launch<HiltModularActivity>(intent()).use { scenario ->
            val originalHost = resumedActivity()
            scenario.onActivity { activity ->
                activity.startActivity(intent(PROFILE_URI).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            assertEquals(originalHost, resumedActivity())
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    @Test
    fun newIntentAppendsProfileAndConsumesKeyBeforeRecreation() {
        ActivityScenario.launch<HiltModularActivity>(intent()).use { scenario ->
            scenario.onActivity { activity ->
                activity.startActivity(
                    Intent(Navigator.ACTION_OPEN_NAV3_HOST).apply {
                        setPackage(activity.packageName)
                        putExtra(Navigator.EXTRA_NAV_KEY, Profile)
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                )
            }
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            scenario.recreate()
            compose.onNodeWithText("Profile Screen").assertIsDisplayed()
            pressBack()
            compose.onNodeWithText("Conversation 1").assertIsDisplayed()
        }
    }

    private fun resumedActivity(): Activity {
        lateinit var activity: Activity
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activity = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED).single()
        }
        return activity
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
