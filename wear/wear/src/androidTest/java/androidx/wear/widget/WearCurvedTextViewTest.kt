/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.wear.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.View.MeasureSpec
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.screenshot.AndroidXScreenshotTestRule
import androidx.test.screenshot.assertAgainstGolden
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@MediumTest
class WearCurvedTextViewTest {

    private val bitmap = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val renderDoneLatch = CountDownLatch(1)

    @get:Rule
    val screenshotRule = AndroidXScreenshotTestRule("wear/wear")

    private fun doOneTest(key: String, views: List<View>) {
        // Set the main frame.
        val mainFrame = FrameLayout(ApplicationProvider.getApplicationContext())
        mainFrame.setBackgroundColor(Color.GRAY)

        for (view in views) {
            mainFrame.addView(view)
        }
        val screenWidth = MeasureSpec.makeMeasureSpec(SCREEN_WIDTH, MeasureSpec.EXACTLY)
        val screenHeight = MeasureSpec.makeMeasureSpec(SCREEN_HEIGHT, MeasureSpec.EXACTLY)
        mainFrame.measure(screenWidth, screenHeight)
        mainFrame.layout(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)
        mainFrame.draw(canvas)
        renderDoneLatch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        bitmap.assertAgainstGolden(screenshotRule, key)
    }

    private fun createThree() = listOf(
        WearCurvedTextView(ApplicationProvider.getApplicationContext()).apply {
            text = "Center"
            textColor = Color.BLUE
            setBackgroundColor(Color.rgb(100, 100, 0))
            anchorType = WearArcLayout.ANCHOR_CENTER
            anchorAngleDegrees = 0f
        },
        WearCurvedTextView(ApplicationProvider.getApplicationContext()).apply {
            text = "Left"
            textColor = Color.RED
            setBackgroundColor(Color.rgb(0, 100, 100))
            anchorAngleDegrees = 240f
            anchorType = WearArcLayout.ANCHOR_START
        },
        WearCurvedTextView(ApplicationProvider.getApplicationContext()).apply {
            text = "Right"
            textColor = Color.GREEN
            setBackgroundColor(Color.rgb(100, 0, 100))
            anchorType = WearArcLayout.ANCHOR_END
            anchorAngleDegrees = 120f
        }
    )

    @Test
    @Throws(Exception::class)
    fun testDefaults() {
        val textView = WearCurvedTextView(ApplicationProvider.getApplicationContext())
        textView.text = "Hello World!"
        doOneTest("hello_world_screenshot", listOf(textView))
    }

    @Test
    @Throws(Exception::class)
    fun testAnchor() {
        doOneTest("anchors_screenshot", createThree())
    }

    @Test
    @Throws(Exception::class)
    fun testSweepDegree() {
        doOneTest(
            "sweep_degree_screenshot",
            createThree().apply {
                forEachIndexed { ix, v ->
                    v.sweepDegrees = 50f
                    v.textAlignment = listOf(
                        View.TEXT_ALIGNMENT_CENTER,
                        View.TEXT_ALIGNMENT_TEXT_START,
                        View.TEXT_ALIGNMENT_TEXT_END
                    )[ix]
                }
            }
        )
    }

    @Test
    @Throws(Exception::class)
    fun testCounterClockwise() {
        doOneTest(
            "counter_clockwise_screenshot",
            createThree().apply { forEach { it.clockwise = false } }
        )
    }

    @Test
    @Throws(Exception::class)
    fun testTextSize() {
        doOneTest(
            "text_size_screenshot",
            createThree().apply {
                forEachIndexed { ix, it -> it.textSize = 20f + ix * 4f }
            }
        )
    }

    @Test
    @Throws(Exception::class)
    fun testEllipsize() {
        doOneTest(
            "ellipsize_screenshot",
            (
                createThree() zip
                    listOf(
                        TextUtils.TruncateAt.START,
                        TextUtils.TruncateAt.MIDDLE,
                        TextUtils.TruncateAt.END
                    )
                )
                .map
                { (v, e) -> v.ellipsize = e ; v.sweepDegrees = 50f; v.text += " but Longer" ; v }
        )
    }

    @Test
    @Throws(Exception::class)
    fun testPadding() {
        doOneTest(
            "padding_screenshot",
            createThree().apply {
                forEachIndexed { ix, it ->
                    it.setPadding(
                        ix * 10, ((ix + 1) % 4) * 10,
                        ((ix + 2) % 4) * 10, ((ix + 3) % 4) * 10
                    )
                }
            }
        )
    }

    companion object {
        private const val SCREEN_WIDTH = 390
        private const val SCREEN_HEIGHT = 390
        private const val TIMEOUT_MS = 1000L
    }
}
