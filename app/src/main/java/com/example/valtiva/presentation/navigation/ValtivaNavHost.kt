package com.example.valtiva.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.valtiva.presentation.main.MainScreen

@Composable
fun ValtivaNavHost(modifier: Modifier = Modifier) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = MainRoute, modifier = modifier) {
    composable<MainRoute> {
      MainScreen(modifier = Modifier.safeDrawingPadding().padding(16.dp))
    }
  }
}
