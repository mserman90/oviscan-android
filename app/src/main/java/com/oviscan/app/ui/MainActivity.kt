package com.oviscan.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oviscan.app.ui.screens.HistoryScreen
import com.oviscan.app.ui.screens.HomeScreen
import com.oviscan.app.ui.screens.MeasurementScreen
import com.oviscan.app.ui.screens.ResultScreen
import com.oviscan.app.ui.theme.DarkBackground
import com.oviscan.app.ui.theme.OviScanTheme
import com.oviscan.app.ui.viewmodel.HerdViewModel
import com.oviscan.app.ui.viewmodel.ScreeningViewModel

class MainActivity : ComponentActivity() {

    private val herdViewModel: HerdViewModel by viewModels()
    private val screeningViewModel: ScreeningViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        // Audio permission granted or denied handling
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

        setContent {
            OviScanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    OviScanNavigation(
                        herdViewModel = herdViewModel,
                        screeningViewModel = screeningViewModel
                    )
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}

@Composable
fun OviScanNavigation(
    herdViewModel: HerdViewModel,
    screeningViewModel: ScreeningViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = herdViewModel,
                onStartScreeningForSheep = { sheep ->
                    screeningViewModel.selectSheep(sheep)
                    navController.navigate("measurement")
                },
                onNavigateToHistory = {
                    navController.navigate("history")
                }
            )
        }

        composable("measurement") {
            MeasurementScreen(
                viewModel = screeningViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResult = {
                    navController.navigate("result") {
                        popUpTo("measurement") { inclusive = true }
                    }
                }
            )
        }

        composable("result") {
            ResultScreen(
                viewModel = screeningViewModel,
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onStartNewScreening = {
                    screeningViewModel.currentSheep.value?.let { sheep ->
                        screeningViewModel.selectSheep(sheep)
                        navController.navigate("measurement") {
                            popUpTo("result") { inclusive = true }
                        }
                    } ?: run {
                        navController.navigate("home")
                    }
                }
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = herdViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
