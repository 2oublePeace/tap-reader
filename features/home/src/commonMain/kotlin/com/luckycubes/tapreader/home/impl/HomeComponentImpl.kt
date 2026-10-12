package com.luckycubes.tapreader.home.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.luckycubes.tapreader.home.api.HomeComponent
import com.luckycubes.tapreader.home.api.HomeState

internal class HomeComponentImpl(
    componentContext: ComponentContext,
) : HomeComponent, ComponentContext by componentContext {

    private val _state = MutableValue(HomeState())
    override val state: Value<HomeState> = _state
}
