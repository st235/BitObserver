package github.com.st235.bitobserver.debug

import android.content.Context
import com.facebook.stetho.Stetho
import timber.log.Timber

interface Tools {

    fun setup(androidContext: Context)

    companion object {
        fun create(isDebug: Boolean): Tools = if (isDebug) DebugTools() else ProductionTools()
    }
}

private class ProductionTools: Tools {
    override fun setup(androidContext: Context) {
        Timber.plant(TimberReleaseTree())
    }
}

private class DebugTools: Tools {
    override fun setup(androidContext: Context) {
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(androidContext)
    }
}
