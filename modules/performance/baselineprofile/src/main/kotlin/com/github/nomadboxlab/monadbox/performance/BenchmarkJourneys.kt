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

// Labels are matched against both English and Chinese resources; every journey reports the label it
// matched (or the full candidate list when it matched nothing) so that a reworded UI shows up in
// the
// generator report instead of silently dropping coverage.
private val ConfigTabLabels = listOf("Config", "Profiles", "配置", "订阅")
private val SettingsTabLabels = listOf("Settings", "设置")
private val AddProfileLabels = listOf("Add profile", "Add Profile", "Add", "添加配置", "添加", "新增")
private val ImportSourceLabels =
    listOf("Subscription", "URL", "Local file", "Blank config", "订阅", "链接", "本地文件", "空白配置")
private val StartControlLabels = listOf("Tap to start", "Start", "VPN", "TUN", "HTTP", "点击启动", "启动")
private val StopControlLabels = listOf("Running", "Stop", "VPN", "TUN", "HTTP", "运行", "停止")
private val EditEntryLabels = listOf("Edit", "Open config", "Edit settings", "编辑", "打开配置", "编辑设置")
private val OpenConfigLabels = listOf("Open config", "Edit", "打开配置", "编辑")
private val SaveLabels = listOf("Save", "Save and exit", "保存", "保存并退出")

internal fun MacrobenchmarkScope.startupJourney(): JourneyResult {
    pressHome()
    startActivityAndWait()
    val windowVisible =
        device.wait(
            Until.hasObject(By.pkg(BenchmarkConfig.TargetPackage).depth(0)),
            BenchmarkConfig.StartupTimeoutMs,
        ) != null
    device.waitForIdle()
    return if (windowVisible) {
        JourneyResult.exercised("${BenchmarkConfig.TargetPackage} window visible")
    } else {
        JourneyResult.skipped(
            "${BenchmarkConfig.TargetPackage} window not visible within ${BenchmarkConfig.StartupTimeoutMs}ms"
        )
    }
}

internal fun MacrobenchmarkScope.openNextMainPage() {
    val midY = device.displayHeight / 2
    device.swipe(device.displayWidth * 4 / 5, midY, device.displayWidth / 5, midY, 24)
    device.waitForIdle()
}

/** Navigate through bottom navigation tabs (Profiles, Settings) and back to Home. */
internal fun MacrobenchmarkScope.bottomNavigationJourney(): JourneyResult {
    openNextMainPage()
    device.waitForIdle()
    val profilesSurface = firstMatchingLabel(ConfigTabLabels)

    openNextMainPage()
    device.waitForIdle()
    val settingsSurface = firstMatchingLabel(SettingsTabLabels)

    // Swipe back to Home.
    val midY = device.displayHeight / 2
    repeat(2) {
        device.swipe(device.displayWidth / 5, midY, device.displayWidth * 4 / 5, midY, 24)
        device.waitForIdle()
    }

    return when {
        profilesSurface == null ->
            JourneyResult.skipped("no match for profiles surface among $ConfigTabLabels")
        settingsSurface == null ->
            JourneyResult.skipped(
                "profiles '$profilesSurface' reached, but no settings surface among $SettingsTabLabels"
            )
        else -> JourneyResult.exercised("profiles '$profilesSurface', settings '$settingsSurface'")
    }
}

/** Scroll settings list to exercise lazy list composition. */
internal fun MacrobenchmarkScope.settingsScrollJourney(): JourneyResult {
    // Navigate to Settings (swipe right twice from Home).
    openNextMainPage()
    openNextMainPage()
    device.waitForIdle()

    val settingsSurface = firstMatchingLabel(SettingsTabLabels)

    // Scroll unconditionally: the lazy-list coverage is wanted even when the verdict below has to
    // report that the Settings surface was not recognised.
    val midX = device.displayWidth / 2
    repeat(3) {
        device.swipe(midX, device.displayHeight * 3 / 4, midX, device.displayHeight / 4, 20)
        device.waitForIdle()
    }
    repeat(3) {
        device.swipe(midX, device.displayHeight / 4, midX, device.displayHeight * 3 / 4, 20)
        device.waitForIdle()
    }

    return if (settingsSurface == null) {
        JourneyResult.skipped("no settings surface among $SettingsTabLabels")
    } else {
        JourneyResult.exercised("settings '$settingsSurface' scrolled both ways")
    }
}

