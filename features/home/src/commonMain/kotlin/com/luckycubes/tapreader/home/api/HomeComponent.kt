package com.luckycubes.tapreader.home.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value

interface HomeComponent {
    val state: Value<HomeState>

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): HomeComponent
    }
}
