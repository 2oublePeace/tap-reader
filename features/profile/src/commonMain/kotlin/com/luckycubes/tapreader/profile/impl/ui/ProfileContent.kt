package com.luckycubes.tapreader.profile.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luckycubes.tapreader.profile.api.ProfileIntent
import com.luckycubes.tapreader.profile.api.ProfileState

@Composable
internal fun ProfileContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
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
private fun ProfileContentDefaultPreview() {
    MaterialTheme {
        ProfileContent(
            state = ProfileState(title = "Профиль и настройки языка"),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ProfileContentLoadingPreview() {
    MaterialTheme {
        ProfileContent(
            state = ProfileState(isLoading = true),
            onIntent = {},
        )
    }
}
