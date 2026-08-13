package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.CodeReaderDatabase
import com.example.data.CodeReaderRepository
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.components.AppNavigationDrawer
import com.example.ui.components.NavDestination
import com.example.ui.screens.CodeViewerScreen
import com.example.ui.screens.FolderCustomizationScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.CodeReaderTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = CodeReaderDatabase.getDatabase(this)
        val repository = CodeReaderRepository(database.recentFileDao(), database.folderDao())
        val factory = MainViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Handle intent if launched via "Open with"
        handleIncomingIntent(intent)

        setContent {
            CodeReaderTheme(darkTheme = true) { // Geometric Balance dark glass theme
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val data: Uri? = intent.data

        if ((Intent.ACTION_VIEW == action || Intent.ACTION_EDIT == action) && data != null) {
            viewModel.openFile(this, data)
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var currentNavDestination by remember { mutableStateOf(NavDestination.WORKSPACE) }

    val openFileState by viewModel.openFileState.collectAsState()
    LaunchedEffect(openFileState.uriString) {
        if (openFileState.uriString.isNotEmpty() && !openFileState.isLoading && openFileState.errorMessage == null) {
            if (navController.currentDestination?.route != "viewer") {
                navController.navigate("viewer")
            }
        }
    }

    AppNavigationDrawer(
        drawerState = drawerState,
        currentDestination = currentNavDestination,
        onNavigate = { destination ->
            currentNavDestination = destination
            coroutineScope.launch { drawerState.close() }

            when (destination) {
                NavDestination.WORKSPACE -> navController.navigate("workspace") {
                    popUpTo("workspace") { inclusive = true }
                }
                NavDestination.FOLDERS -> navController.navigate("folders") {
                    popUpTo("workspace")
                }
                NavDestination.SETTINGS -> navController.navigate("settings") {
                    popUpTo("workspace")
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "workspace") {
            composable("workspace") {
                WorkspaceScreen(
                    viewModel = viewModel,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onOpenFile = {
                        navController.navigate("viewer")
                    },
                    onNavigateToFolders = {
                        currentNavDestination = NavDestination.FOLDERS
                        navController.navigate("folders")
                    }
                )
            }

            composable("folders") {
                FolderCustomizationScreen(
                    viewModel = viewModel,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }

            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }

            composable("viewer") {
                CodeViewerScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
