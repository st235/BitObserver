package github.com.st235.data.models

import androidx.annotation.StringRes
import github.com.st235.data.R

enum class TimeInterval(
    @StringRes val localizationId: Int,
    val requestId: String
) {
    SEVEN_DAYS(R.string.seven_days, "7days"),
    FIVE_WEEKS(R.string.five_weeks, "5weeks"),
    MONTH(R.string.one_month, "1months"),
    ALL(R.string.all, "all")
}
