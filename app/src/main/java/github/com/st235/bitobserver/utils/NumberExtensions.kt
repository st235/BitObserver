package github.com.st235.bitobserver.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.format.DateFormat
import android.text.style.RelativeSizeSpan
import java.text.DecimalFormatSymbols
import java.util.*

const val DATE_FORMAT = "dd MMMM, yyyy"
const val CLIPPING_MASK = "%1$02d"

/**
 * @return currency decimal separator for current locale
 */
fun getCurrencySeparator() = DecimalFormatSymbols.getInstance().decimalSeparator

fun Long.dateFromTimeStamp(locale: Locale = Locale.getDefault()): String {
    val calendar = Calendar.getInstance(locale)
    calendar.timeInMillis = this * 1000
    return DateFormat.format(DATE_FORMAT, calendar).toString()
}

fun Float.findNearest(round: Int): Float {
    if (this <= 0) {
        return 0F
    }

    val value = this.toInt()

    val a = value / round * round
    val b = a + round
    return (if (this - a > b - this) b else a).toFloat()
}

fun Float.clipAndFormat(): Spannable {
    val builder = SpannableStringBuilder()

    val iv = this.toInt()
    val lv = String.format(CLIPPING_MASK, (this * 100).toInt() % 100)

    builder
        .append(iv.toString())
        .append(getCurrencySeparator())
        .append(lv)
        .append(" $")

    builder.setSpan(
        RelativeSizeSpan(0.7F),
        builder.length - lv.length - 3, builder.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return builder
}
