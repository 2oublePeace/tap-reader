package com.luckycubes.tapreader.profile.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.luckycubes.tapreader.profile.impl.ui.ProfileContent

@Composable
fun ProfileScreen(
    component: ProfileComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    ProfileContent(
        state = state,
        modifier = modifier,
    )
}
