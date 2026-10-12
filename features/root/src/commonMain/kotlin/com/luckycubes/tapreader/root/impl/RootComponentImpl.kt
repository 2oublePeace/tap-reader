package com.luckycubes.tapreader.root.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.luckycubes.tapreader.home.api.HomeComponent
import com.luckycubes.tapreader.profile.api.ProfileComponent
import com.luckycubes.tapreader.root.api.RootComponent
import kotlinx.serialization.Serializable

internal class RootComponentImpl(
    componentContext: ComponentContext,
    private val homeComponentFactory: HomeComponent.Factory,
    private val profileComponentFactory: ProfileComponent.Factory,
) : RootComponent, ComponentContext by componentContext {

    private val stackNavigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = stackNavigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Home,
        handleBackButton = true,
        childFactory = ::stackChild,
    )

    private fun stackChild(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child {
        return when (config) {
            is Config.Home -> RootComponent.Child.Home(
                component = homeComponentFactory(componentContext = componentContext)
            )
            is Config.Profile -> RootComponent.Child.Profile(
                component = profileComponentFactory(componentContext = componentContext)
            )
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Home : Config

        @Serializable
        data object Profile : Config
    }
}
