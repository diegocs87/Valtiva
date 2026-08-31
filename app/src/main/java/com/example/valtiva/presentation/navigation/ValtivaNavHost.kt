package com.example.valtiva.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.valtiva.presentation.home.ValtivaHomeScreen

@Composable
fun ValtivaNavHost(modifier: Modifier = Modifier) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = MainRoute, modifier = modifier) {
    composable<MainRoute> {
      // Window insets are handled by the home shell's Scaffold, which has to place the bottom bar
      // against them; padding the whole graph here would push that bar up off the system bar.
      ValtivaHomeScreen()
    }
  }
}
