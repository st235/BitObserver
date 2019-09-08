package github.com.st235.bitobserver.di

import android.content.Context
import dagger.Module
import dagger.Provides
import github.com.st235.bitobserver.BuildConfig
import github.com.st235.bitobserver.debug.ThreadUtils
import github.com.st235.bitobserver.debug.Tools
import github.com.st235.bitobserver.utils.RxSchedulers
import github.com.st235.bitobserver.utils.RxSchedulersImpl
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideTools(): Tools = Tools.create(BuildConfig.DEBUG)

    @Provides
    @Singleton
    fun threadAssertionHelper(): ThreadUtils = ThreadUtils()

    @Provides
    @Singleton
    fun provideRxSchedulers(): RxSchedulers = RxSchedulersImpl()
}
