/*
 * This file is part of MonadBox.
 *
 * Regression test: the traffic bar chart axis label must show "0 B" when there is
 * no traffic yet, instead of a misleading "1 B" minimum.
 */

package com.github.nomadboxlab.monadbox.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.nomadboxlab.monadbox.presentation.component.BarChartItem
import com.github.nomadboxlab.monadbox.presentation.component.TrafficBarChart
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class TrafficBarChartTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun zeroTraffic_displaysZeroBytesLabel() {
        composeTestRule.setContent {
            TrafficBarChart(
                items =
                    listOf(BarChartItem("9", 0L), BarChartItem("10", 0L), BarChartItem("11", 0L))
            )
        }

        composeTestRule.onNodeWithText("0 B").assertIsDisplayed()
    }

    @Test
    fun withTraffic_displaysMaxValueLabel() {
        composeTestRule.setContent {
            TrafficBarChart(items = listOf(BarChartItem("9", 1024L), BarChartItem("10", 2048L)))
        }

        composeTestRule.onNodeWithText("2.0 KB").assertIsDisplayed()
    }
}
