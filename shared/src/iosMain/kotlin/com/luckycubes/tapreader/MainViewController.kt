package com.luckycubes.tapreader

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.luckycubes.tapreader.core.di.initKoin
import com.luckycubes.tapreader.root.api.RootComponent
import org.koin.mp.KoinPlatform

fun MainViewController() = ComposeUIViewController {
    val lifecycle = LifecycleRegistry()
    val factory = KoinPlatform.getKoin().get<RootComponent.Factory>()
    val root = factory(DefaultComponentContext(lifecycle = lifecycle))
    
    App(rootComponent = root)
}

fun initKoinIOS() {
    initKoin()
}