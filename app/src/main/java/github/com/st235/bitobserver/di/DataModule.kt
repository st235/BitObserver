package github.com.st235.bitobserver.di

import dagger.Module
import dagger.Provides
import github.com.st235.data.ChartRepository
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun providesChartRepository(): ChartRepository {
        return ChartRepository.createInstance()
    }
}