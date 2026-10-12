package com.luckycubes.tapreader.profile.api

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileState(
    val title: String = "Profile",
    val isLoading: Boolean = false,
)
