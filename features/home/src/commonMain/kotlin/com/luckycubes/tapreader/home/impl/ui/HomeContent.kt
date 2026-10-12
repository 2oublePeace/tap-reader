package com.luckycubes.tapreader.home.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luckycubes.tapreader.home.api.HomeIntent
import com.luckycubes.tapreader.home.api.HomeState

@Composable
internal fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(state.title)
        }
    }
}

@Preview
@Composable
private fun HomeContentDefaultPreview() {
    MaterialTheme {
        HomeContent(
            state = HomeState(title = "TapReader — Библиотека"),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun HomeContentLoadingPreview() {
    MaterialTheme {
        HomeContent(
            state = HomeState(isLoading = true),
            onIntent = {},
        )
    }
}
