package com.luckycubes.tapreader.core.di

import com.luckycubes.tapreader.home.api.di.homeModule
import com.luckycubes.tapreader.profile.api.di.profileModule
import com.luckycubes.tapreader.root.api.di.rootModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(homeModule, profileModule, rootModule)
    }
}
