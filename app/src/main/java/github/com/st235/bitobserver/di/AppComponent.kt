package github.com.st235.bitobserver.di

import dagger.Component
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.presentation.charts.ChartActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun inject(app: BitObserverApp)
    fun inject(activity: ChartActivity)
}