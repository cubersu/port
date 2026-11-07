package com.example.port

import com.example.port.domain.repository.PortRepository
import com.example.port.data.repository.DefaultPortRepository
import dagger.Module
import dagger.Provides
import dagger.Scope

@Scope
@Module
abstract class PortModule {
    @Provides
    @Scope
    abstract fun providesPortRepository(): PortRepository
    
    @Provides
    fun provideDefaultPortRepository(): PortRepository = DefaultPortRepository()
}
