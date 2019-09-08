package github.com.st235.bitobserver.utils

import android.app.Activity
import android.R.attr.orientation
import android.content.res.Configuration


fun <T> Activity.alternateForOrientation(
    portraitResource: T,
    landscapeResource: T
): T {
    val orientation = resources.configuration.orientation
    return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        landscapeResource
    } else {
        portraitResource
    }
}
