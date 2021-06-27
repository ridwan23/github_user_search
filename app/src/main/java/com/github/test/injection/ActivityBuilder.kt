package com.github.test.injection

import androidx.paging.ExperimentalPagingApi
import com.github.test.ui.main.MainActivity
import com.github.test.ui.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ExperimentalPagingApi
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity

}