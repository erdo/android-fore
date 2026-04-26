package foo.bar.example.forecompose.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.early.fore.ui.size.rememberWindowSize
import foo.bar.example.forecompose.ui.screens.home.HomeScreen
import foo.bar.example.forecompose.ui.theme.ComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTheme {
                Surface( modifier = Modifier.fillMaxSize()) {
                    HomeScreen(rememberWindowSize())
                }
            }
        }
    }
}
