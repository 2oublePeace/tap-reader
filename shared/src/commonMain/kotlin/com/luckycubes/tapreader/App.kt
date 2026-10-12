package com.luckycubes.tapreader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.luckycubes.tapreader.root.api.RootComponent
import com.luckycubes.tapreader.root.api.RootScreen

@Composable
fun App(rootComponent: RootComponent) {
    MaterialTheme {
        RootScreen(
            component = rootComponent,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
        )
    }
}