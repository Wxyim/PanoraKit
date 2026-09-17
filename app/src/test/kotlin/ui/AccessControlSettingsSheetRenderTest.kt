/*
 * This file is part of MonadBox.
 *
 * Regression test: the Access Control Settings sheet must keep its bottom button
 * row fully visible and separated from the (scrollable) settings card, even when
 * the available window height is too small for the full card content.
 */

package com.github.nomadboxlab.monadbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.component.ConfigActionMenuRow
import com.github.nomadboxlab.monadbox.presentation.component.ConfigEntryActionOption
import com.github.nomadboxlab.monadbox.presentation.component.DialogButtonRow
import com.github.nomadboxlab.monadbox.presentation.component.EnumSelector
import com.github.nomadboxlab.monadbox.presentation.component.TestTags
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperSwitch

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class, qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AccessControlSettingsSheetRenderTest {

    @get:Rule val composeTestRule = createComposeRule()

    @androidx.compose.runtime.Composable
    private fun sheetContent() {
        // Mimic BottomSheetColumn: wrapContentHeight + heightIn(max) + clip + background.
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(max = 616.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.height(36.dp))
            // Mimic the fixed layout: BoxWithConstraints + scrollable, capped card + pinned
            // buttons.
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val maxCardHeight = (maxHeight - 56.dp - 24.dp).coerceAtLeast(32.dp)
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .heightIn(max = maxCardHeight)
                                .verticalScroll(rememberScrollState())
                    ) {
                        Card {
                            SuperSwitch(title = "显示系统应用", checked = true, onCheckedChange = {})
                            SuperSwitch(title = "已选应用优先", checked = false, onCheckedChange = {})
                            EnumSelector(
                                title = "排序方式",
                                currentValue = 0,
                                items = listOf("默认", "按名称", "按时间"),
                                values = listOf(0, 1, 2),
                                onValueChange = {},
                            )
                            ConfigActionMenuRow(
                                title = "批量操作",
                                summary = "对当前筛选结果中的应用批量操作",
                                options =
                                    listOf(
                                        ConfigEntryActionOption(
                                            "全选",
                                            MonadIcons.Check,
                                            onClick = {},
                                        ),
                                        ConfigEntryActionOption(
                                            "全不选",
                                            MonadIcons.Check,
                                            onClick = {},
                                        ),
                                    ),
                            )
                            ConfigActionMenuRow(
                                title = "地区快捷选择",
                                summary = "对当前筛选结果中的应用按地区快速选择",
                                options =
                                    listOf(
                                        ConfigEntryActionOption(
                                            "国内应用",
                                            MonadIcons.Check,
                                            onClick = {},
                                        )
                                    ),
                            )
                            ConfigActionMenuRow(
                                title = "导入/导出",
                                summary = "导入时仅保留当前设备可识别的包名",
                                options =
                                    listOf(
                                        ConfigEntryActionOption(
                                            "导入",
                                            MonadIcons.Check,
                                            onClick = {},
                                        )
                                    ),
                            )
                        }
                    }
                    DialogButtonRow(
                        onCancel = {},
                        onConfirm = {},
                        cancelText = "取消",
                        confirmText = "确定",
                    )
                }
            }
        }
    }

    @Test
    fun buttonsStayVisibleOnTypicalPhoneWindow() {
        composeTestRule.setContent {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                sheetContent()
            }
        }

        val importRow = composeTestRule.onNodeWithText("导入/导出").fetchSemanticsNode().boundsInRoot
        val cancelButton =
            composeTestRule.onNodeWithTag(TestTags.Dialog.CancelButton).fetchSemanticsNode()
        val confirmButton =
            composeTestRule.onNodeWithTag(TestTags.Dialog.ConfirmButton).fetchSemanticsNode()
        val cancelBounds = cancelButton.boundsInRoot
        val confirmBounds = confirmButton.boundsInRoot

        assertTrue(
            "Button row must be fully visible within the window, got cancel=$cancelBounds confirm=$confirmBounds",
            cancelBounds.bottom <= 640f && confirmBounds.bottom <= 640f,
        )
        assertTrue(
            "Button row must start below the settings card (card bottom=${importRow.bottom}), got cancel top=${cancelBounds.top}",
            cancelBounds.top >= importRow.bottom,
        )
    }

    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h600dp")
    fun buttonsStayVisibleOnShortWindow() {
        composeTestRule.setContent {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                sheetContent()
            }
        }

        val importRow = composeTestRule.onNodeWithText("导入/导出").fetchSemanticsNode().boundsInRoot
        val cancelButton =
            composeTestRule
                .onNodeWithTag(TestTags.Dialog.CancelButton)
                .fetchSemanticsNode()
                .boundsInRoot
        val confirmButton =
            composeTestRule
                .onNodeWithTag(TestTags.Dialog.ConfirmButton)
                .fetchSemanticsNode()
                .boundsInRoot

        assertTrue(
            "Button row must be fully visible on a short window, got cancel=$cancelButton confirm=$confirmButton",
            cancelButton.bottom <= 600f && confirmButton.bottom <= 600f,
        )
        assertTrue(
            "Button row must start below the card (card bottom=${importRow.bottom}), got cancel top=${cancelButton.top}",
            cancelButton.top >= importRow.bottom,
        )
    }
}
