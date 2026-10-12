package com.luckycubes.tapreader.profile.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.luckycubes.tapreader.profile.api.ProfileComponent
import com.luckycubes.tapreader.profile.api.ProfileState

internal class ProfileComponentImpl(
    componentContext: ComponentContext,
) : ProfileComponent, ComponentContext by componentContext {

    private val _state = MutableValue(ProfileState())
    override val state: Value<ProfileState> = _state
}
