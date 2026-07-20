/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.performance

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Until

internal fun MacrobenchmarkScope.startupJourney() {
    pressHome()
    startActivityAndWait()
    device.wait(
        Until.hasObject(By.pkg(BenchmarkConfig.TargetPackage).depth(0)),
        BenchmarkConfig.StartupTimeoutMs,
    )
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.openNextMainPage() {
    val midY = device.displayHeight / 2
    device.swipe(device.displayWidth * 4 / 5, midY, device.displayWidth / 5, midY, 24)
    device.waitForIdle()
}

/** Navigate through bottom navigation tabs (Profiles, Settings) and back to Home. */
internal fun MacrobenchmarkScope.bottomNavigationJourney() {
    // Swipe to Profiles tab
    openNextMainPage()
    device.waitForIdle()

    // Swipe to Settings tab
    openNextMainPage()
    device.waitForIdle()

    // Swipe back to Home
    val midY = device.displayHeight / 2
    device.swipe(device.displayWidth / 5, midY, device.displayWidth * 4 / 5, midY, 24)
    device.waitForIdle()
    device.swipe(device.displayWidth / 5, midY, device.displayWidth * 4 / 5, midY, 24)
    device.waitForIdle()
}

/** Scroll settings list to exercise lazy list composition. */
internal fun MacrobenchmarkScope.settingsScrollJourney() {
    // Navigate to Settings (swipe right twice from Home)
    openNextMainPage()
    openNextMainPage()
    device.waitForIdle()

    // Scroll down
    val midX = device.displayWidth / 2
    repeat(3) {
        device.swipe(midX, device.displayHeight * 3 / 4, midX, device.displayHeight / 4, 20)
        device.waitForIdle()
    }

    // Scroll back up
    repeat(3) {
        device.swipe(midX, device.displayHeight / 4, midX, device.displayHeight * 3 / 4, 20)
        device.waitForIdle()
    }
}

/** Exercise the profile/config import entry path without requiring a fixture file or network. */
internal fun MacrobenchmarkScope.configurationImportJourney() {
    navigateToConfigPage()
    val openedImportSurface =
        clickFirstMatching("Add profile", "Add Profile", "Add", "添加配置", "添加", "新增")

    if (openedImportSurface) {
        clickFirstMatching(
            "Subscription",
            "URL",
            "Local file",
            "Blank config",
            "订阅",
            "链接",
            "本地文件",
            "空白配置",
        )
        dismissTransientSurface()
    }
}

/** Exercise the primary Home start/stop control when the runtime can be toggled in this install. */
internal fun MacrobenchmarkScope.startStopProxyJourney() {
    navigateToHomePage()
    val attemptedStart =
        clickFirstMatching("Tap to start", "Start", "VPN", "TUN", "HTTP", "点击启动", "启动")
    if (!attemptedStart) return

    dismissPermissionOrErrorSurface()
    device.waitForIdle()
    clickFirstMatching("Running", "Stop", "VPN", "TUN", "HTTP", "运行", "停止")
    dismissPermissionOrErrorSurface()
}

/** Exercise edit/save controls for local profile or override editors when seeded data exists. */
internal fun MacrobenchmarkScope.editSaveJourney() {
    navigateToConfigPage()
    val openedEditEntry =
        clickFirstMatching("Edit", "Open config", "Edit settings", "编辑", "打开配置", "编辑设置")
    if (!openedEditEntry) return

    clickFirstMatching("Open config", "Edit", "打开配置", "编辑")
    clickFirstMatching("Save", "Save and exit", "保存", "保存并退出")
    dismissPermissionOrErrorSurface()
    dismissTransientSurface()
}

private fun MacrobenchmarkScope.navigateToHomePage() {
    val midY = device.displayHeight / 2
    repeat(3) {
        device.swipe(device.displayWidth / 5, midY, device.displayWidth * 4 / 5, midY, 24)
        device.waitForIdle()
    }
}

private fun MacrobenchmarkScope.navigateToConfigPage() {
    if (clickFirstMatching("Config", "Profiles", "配置", "订阅")) return

    navigateToHomePage()
    repeat(2) { openNextMainPage() }
}

private fun MacrobenchmarkScope.dismissTransientSurface() {
    device.pressBack()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.dismissPermissionOrErrorSurface() {
    clickFirstMatching("Cancel", "Deny", "Not now", "OK", "取消", "拒绝", "暂不", "确定")
}

private fun MacrobenchmarkScope.clickFirstMatching(vararg needles: String): Boolean {
    for (needle in needles) {
        if (clickFirst(By.textContains(needle))) return true
        if (clickFirst(By.descContains(needle))) return true
    }
    return false
}

private fun MacrobenchmarkScope.clickFirst(selector: BySelector): Boolean {
    val node =
        device.wait(Until.findObject(selector), BenchmarkConfig.UiWaitTimeoutMs) ?: return false
    node.click()
    device.waitForIdle()
    return true
}
