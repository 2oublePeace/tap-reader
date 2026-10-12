package com.luckycubes.tapreader.profile.api.di

import com.luckycubes.tapreader.profile.api.ProfileComponent
import com.luckycubes.tapreader.profile.impl.ProfileComponentImpl
import org.koin.dsl.module

val profileModule = module {
    factory<ProfileComponent.Factory> {
        ProfileComponent.Factory { componentContext ->
            ProfileComponentImpl(componentContext = componentContext)
        }
    }
}
