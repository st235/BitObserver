package github.com.st235.bitobserver.utils

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Enclosed::class)
class NumberExtensionsTest {

    @RunWith(Parameterized::class)
    class FloatFindNearestTest(val value: Float, val roundBy: Int, val expected: Float) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<Any>> =
                listOf(
                    arrayOf(344F, 100, 300F),
                    arrayOf(364F, 100, 400F),
                    arrayOf(12F, 10, 10F),
                    arrayOf(10_000F, 100, 10_000F),
                    arrayOf(-544F, 100, 0F),
                    arrayOf(198F, 10, 200F),
                    arrayOf(198F, 100, 200F),
                    arrayOf(193F, 10, 190F),
                    arrayOf(2678F, 1000, 3000F),
                    arrayOf(2678F, 500, 2500F),
                    arrayOf(364F, 300, 300F),
                    arrayOf(178F, 400, 0F),
                    arrayOf(888F, 900, 900F)
                )
        }

        @Test
        fun `test that nearest value calculated correctly`() {
            val result = value.findNearest(roundBy)
            assertEquals("Test case ($value, $roundBy, $expected), actual: $result",
                expected, result)
        }
    }

    @RunWith(Parameterized::class)
    class FloatClipTest(val value: Float, val expected: String) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<Any>> =
                listOf(
                    arrayOf(123F, "123.00"),
                    arrayOf(123.77F, "123.77"),
                    arrayOf(123.777F, "123.77"),
                    arrayOf(123.007F, "123.00"),
                    arrayOf(123.017F, "123.01"),
                    arrayOf(123.017F, "123.01"),
                    arrayOf(123.44F, "123.44")
                )
        }

        @Test
        fun `test that number clipped correctly`() {
            val result = value.clip(decimalSeparator = '.', postfix = "")
            assertEquals("Test case ($value, $expected), actual: $result",
                expected, result)
        }
    }
}
