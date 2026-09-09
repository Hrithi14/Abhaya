package com.pbrlm.abhaya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.pbrlm.abhaya.navigation.AbhayaNavGraph
import com.pbrlm.abhaya.ui.theme.AbhayaTheme
import com.pbrlm.abhaya.ui.theme.BackgroundDark
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AbhayaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundDark)
                ) {
                    val navController = rememberNavController()
                    AbhayaNavGraph(navController = navController)
                }
            }
        }
    }
}
