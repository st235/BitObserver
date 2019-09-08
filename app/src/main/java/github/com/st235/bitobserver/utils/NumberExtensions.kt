package github.com.st235.bitobserver.utils

import android.text.Spannable
import android.text.SpannableString
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

fun Float.clip(
    decimalSeparator: Char,
    postfix: String
): String {
    val factor = 100
    val builder = StringBuilder()

    val iv = this.toInt()
    val lv = String.format(CLIPPING_MASK, (this * factor).toInt() % factor)

    builder
        .append(iv.toString())
        .append(decimalSeparator)
        .append(lv)
        .append(postfix)

    return builder.toString()
}

fun Float.clipAndFormat(
    decimalSeparator: Char = getCurrencySeparator(),
    postfix: String = " $"
    ): Spannable {
    val decimalsToTrim = 2

    val spannable = SpannableString(
        clip(decimalSeparator = decimalSeparator, postfix = postfix)
    )

    spannable.setSpan(
        RelativeSizeSpan(0.7F),
        spannable.length - decimalsToTrim - postfix.length - 1, spannable.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannable
}
