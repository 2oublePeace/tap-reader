package com.luckycubes.tapreader.home.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.luckycubes.tapreader.home.impl.ui.HomeContent

@Composable
fun HomeScreen(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    HomeContent(
        state = state,
        onIntent = component::onIntent,
        modifier = modifier,
    )
}
