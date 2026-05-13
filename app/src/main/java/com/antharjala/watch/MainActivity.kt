package com.antharjala.watch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.antharjala.watch.ui.alerts.AlertsScreen
import com.antharjala.watch.ui.guide.RechargeGuideScreen
import com.antharjala.watch.ui.log.BorewellLogScreen
import com.antharjala.watch.ui.map.WaterMapScreen
import com.antharjala.watch.ui.profile.ProfileScreen
import com.antharjala.watch.ui.theme.*
import com.antharjala.watch.viewmodel.MainViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Map     : Screen("map",     "Water Map",  Icons.Default.Map)
    object Log     : Screen("log",     "Log",        Icons.Default.Add)
    object Guide   : Screen("guide",   "Guide",      Icons.Default.Eco)
    object Alerts  : Screen("alerts",  "Alerts",     Icons.Default.Notifications)
    object Profile : Screen("profile", "Profile",    Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Map, Screen.Log, Screen.Guide, Screen.Alerts, Screen.Profile
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AntharJalaWatchTheme {
                AntharJalaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntharJalaApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val unreadCount by viewModel.unreadAlertCount.collectAsState()
    val logSuccess by viewModel.logSuccess.collectAsState()

    // Snackbar for log success
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(logSuccess) {
        if (logSuccess) {
            snackbarHostState.showSnackbar(
                message = "✅ Borewell reading logged successfully!",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepOcean,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SurfaceCard,
                    contentColor = TextPrimary,
                    actionColor = CyanAccent,
                    modifier = Modifier.padding(bottom = 80.dp)
                )
            }
        },
        bottomBar = {
            AntharBottomBar(
                navController = navController,
                unreadAlertCount = unreadCount
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Map.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                WaterMapScreen(viewModel = viewModel)
            }
            composable(
                route = Screen.Log.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                BorewellLogScreen(viewModel = viewModel)
            }
            composable(
                route = Screen.Guide.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                RechargeGuideScreen()
            }
            composable(
                route = Screen.Alerts.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                AlertsScreen(viewModel = viewModel)
            }
            composable(
                route = Screen.Profile.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                ProfileScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun AntharBottomBar(
    navController: androidx.navigation.NavHostController,
    unreadAlertCount: Int
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        color = OceanBlue,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                val badgeCount = if (screen == Screen.Alerts) unreadAlertCount else 0

                BottomNavItem(
                    screen = screen,
                    selected = selected,
                    badgeCount = badgeCount,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    screen: Screen,
    selected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit
) {
    val selectedColor = CyanAccent
    val unselectedColor = TextSecondary.copy(alpha = 0.6f)
    val iconColor = if (selected) selectedColor else unselectedColor

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Box {
                Icon(
                    imageVector = screen.icon,
                    contentDescription = screen.label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                if (badgeCount > 0) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd).offset(4.dp, (-4).dp),
                        containerColor = StressCritical,
                        contentColor = Color.White
                    ) {
                        Text("$badgeCount", fontSize = 9.sp)
                    }
                }
            }
        },
        label = {
            Text(
                screen.label,
                color = iconColor,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            unselectedIconColor = unselectedColor,
            selectedTextColor = selectedColor,
            unselectedTextColor = unselectedColor,
            indicatorColor = CyanAccent.copy(alpha = 0.15f)
        )
    )
}
