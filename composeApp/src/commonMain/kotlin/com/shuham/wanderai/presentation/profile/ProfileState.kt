package com.shuham.wanderai.presentation.profile

data class ProfileState(
    val email: String? = null,
    val displayName: String? = null,
    val isLoggedOut: Boolean = false
)

sealed interface ProfileAction {
    object OnLogoutClicked : ProfileAction
}
