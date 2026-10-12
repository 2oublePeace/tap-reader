package com.luckycubes.tapreader.root.api.di

import com.arkivanov.decompose.ComponentContext
import com.luckycubes.tapreader.root.api.RootComponent
import com.luckycubes.tapreader.root.impl.RootComponentImpl
import org.koin.dsl.module

val rootModule = module {
    factory<RootComponent.Factory> {
        RootComponent.Factory { componentContext ->
            RootComponentImpl(
                componentContext = componentContext,
                homeComponentFactory = get(),
                profileComponentFactory = get(),
            )
        }
    }
}
