package com.luckycubes.tapreader.profile.api

sealed interface ProfileIntent {
    data object Refresh : ProfileIntent
}
