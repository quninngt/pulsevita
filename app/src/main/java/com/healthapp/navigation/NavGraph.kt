package com.healthapp.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.healthapp.ui.auth.AuthViewModel
import com.healthapp.ui.auth.LoginScreen
import com.healthapp.ui.auth.RegisterScreen
import com.healthapp.ui.diet.DietScreen
import com.healthapp.ui.exercise.ExerciseScreen
import com.healthapp.ui.home.HomeScreen
import com.healthapp.ui.mental.MentalScreen
import com.healthapp.ui.plan.PlanScreen
import com.healthapp.ui.profile.ProfileScreen
import com.healthapp.ui.profile.ExportScreen
import com.healthapp.ui.report.ReportScreen

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "首页", Icons.Default.Home),
    BottomNavItem(Screen.Diet, "饮食", Icons.Default.Restaurant),
    BottomNavItem(Screen.Exercise, "运动", Icons.Default.FitnessCenter),
    BottomNavItem(Screen.Mental, "心理", Icons.Default.Psychology)
)

/**
 * Auth navigation graph for login/register flow.
 */
@Composable
fun AuthNavHost(onLoginSuccess: () -> Unit) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = onLoginSuccess,
                viewModel = authViewModel
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = onLoginSuccess,
                viewModel = authViewModel
            )
        }
    }
}

/**
 * Main app navigation graph (requires login).
 */
@Composable
fun HealthNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true

                    // Animated color for selected state
                    val animatedTint by animateColorAsState(
                        targetValue = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(300),
                        label = "navIconTint"
                    )

                    // Animated scale for selected icon
                    val animatedScale by animateFloatAsState(
                        targetValue = if (selected) 1.15f else 1f,
                        animationSpec = tween(300),
                        label = "navIconScale"
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = animatedTint,
                                modifier = Modifier.size((24 * animatedScale).dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = if (selected) 12.sp else 11.sp
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Diet.route) {
                DietScreen(navController = navController)
            }
            composable(Screen.Exercise.route) {
                ExerciseScreen(navController = navController)
            }
            composable(Screen.Mental.route) {
                MentalScreen(navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screen.Plan.route) {
                PlanScreen(navController = navController)
            }
            composable(Screen.Report.route) {
                ReportScreen(navController = navController)
            }
            composable(Screen.Export.route) {
                ExportScreen(navController = navController)
            }
        }
    }
}
