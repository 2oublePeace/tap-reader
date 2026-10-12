package com.luckycubes.tapreader.home.api

sealed interface HomeIntent {
    data object Refresh : HomeIntent
}
