package com.luckycubes.tapreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.arkivanov.decompose.defaultComponentContext
import com.luckycubes.tapreader.root.api.RootComponent
import org.koin.mp.KoinPlatform

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val factory = KoinPlatform.getKoin().get<RootComponent.Factory>()
        val root = factory(defaultComponentContext())

        setContent {
            App(rootComponent = root)
        }
    }
}