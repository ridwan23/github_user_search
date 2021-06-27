package com.github.test.ui.main

import androidx.lifecycle.ViewModel
import com.github.test.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindViewModel(mainVM: MainViewModel): ViewModel
}