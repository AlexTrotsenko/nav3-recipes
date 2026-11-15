package com.example.nav3recipes.modularhilt.profile.impl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.nav3recipes.modularhilt.common.Navigator
import com.example.nav3recipes.modularhilt.common.extractNavKey
import com.example.nav3recipes.modularhilt.profile.api.Profile
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        intent.extractNavKey<Profile>() ?: error("Profile key is required")
        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    ProfileScreen(
                        onBack = { navigator.goBack() },
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}
