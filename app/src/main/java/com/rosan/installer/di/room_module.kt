package com.rosan.installer.di

import android.content.Context
import com.rosan.installer.data.settings.model.room.InstallerRoom
import com.rosan.installer.data.settings.model.room.repo.AppRepoImpl
import com.rosan.installer.data.settings.model.room.repo.ConfigRepoImpl
import com.rosan.installer.data.settings.repo.AppRepo
import com.rosan.installer.data.settings.repo.ConfigRepo
import org.koin.dsl.module

val roomModule = module {
    single {
        val context by inject<Context>()
        InstallerRoom.build(context)
    }

    single<AppRepo> {
        val roomDatabase by inject<InstallerRoom>()
        AppRepoImpl(roomDatabase.appDao())
    }

    single<ConfigRepo> {
        val roomDatabase by inject<InstallerRoom>()
        ConfigRepoImpl(roomDatabase.configDao())
    }
}
