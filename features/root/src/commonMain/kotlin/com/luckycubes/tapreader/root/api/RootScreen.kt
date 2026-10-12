package com.luckycubes.tapreader.root.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
