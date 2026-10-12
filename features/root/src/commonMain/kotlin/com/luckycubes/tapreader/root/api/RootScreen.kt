package com.luckycubes.tapreader.root.api

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
import com.luckycubes.tapreader.root.impl.RootContent

@Composable
fun RootScreen(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    RootContent(
        component = component,
        modifier = modifier,
    )
}
