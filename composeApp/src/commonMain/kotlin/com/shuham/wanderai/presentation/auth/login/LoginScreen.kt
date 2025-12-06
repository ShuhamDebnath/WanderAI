package com.shuham.wanderai.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import wanderai.composeapp.generated.resources.Res
import wanderai.composeapp.generated.resources.apple
import wanderai.composeapp.generated.resources.facebook
import wanderai.composeapp.generated.resources.google
import wanderai.composeapp.generated.resources.traveler

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (state.loginSuccess) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToSignUp = onNavigateToSignUp
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            Color(0xFF004A52)
                        )
                    )
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(60.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.traveler),
                    contentDescription = "Traveler Illustration",
                    modifier = Modifier.size(width = 180.dp, height = 240.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome Back!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { onAction(LoginAction.OnEmailChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email Address") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, null) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onAction(LoginAction.OnPasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray,
                        ),
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            val icon = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { onAction(LoginAction.OnTogglePasswordVisibility) }) {
                                Icon(icon, contentDescription = "Toggle password visibility")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onAction(LoginAction.OnLoginClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White)
                        } else {
                            Text("Log In", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    OrDivider()
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        SocialButton(onClick = { onAction(LoginAction.OnGoogleSignInClicked) }, iconRes = Res.drawable.google)
                        SocialButton(onClick = { onAction(LoginAction.OnAppleSignInClicked) }, iconRes = Res.drawable.apple)
                        SocialButton(onClick = { onAction(LoginAction.OnFacebookSignInClicked) }, iconRes = Res.drawable.facebook)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Don\'t have an account?")
                        TextButton(onClick = onNavigateToSignUp) {
                            Text("Sign Up", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (state.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { onAction(LoginAction.OnErrorDismissed) },
                title = { Text("Login Failed") },
                text = { Text(state.errorMessage) },
                confirmButton = {
                    Button(onClick = { onAction(LoginAction.OnErrorDismissed) }) {
                        Text("OK")
                    }
                }
            )
        }

        if (state.showFeatureComingSoonDialog) {
            AlertDialog(
                onDismissRequest = { onAction(LoginAction.OnFeatureComingSoonDismissed) },
                title = { Text("Feature Coming Soon") },
                text = { Text("Social login options will be available in a future update.") },
                confirmButton = {
                    Button(onClick = { onAction(LoginAction.OnFeatureComingSoonDismissed) }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text("OR", color = Color.Gray, fontWeight = FontWeight.SemiBold)
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SocialButton(onClick: () -> Unit, iconRes: DrawableResource) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
    }
}
