package com.example.port.di

import com.example.port.data.repository.DefaultPortRepository
import com.example.port.domain.repository.PortRepository

/** Basit, elle yazılmış bağımlılık sağlayıcı. */
object PortModule {
    val portRepository: PortRepository by lazy { DefaultPortRepository() }
}
