package github.com.st235.bitobserver

import android.app.Application
import android.content.Context
import github.com.st235.bitobserver.debug.Tools
import github.com.st235.bitobserver.di.AppComponent
import github.com.st235.bitobserver.di.AppModule
import github.com.st235.bitobserver.di.DaggerAppComponent
import javax.inject.Inject

class BitObserverApp: Application() {

    companion object {
        fun get(applicationContext: Context): BitObserverApp? {
            return (applicationContext as? BitObserverApp)
        }
    }

    @Inject
    lateinit var tools: Tools

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        onDiCreate()

        tools.setup(this)
    }

    private fun onDiCreate() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        appComponent.inject(this)
    }
}
