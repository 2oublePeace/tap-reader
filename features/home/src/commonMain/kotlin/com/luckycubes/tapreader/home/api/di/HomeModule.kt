package com.luckycubes.tapreader.home.api.di

import com.arkivanov.decompose.ComponentContext
import com.luckycubes.tapreader.home.api.HomeComponent
import com.luckycubes.tapreader.home.impl.HomeComponentImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val homeModule = module {
    factory<HomeComponent.Factory> {
        HomeComponent.Factory { componentContext ->
            HomeComponentImpl(componentContext = componentContext)
        }
    }
}
