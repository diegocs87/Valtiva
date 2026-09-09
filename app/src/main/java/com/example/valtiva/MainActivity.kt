package com.example.valtiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.presentation.navigation.ValtivaNavHost
import com.example.valtiva.presentation.theme.ThemeViewModel
import com.example.valtiva.presentation.theme.ValtivaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

private const val BRANDED_SPLASH_MILLIS = 900L

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      val themeViewModel: ThemeViewModel = hiltViewModel()
      val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

      ValtivaTheme(darkTheme = themeMode.resolveDarkTheme()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          var showBrandedSplash by remember { mutableStateOf(true) }
          LaunchedEffect(Unit) {
            delay(BRANDED_SPLASH_MILLIS)
            showBrandedSplash = false
          }

          if (showBrandedSplash) {
            BrandedSplash()
          } else {
            ValtivaNavHost()
          }
        }
      }
    }
  }
}

/**
 * Hands off from the OS's mandatory cold-start splash (icon-only, see Theme.Valtiva.Starting)
 * to the full brand artwork supplied by design, including the slogan the OS splash can't show.
 * Drawn as-is with ContentScale.Crop: the source art is centered on a large navy field, so
 * cropping only trims that empty margin and never distorts or clips the logo/wordmark/slogan.
 */
@Composable
private fun BrandedSplash() {
  Image(
    painter = painterResource(R.drawable.splash_valtiva),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier.fillMaxSize(),
  )
}

/** [ThemeMode.SYSTEM] defers to the OS setting; [ThemeMode.LIGHT]/[ThemeMode.DARK] override it. */
@Composable
private fun ThemeMode.resolveDarkTheme(): Boolean = when (this) {
  ThemeMode.LIGHT -> false
  ThemeMode.DARK -> true
  ThemeMode.SYSTEM -> isSystemInDarkTheme()
}
