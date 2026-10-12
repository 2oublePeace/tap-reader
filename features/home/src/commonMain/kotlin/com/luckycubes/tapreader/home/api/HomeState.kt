package com.luckycubes.tapreader.home.api

import androidx.compose.runtime.Immutable

@Immutable
data class HomeState(
    val title: String = "TapReader",
    val isLoading: Boolean = false,
)