/** Exercise the profile/config import entry path without requiring a fixture file or network. */
internal fun MacrobenchmarkScope.configurationImportJourney(): JourneyResult {
    navigateToConfigPage()
    val addEntry =
        clickFirstMatching(*AddProfileLabels.toTypedArray())
            ?: return JourneyResult.skipped("no add-profile entry among $AddProfileLabels")

    val importSource = clickFirstMatching(*ImportSourceLabels.toTypedArray())
    dismissTransientSurface()

    return if (importSource == null) {
        JourneyResult.skipped(
            "add-profile '$addEntry' opened, but no import source among $ImportSourceLabels"
        )
    } else {
        JourneyResult.exercised("add profile '$addEntry' -> import '$importSource'")
    }
}

/** Exercise the primary Home start/stop control when the runtime can be toggled in this install. */
internal fun MacrobenchmarkScope.startStopProxyJourney(): JourneyResult {
    navigateToHomePage()
    val start =
        clickFirstMatching(*StartControlLabels.toTypedArray())
            ?: return JourneyResult.skipped(
                "no start control among $StartControlLabels (install has no profile/config seeded)"
            )

    dismissPermissionOrErrorSurface()
    device.waitForIdle()
    val stop = clickFirstMatching(*StopControlLabels.toTypedArray())
    dismissPermissionOrErrorSurface()

    return if (stop == null) {
        JourneyResult.skipped("started via '$start', but no stop control among $StopControlLabels")
    } else {
        JourneyResult.exercised("start '$start', stop '$stop'")
    }
}

/** Exercise edit/save controls for local profile or override editors when seeded data exists. */
internal fun MacrobenchmarkScope.editSaveJourney(): JourneyResult {
    navigateToConfigPage()
    val editEntry =
        clickFirstMatching(*EditEntryLabels.toTypedArray())
            ?: return JourneyResult.skipped("no edit entry among $EditEntryLabels")

    clickFirstMatching(*OpenConfigLabels.toTypedArray())
    val save = clickFirstMatching(*SaveLabels.toTypedArray())
    dismissPermissionOrErrorSurface()
    dismissTransientSurface()

    return if (save == null) {
        JourneyResult.skipped("editor '$editEntry' opened, but no save control among $SaveLabels")
    } else {
        JourneyResult.exercised("edit '$editEntry' -> save '$save'")
    }
}

private fun MacrobenchmarkScope.navigateToHomePage() {
    val midY = device.displayHeight / 2
    repeat(3) {
        device.swipe(device.displayWidth / 5, midY, device.displayWidth * 4 / 5, midY, 24)
        device.waitForIdle()
    }
}

private fun MacrobenchmarkScope.navigateToConfigPage() {
    if (clickFirstMatching(*ConfigTabLabels.toTypedArray()) != null) return

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

private fun MacrobenchmarkScope.firstMatchingLabel(needles: List<String>): String? =
    needles.firstOrNull { needle ->
        device.wait(Until.hasObject(By.textContains(needle)), BenchmarkConfig.UiVerifyTimeoutMs) !=
            null ||
            device.wait(
                Until.hasObject(By.descContains(needle)),
                BenchmarkConfig.UiVerifyTimeoutMs,
            ) != null
    }

/** @return the label that was clicked, or `null` when no candidate was on screen. */
private fun MacrobenchmarkScope.clickFirstMatching(vararg needles: String): String? {
    for (needle in needles) {
        if (clickFirst(By.textContains(needle))) return needle
        if (clickFirst(By.descContains(needle))) return needle
    }
    return null
}

private fun MacrobenchmarkScope.clickFirst(selector: BySelector): Boolean {
    val node =
        device.wait(Until.findObject(selector), BenchmarkConfig.UiWaitTimeoutMs) ?: return false
    node.click()
    device.waitForIdle()
    return true
}
