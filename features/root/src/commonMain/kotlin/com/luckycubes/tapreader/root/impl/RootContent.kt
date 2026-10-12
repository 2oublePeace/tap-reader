package com.luckycubes.tapreader.root.impl

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.luckycubes.tapreader.home.api.HomeScreen
import com.luckycubes.tapreader.profile.api.ProfileScreen
import com.luckycubes.tapreader.root.api.RootComponent

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.childStack,
        modifier = modifier.fillMaxSize(),
        animation = stackAnimation(fade() + scale()),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Home -> HomeScreen(
                component = instance.component,
                modifier = Modifier.fillMaxSize(),
            )
            is RootComponent.Child.Profile -> ProfileScreen(
                component = instance.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}