package com.example.nav3recipes.modularhilt

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.core.os.BundleCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.modularhilt.common.AppNavKey
import com.example.nav3recipes.modularhilt.common.NavEntryInstaller
import com.example.nav3recipes.modularhilt.common.Navigator
import com.example.nav3recipes.modularhilt.common.NavigatorFactory
import com.example.nav3recipes.modularhilt.common.extractNavKey
import com.example.nav3recipes.modularhilt.conversationlist.api.ConversationList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HiltModularActivity : ComponentActivity() {
    @Inject
    lateinit var navigatorFactory: NavigatorFactory

    @Inject
    lateinit var entryInstallers: Set<@JvmSuppressWildcards NavEntryInstaller>

    @Inject
    lateinit var deepLinkMatchers: Set<@JvmSuppressWildcards BackStackMatcher<*, NavKey>>

    private val backStack: SnapshotStateList<NavKey> = mutableStateListOf()
    private lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        navigator = navigatorFactory.create(activity = this, backStack = backStack)

        val restoredStack = savedInstanceState?.let {
            BundleCompat.getSerializable(it, STATE_BACK_STACK, ArrayList::class.java)
                ?.filterIsInstance<AppNavKey>()
        }
        val incomingKey = if (savedInstanceState == null) intent.extractNavKey<AppNavKey>() else null
        when {
            !restoredStack.isNullOrEmpty() -> backStack.addAll(restoredStack)
            incomingKey != null -> {
                // A new host opened above a legacy Activity starts with only the requested screen.
                backStack.add(incomingKey)
                intent.removeExtra(Navigator.EXTRA_NAV_KEY)
            }
            else -> {
                backStack.add(ConversationList)
                if (savedInstanceState == null) {
                    // Matchers describe synthetic stacks; route each destination through Navigator
                    // so legacy keys are launched as Activities and never inserted into NavDisplay.
                    intent.matchDeepLink()?.drop(1)?.forEach(navigator::goTo)
                }
            }
        }

        setContent {
            MaterialTheme {
                Scaffold(
                    bottomBar = {
                        TextButton(onClick = {
                            startActivity(Intent(this@HiltModularActivity, HiltDeepLinkActivity::class.java))
                        }) {
                            Text("Deep link examples")
                        }
                    },
                ) { paddingValues ->
                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier.padding(paddingValues),
                        onBack = navigator::goBack,
                        entryProvider = entryProvider {
                            entryInstallers.forEach { install -> install(navigator) }
                        },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val incomingKey = intent.extractNavKey<AppNavKey>()
        if (incomingKey != null) {
            intent.removeExtra(Navigator.EXTRA_NAV_KEY)
            navigator.goTo(incomingKey)
        } else {
            intent.matchDeepLink()?.lastOrNull()?.let(navigator::goTo)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(STATE_BACK_STACK, ArrayList(backStack.filterIsInstance<AppNavKey>()))
        super.onSaveInstanceState(outState)
    }

    private fun Intent.matchDeepLink(): List<AppNavKey>? {
        if (data == null) return null
        val request = DeepLinkRequest(this)
        return deepLinkMatchers.firstNotNullOfOrNull { it.match(request) }
            ?.backStack?.map { it as AppNavKey }
    }

    private companion object {
        const val STATE_BACK_STACK = "nav3_back_stack"
    }
}
