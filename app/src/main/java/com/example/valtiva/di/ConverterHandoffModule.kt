package com.example.valtiva.di

import com.example.valtiva.presentation.converter.ConverterHandoffCoordinator
import com.example.valtiva.presentation.converter.ConverterHandoffPublisher
import com.example.valtiva.presentation.converter.ConverterHandoffReceiver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Binds the two faces of the History-to-Converter hand-off to their single coordinator.
 *
 * Split from [RepositoryModule]/[UseCaseModule] on purpose: this is presentation-layer wiring (a
 * cross-screen signal), not a domain repository or use case, so it does not belong among them.
 *
 * [ConverterHandoffCoordinator] is `@Singleton` itself and needs no [dagger.Provides] factory; the
 * two [Binds] only tell Dagger which interface each consumer should see.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ConverterHandoffModule {

  @Binds
  abstract fun bindConverterHandoffPublisher(
    coordinator: ConverterHandoffCoordinator,
  ): ConverterHandoffPublisher

  @Binds
  abstract fun bindConverterHandoffReceiver(
    coordinator: ConverterHandoffCoordinator,
  ): ConverterHandoffReceiver
}
