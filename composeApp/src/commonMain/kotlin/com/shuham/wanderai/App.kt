package com.shuham.wanderai

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shuham.wanderai.navigation.Login
import com.shuham.wanderai.navigation.Main
import com.shuham.wanderai.navigation.SignUp
import com.shuham.wanderai.navigation.Splash
import com.shuham.wanderai.presentation.auth.login.LoginRoute
import com.shuham.wanderai.presentation.auth.signup.SignUpRoute
import com.shuham.wanderai.presentation.main.MainScreen
import com.shuham.wanderai.presentation.splash.SplashScreen
import com.shuham.wanderai.theme.WanderAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    WanderAITheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Splash,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<Splash> {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Login) { popUpTo(Splash) { inclusive = true } }
                    },
                    onNavigateToMain = {
                        navController.navigate(Main) { popUpTo(Splash) { inclusive = true } }
                    }
                )
            }

            composable<Login> {
                LoginRoute(
                    onLoginSuccess = {
                        navController.navigate(Main) { popUpTo(Login) { inclusive = true } }
                    },
                    onNavigateToSignUp = { navController.navigate(SignUp) }
                )
            }

            composable<SignUp> {
                SignUpRoute(
                    onSignUpSuccess = {
                        navController.navigate(Main) { popUpTo(SignUp) { inclusive = true } }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable<Main> {
                MainScreen(onLogout = {
                    // On logout, restart the entire app flow from Splash
                    navController.navigate(Splash) {
                        popUpTo(0) { inclusive = true } // Clears the entire back stack
                    }
                })
            }
        }
    }
}
