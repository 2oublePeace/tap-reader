package com.luckycubes.tapreader.profile.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value

interface ProfileComponent {
    val state: Value<ProfileState>
    fun onIntent(intent: ProfileIntent)

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): ProfileComponent
    }
}
