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

import android.os.SystemClock
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

// Labels are matched against both English and Chinese resources; every journey reports the label it
// matched (or the full candidate list when it matched nothing) so that a reworded UI shows up in
// the
// generator report instead of silently dropping coverage.
private val ConfigTabLabels = listOf("Config", "Profiles", "配置", "订阅")
private val SettingsTabLabels = listOf("Settings", "设置")
private val AddProfileLabels =
    listOf("Add Profile", "Add profile", "添加配置", "新增配置", "添加", "新增", "Add")
private val ProfileTypePickerLabels = listOf("Profile Type", "配置类型")
private val BlankConfigSourceLabels = listOf("Blank Config", "空白配置")
private val ConfirmLabels = listOf("Confirm", "确定", "确认")
private val CancelLabels = listOf("Cancel", "取消")

// Surfaces that only the Profiles page shows. "Profiles" is the page title, the other entries are
// its empty state; the bottom-bar tab is deliberately absent because it is labelled "Config" and is
// on screen for every page, which would make the check useless.
private val ProfilesSurfaceLabels =
    listOf("No profiles", "Click top-right", "Profiles", "暂无配置文件", "点击右上角")

// Surfaces of the first-run wizard. A clean install walks it before the main shell exists, and only
// its last step writes `initialSetupCompleted`, so every Home-facing journey below depends on it.
//
// The opening step is a titleless hero: its only stable node is the round "Start" arrow, whose
// content description is `MLang.Component.Button.Start` and which only appears once its
// `RevealScaleBlock` fires (680ms in). Both the description and the typewriter wordmark are listed
// here or that first step reads as "no wizard at all", `onboardingJourney` bails before tapping
// anything, and a clean-install collection stops at `OnboardingStartupActivity` without ever
// reaching the shell.
private val OnboardingSurfaceLabels =
    listOf(
        "Start",
        "开始",
        // `StartupTypewriterPhrases`: falls back to the hero wordmark when the arrow's node is not
        // exposed (it sits under an `AndroidView` hit target).
        "MonadBox",
        "Hello Word",
        "Ready to Go",
        "Confirm Runtime Access",
        "Confirm Privacy Notice",
        "Tune the Interface",
        "GitHub Repo",
        "准备完成",
        "确认运行权限",
        "确认隐私说明",
        "调整界面风格",
        "GitHub 仓库",
    )
private val OnboardingAdvanceLabels =
    listOf("Next", "Enter App", "Start Setup", "Start", "下一步", "进入应用", "开始设置", "开始")

/** Step titles that carry the "I accept the privacy notice" checkbox. */
private val PrivacyNoticeSurfaceLabels = listOf("Confirm Privacy Notice", "确认隐私说明")

// Bottom-bar labels. They exist in the main shell only, which is what makes them a usable witness
// that the wizard has actually been left behind.
private val MainShellLabels = listOf("Home", "首页")
private val StartControlLabels = listOf("Tap to start", "Start", "VPN", "TUN", "HTTP", "点击启动", "启动")
private val StopControlLabels = listOf("Running", "Stop", "VPN", "TUN", "HTTP", "运行", "停止")
private val EditEntryLabels = listOf("Edit", "Open config", "Edit settings", "编辑", "打开配置", "编辑设置")
private val OpenConfigLabels = listOf("Open config", "Edit", "打开配置", "编辑")
private val SaveLabels = listOf("Save", "Save and exit", "保存", "保存并退出")

/**
 * Add-sheet steps compose more slowly than a plain surface probe on the software-rendered CI
 * emulator, so each one gets a longer budget than [BenchmarkConfig.UiVerifyTimeoutMs].
 */
private const val SheetStepTimeoutMs = 6_000L

/**
 * A mode switch always answers through the toast dialog host: the "switched" info dialog when a
 * profile exists, or the switch-failed/`ProfileNotExist` error dialog on a clean install. The error
 * branch only surfaces after `HomeRoute.MODE_SWITCH_ERROR_DELAY_MS`, so waiting for it needs more
 * budget than a plain surface probe.
 */
private const val DialogFeedbackTimeoutMs = 4_000L

/**
 * Budget for recognising the first wizard step.
 *
 * It is longer than `BenchmarkConfig.UiWaitTimeoutMs` because the opening hero reveals its start
 * arrow 680ms in (`RevealScaleBlock`), so for the first ~700ms of the page there is no node to
 * recognise at all.
 */
private const val OnboardingSurfaceTimeoutMs = 6_000L

/**
 * Let a freshly revealed control settle before tapping it.
 *
 * The start arrow is revealed 680ms in and then faded/scaled in over `RevealDurationMs`; a tap that
 * lands while that is still running is swallowed, and the journey used to record the tap as taken
 * and then stall on the same control until the step budget ran out.
 */
private const val OnboardingTapSettleMs = 800L

/** Attempts/backoff for re-tapping a control that is only used by a single wizard step. */
private const val OnboardingTapAttempts = 4
private const val OnboardingTapRetryMs = 400L

/** Attempts for ticking the terms step's privacy checkbox before giving up on that step. */
private const val OnboardingCheckboxAttempts = 4

/**
 * The wizard's start arrow, which exists on the opening step only.
 *
 * That makes "the control is gone" a usable progress check for it: it is the one advancement whose
 * label is not shared with the following step, so a swallowed tap can be detected and retried.
 */
private val OnboardingStartControlLabels = listOf("Start", "开始")

private const val LabelPollIntervalMs = 100L

/** The wizard is five steps; the extra iterations cover a stray tap that did not advance one. */
private const val MaxOnboardingSteps = 8

// The routing-mode names, in the order the drop-down lists them. The badge and the drop-down
// entries render the same strings, so the journey below tells them apart by *where* the string
// lives (description vs. text) rather than by the string itself.
private val ModeSwitchLabels = listOf("Rule", "Direct", "Global", "规则分流", "直连", "全局代理")

internal fun MacrobenchmarkScope.startupJourney(): JourneyResult {
    pressHome()
    startActivityAndWait()
    val windowVisible = appWindowVisible(BenchmarkConfig.StartupTimeoutMs)
    device.waitForIdle()
    return if (windowVisible) {
        // The surface digest separates "the app is up" from "the app is up on the right screen",
        // which is otherwise indistinguishable in the report when the first-run wizard is showing.
        JourneyResult.exercised(
            "${BenchmarkConfig.TargetPackage} window visible; ${describeSurface(limit = 8)}"
        )
    } else {
        JourneyResult.skipped(
            "${BenchmarkConfig.TargetPackage} window not visible within ${BenchmarkConfig.StartupTimeoutMs}ms"
        )
    }
}

/**
 * Walk the first-run wizard when the install starts on it.
 *
 * A clean install hands `MainActivity` off to the wizard while `initialSetupCompleted` is false,
 * and nothing else in this block can reach Home until the wizard is finished — every Home-facing
 * journey then reports "surface not found" instead of exercising anything. The wizard's last step
 * is what writes the flag, so walking it also puts the real first-run path into the sampled
 * profile. When the app starts past the wizard this is a no-op.
 */
internal fun MacrobenchmarkScope.onboardingJourney(): JourneyResult {
    requireAppOnScreen()?.let {
        return it
    }

    // An install that is already set up starts in the main shell: nothing to walk, and the probe is
    // non-blocking so a fresh install does not pay a timeout before the wizard shows up.
    if (awaitAnyLabel(MainShellLabels, 0L) != null) {
        return JourneyResult.exercised("no wizard surface; install starts past onboarding")
    }

    // The opening step has no title and reveals its only control 680ms in, so detection gets a
    // longer budget than a plain surface probe and falls back to the advance controls: a step whose
    // labels are reworded must not read as "no wizard at all", which is how collection used to stop
    // at `OnboardingStartupActivity` and never reach the shell.
    val surface =
        awaitAnyLabel(OnboardingSurfaceLabels, OnboardingSurfaceTimeoutMs)
            ?: awaitAnyLabel(OnboardingAdvanceLabels, 0L)
            ?: return JourneyResult.exercised("no wizard surface; install starts past onboarding")

    val steps = mutableListOf<String>()
    repeat(MaxOnboardingSteps) {
        if (awaitMainShell()) {
            return JourneyResult.exercised("wizard '$surface' -> ${steps.joinToString(" -> ")}")
        }

        // The terms step keeps its primary action disabled until the notice is accepted.
        acceptPrivacyNoticeIfOffered()

        val control =
            awaitAnyLabel(OnboardingAdvanceLabels, BenchmarkConfig.UiWaitTimeoutMs)
                ?: return JourneyResult.skipped(
                    "wizard stalled on '$surface': none of $OnboardingAdvanceLabels is present; ${describeSurface()}"
                )
        SystemClock.sleep(OnboardingTapSettleMs)
        val advanced =
            if (control in OnboardingStartControlLabels) {
                tapUntilGone(control)
            } else {
                tapFirstLabel(control)
            }
        if (!advanced) {
            return JourneyResult.skipped(
                "wizard control '$control' did not advance the step; ${describeSurface()}"
            )
        }
        device.waitForIdle()
        steps += control
    }

    return if (awaitMainShell()) {
        JourneyResult.exercised("wizard '$surface' -> ${steps.joinToString(" -> ")}")
    } else {
        JourneyResult.skipped(
            "wizard '$surface' did not reach the main shell in $MaxOnboardingSteps steps (${steps.joinToString(" -> ")}); ${describeSurface()}"
        )
    }
}

/**
 * Tick the privacy notice checkbox when the current wizard step offers one.
 *
 * The terms step disables its primary action until the notice is accepted, and only the checkbox
 * node itself carries the toggle action, so it has to be addressed as a checkable node.
 *
 * Every attempt re-finds the node and the tick is verified afterwards: the wizard re-renders as it
 * is walked, so a handle can go stale between the search and the read, and a single stale read used
 * to be treated as "nothing left to accept" - which left the walk stalled on this step clicking a
 * disabled "Next" until the step budget ran out.
 */
private fun MacrobenchmarkScope.acceptPrivacyNoticeIfOffered() {
    if (awaitAnyLabel(PrivacyNoticeSurfaceLabels, timeoutMs = 0L) == null) return
    repeat(OnboardingCheckboxAttempts) {
        if (acceptPrivacyNoticeOnce()) return
        SystemClock.sleep(OnboardingTapRetryMs)
    }
}

/** @return `true` once the notice is accepted (also when there is no checkbox on this step). */
private fun MacrobenchmarkScope.acceptPrivacyNoticeOnce(): Boolean {
    val checkbox =
        device.findObject(appSelector(By.checkable(true)))
            // Compose publishes the role as the platform class name, so this still finds the same
            // node if a build maps the toggleable state without the checkable flag.
            ?: device.findObject(appSelector(By.clazz(CheckboxClassName)))
            ?: return false

    val checked = staleSafe { checkbox.isChecked }
    if (checked == true) return true

    if (!clickNode(checkbox) && !tapFirstMatch(By.checkable(true))) {
        return false
    }
    device.waitForIdle()

    // Re-find before reading: the handle from the search above can be stale by now.
    val accepted =
        device.findObject(appSelector(By.checkable(true)))
            ?: device.findObject(appSelector(By.clazz(CheckboxClassName)))
    return staleSafe { accepted?.isChecked } ?: false
}

/** `Role.Checkbox` as Compose reports it to the platform accessibility tree. */
private const val CheckboxClassName = "android.widget.CheckBox"

private fun MacrobenchmarkScope.awaitMainShell(): Boolean =
    awaitAnyLabel(MainShellLabels, BenchmarkConfig.UiVerifyTimeoutMs) != null

internal fun MacrobenchmarkScope.openNextMainPage() {
    val midY = device.displayHeight / 2
    device.swipe(device.displayWidth * 4 / 5, midY, device.displayWidth / 5, midY, 24)
    device.waitForIdle()
}

/**
 * Swipe to the neighbouring main page.
 *
 * The swipe is the coverage this leg exists for, so its verdict only has to say that there was an
 * app on screen to swipe on: a swipe on the launcher would otherwise be recorded as a covered
 * journey.
 */
internal fun MacrobenchmarkScope.nextMainPageJourney(): JourneyResult {
    requireAppOnScreen()?.let {
        return it
    }
    openNextMainPage()
    return JourneyResult.exercised("swiped to the neighbouring main page")
}

/** Navigate through bottom navigation tabs (Profiles, Settings) and back to Home. */
internal fun MacrobenchmarkScope.bottomNavigationJourney(): JourneyResult {
    requireAppOnScreen()?.let {
        return it
    }

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

/**
 * Open the Home routing-mode drop-down and switch through it.
 *
 * This is the panel whose exit animation used to overlap the feedback of a failed mode switch on a
 * cold start (see `HomeRoute.MODE_SWITCH_ERROR_DELAY_MS`), and it is the only place the overlay
 * composables are built. The badge and the drop-down entries render the same mode names, so they
 * are told apart by where the string lives:
 * 1. while the panel is closed the badge is the only node carrying a mode name in its
 *    contentDescription, so it can be opened by description;
 * 2. the entries carry the name as plain text only, and the name the badge currently shows is
 *    skipped, so the entry lookup can never land back on the badge behind the scrim.
 */
internal fun MacrobenchmarkScope.homeModeSwitchJourney(): JourneyResult {
    requireAppOnScreen()?.let {
        return it
    }

    navigateToHomePage()

    val badgeLabel =
        firstMatchingDescription(ModeSwitchLabels)
            ?: return JourneyResult.skipped("no mode badge among $ModeSwitchLabels")
    if (!clickFirst(By.descContains(badgeLabel)) && !tapFirstMatch(By.descContains(badgeLabel))) {
        return JourneyResult.skipped("mode badge '$badgeLabel' was not clickable")
    }
    device.waitForIdle()

    val entry =
        ModeSwitchLabels.firstOrNull { label ->
            label != badgeLabel &&
                device.wait(
                    Until.hasObject(By.textContains(label)),
                    BenchmarkConfig.UiWaitTimeoutMs,
                ) != null
        }
            ?: return JourneyResult.skipped(
                "mode panel '$badgeLabel' opened, but listed no other mode among $ModeSwitchLabels"
            )

    if (!clickFirst(By.textContains(entry))) {
        return JourneyResult.skipped("mode entry '$entry' was not clickable")
    }
    device.waitForIdle()

    // Switching the routing mode always ends in a toast dialog: an info dialog on success, the
    // no-profile/switch-failed error dialog otherwise. Waiting for it is what puts the
    // `SuperDialog` -> `DialogLayout` -> `DialogEntry` -> `DialogContentLayout` stack into the
    // sampled profile; composing it cold, mid enter-animation, is the first-open jank, and
    // `waitForIdle` alone returns before the delayed error branch appears.
    val dialogConfirm = awaitAnyLabel(ConfirmLabels, DialogFeedbackTimeoutMs)
    if (dialogConfirm != null) {
        clickFirstLabel(dialogConfirm)
        device.waitForIdle()
    }

    val dialogDetail =
        if (dialogConfirm != null) {
            "; toast dialog dismissed via '$dialogConfirm'"
        } else {
            "; no toast dialog surfaced among $ConfirmLabels"
        }
    return JourneyResult.exercised(
        "mode badge '$badgeLabel' expanded, selected '$entry'$dialogDetail"
    )
}

/** Scroll settings list to exercise lazy list composition. */
internal fun MacrobenchmarkScope.settingsScrollJourney(): JourneyResult {
    requireAppOnScreen()?.let {
        return it
    }

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

/**
 * Exercise the profile import surface without a fixture file, a file picker, or the network.
 *
 * The CI install has no profile, so there is no edit target either and the Profiles page add action
 * is the only reachable import entry. The sheet opens on the online "Subscription URL" source,
 * which this journey switches to the offline "Blank Config" source: that path writes a profile and
 * lands in the config editor, so add -> pick source -> confirm -> edit all end up in the sampled
 * profile. When the source picker cannot be driven the journey falls back to confirming the default
 * source, which still exercises the sheet and its validation branch, and says so in its detail.
 */
internal fun MacrobenchmarkScope.configurationImportJourney(): JourneyResult {
    requireAppOnScreen()?.let {
        return it
    }

    navigateToProfilesPage()

    val addEntry =
        awaitAnyLabel(AddProfileLabels, SheetStepTimeoutMs)
            ?: return JourneyResult.skipped(
                "no add-profile entry among $AddProfileLabels; ${describeSurface()}"
            )
    if (!clickFirstLabel(addEntry)) {
        return JourneyResult.skipped(
            "add-profile entry '$addEntry' was not tappable; ${describeSurface()}"
        )
    }
    device.waitForIdle()

    // The confirm control proves the add surface really opened; it also drives the import on every
    // path below.
    val confirm =
        awaitAnyLabel(ConfirmLabels, SheetStepTimeoutMs)
            ?: return JourneyResult.skipped(
                "add-profile '$addEntry' opened no confirm control among $ConfirmLabels; ${describeSurface()}"
            )

    val blankSource = selectBlankConfigSource()
    if (!clickFirstLabel(confirm)) {
        dismissTransientSurface()
        return JourneyResult.skipped("confirm '$confirm' was not tappable; ${describeSurface()}")
    }
    device.waitForIdle()

    if (awaitAnyLabel(SaveLabels, SheetStepTimeoutMs) != null) {
        // A blank profile opens straight into the config editor; leave it so the next journey
        // starts
        // from the Profiles surface again.
        device.pressBack()
        device.waitForIdle()
        return JourneyResult.exercised(
            "add profile '$addEntry' -> source '${blankSource ?: "default"}' -> confirm '$confirm' -> editor"
        )
    }

    // No editor: either the picker could not be driven (the default source then hits its validation
    // branch) or the confirm tap was swallowed. The sheet is still up in both cases.
    val sheetStillOpen = awaitAnyLabel(ConfirmLabels, BenchmarkConfig.UiWaitTimeoutMs) != null
    if (clickFirstMatching(*CancelLabels.toTypedArray()) == null) dismissTransientSurface()

    return when {
        blankSource == null && sheetStillOpen ->
            JourneyResult.exercised(
                "add profile '$addEntry' -> confirm '$confirm' exercised the sheet without the $ProfileTypePickerLabels source picker"
            )
        blankSource == null ->
            JourneyResult.skipped(
                "add-profile '$addEntry' produced no confirmable sheet and no source picker; ${describeSurface()}"
            )
        else ->
            JourneyResult.skipped(
                "add profile '$addEntry' -> source '$blankSource' -> confirm '$confirm' reached no editor; ${describeSurface()}"
            )
    }
}

/** Exercise the primary Home start/stop control when the runtime can be toggled in this install. */
internal fun MacrobenchmarkScope.startStopProxyJourney(): JourneyResult {
    requireAppOnScreen()?.let {
        return it
    }

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
    requireAppOnScreen()?.let {
        return it
    }

    navigateToProfilesPage()
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

/**
 * Bring the Profiles surface to the front.
 *
 * The bottom-bar tab is tried first because it jumps straight to the page; the swipe path covers
 * the case where it is not tappable (an auto-hidden bar disables its tabs). Both paths are
 * idempotent, so running them in sequence always lands on the same page.
 */
private fun MacrobenchmarkScope.navigateToProfilesPage() {
    if (clickFirstMatching(*ConfigTabLabels.toTypedArray()) != null && awaitProfilesSurface())
        return

    navigateToHomePage()
    repeat(2) { openNextMainPage() }
    awaitProfilesSurface()
}

private fun MacrobenchmarkScope.awaitProfilesSurface(): Boolean =
    awaitAnyLabel(ProfilesSurfaceLabels, BenchmarkConfig.UiVerifyTimeoutMs) != null

/**
 * Switch the add sheet from its default online source to the offline "Blank Config" entry.
 *
 * The source lives behind a window spinner: the row displays the selected source and opens the
 * candidate list when tapped. The row *title* is used as the handle rather than the source name,
 * because the sheet's URL field carries a very similar "Subscription URL" text.
 *
 * @return the matched source label, or `null` when the picker could not be driven.
 */
private fun MacrobenchmarkScope.selectBlankConfigSource(): String? {
    val picker =
        awaitAnyLabel(ProfileTypePickerLabels, BenchmarkConfig.UiWaitTimeoutMs) ?: return null
    if (!clickFirstLabel(picker)) return null
    device.waitForIdle()

    val source = awaitAnyLabel(BlankConfigSourceLabels, BenchmarkConfig.UiWaitTimeoutMs)
    if (source == null || !clickFirstLabel(source)) {
        // Put the picker back the way it was instead of leaving a dialog in front of the sheet.
        device.pressBack()
        device.waitForIdle()
        return null
    }
    device.waitForIdle()
    return source
}

private fun MacrobenchmarkScope.dismissTransientSurface() {
    device.pressBack()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.dismissPermissionOrErrorSurface() {
    // The consent dialogs belong to the system UI, so this one deliberately reaches outside the
    // target package.
    clickFirstMatching("Cancel", "Deny", "Not now", "OK", "取消", "拒绝", "暂不", "确定", appOnly = false)
}

private fun MacrobenchmarkScope.firstMatchingLabel(needles: List<String>): String? =
    needles.firstOrNull { needle ->
        device.wait(
            Until.hasObject(appSelector(By.textContains(needle))),
            BenchmarkConfig.UiVerifyTimeoutMs,
        ) != null ||
            device.wait(
                Until.hasObject(appSelector(By.descContains(needle))),
                BenchmarkConfig.UiVerifyTimeoutMs,
            ) != null
    }

/**
 * Like [firstMatchingLabel], but only the content description is searched. The mode badge is
 * addressable this way; matching it by text would also match the drop-down entry of the same name.
 */
private fun MacrobenchmarkScope.firstMatchingDescription(needles: List<String>): String? =
    needles.firstOrNull { needle ->
        device.wait(
            Until.hasObject(appSelector(By.descContains(needle))),
            BenchmarkConfig.UiVerifyTimeoutMs,
        ) != null
    }

private fun appSelector(selector: BySelector): BySelector =
    selector.pkg(BenchmarkConfig.TargetPackage)

/**
 * @return the verdict for a leg that needs the app up while it is not, or `null` when it is.
 *
 * Every package-filtered probe also matches nodes the app *owns* while another window renders them
 * — the rows of its own notification, its launcher widget — and those outlived a force-stopped app,
 * which made whole legs report "exercised" with nothing running. A leg therefore proves the app is
 * the window in front before it touches the screen.
 */
private fun MacrobenchmarkScope.requireAppOnScreen(): JourneyResult? {
    val foreground = device.currentPackageName
    return if (foreground == BenchmarkConfig.TargetPackage && appWindowVisible()) {
        null
    } else {
        JourneyResult.skipped("app is not in front (foreground: $foreground); ${describeSurface()}")
    }
}

/**
 * Whether the app has a window of its own on screen.
 *
 * The foreground check in [requireAppOnScreen] rules out a leg running while the app is not even
 * alive; this one rules out the app being up but behind another window, where its nodes are still
 * in the tree of whatever is on top.
 */
private fun MacrobenchmarkScope.appWindowVisible(
    timeoutMs: Long = BenchmarkConfig.UiWaitTimeoutMs
): Boolean =
    device.wait(Until.hasObject(By.pkg(BenchmarkConfig.TargetPackage).depth(0)), timeoutMs) != null

/**
 * Wait for any of [needles] to appear as text or content description.
 *
 * [clickFirstMatching] gives every candidate its own [BenchmarkConfig.UiWaitTimeoutMs] wait; on the
 * software-rendered CI emulator that adds up, and a label that appears late then looks like a label
 * that never appeared. This waits once for the whole set and reports which entry matched.
 */
private fun MacrobenchmarkScope.awaitAnyLabel(needles: List<String>, timeoutMs: Long): String? {
    val deadline = SystemClock.uptimeMillis() + timeoutMs
    while (true) {
        needles
            .firstOrNull { needle ->
                device.findObject(appSelector(By.textContains(needle))) != null ||
                    device.findObject(appSelector(By.descContains(needle))) != null
            }
            ?.let {
                return it
            }
        if (SystemClock.uptimeMillis() >= deadline) return null
        SystemClock.sleep(LabelPollIntervalMs)
    }
}

/**
 * A compact list of the app's visible text and content descriptions.
 *
 * Journey reports only carry a detail string, so a skip reason that says what *was* on screen is
 * what turns "the entry was not found" into an actionable CI failure.
 */
private fun MacrobenchmarkScope.describeSurface(limit: Int = 12): String {
    val entries = LinkedHashSet<String>()
    // This digest is diagnostic only. The surface it samples is still settling when it is read
    // (the startup feature window in particular), so a node can be replaced between the tree walk
    // and the property read and UiAutomator answers with `StaleObjectException`. Dropping one
    // entry costs a little context in the report; letting it escape fails the journey that is
    // being described, which is what made a required startup leg fail while the app was healthy.
    for (node in surfaceNodes()) {
        val labels = staleSafe { listOfNotNull(node.text, node.contentDescription) } ?: continue
        labels
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.length <= 64 }
            .forEach { entries += it }
        if (entries.size >= limit) break
    }
    return "surface=[${entries.take(limit).joinToString(" | ").ifEmpty { "<no app nodes>" }}]"
}

private fun MacrobenchmarkScope.surfaceNodes(): List<UiObject2> =
    staleSafe { device.findObjects(By.pkg(BenchmarkConfig.TargetPackage)) } ?: emptyList()

/**
 * Run [block], turning a node that went stale mid-read into `null`.
 *
 * UiAutomator hands out live handles: every property read and every tap goes back to the device,
 * and anything the app replaced in between (a re-rendered screen, a page transition, the emulator
 * dropping a frame) comes back as [StaleObjectException] instead of a value.
 */
private fun <T> staleSafe(block: () -> T): T? =
    try {
        block()
    } catch (_: StaleObjectException) {
        null
    }

/** @return `true` when the tap reached the device, `false` when [node] was replaced first. */
private fun clickNode(node: UiObject2): Boolean =
    try {
        node.click()
        true
    } catch (_: Exception) {
        // UiAutomator refuses to click a node that is still animating in or was replaced since the
        // search; `tapFirstMatch` re-finds it and taps the reported bounds instead, so treat every
        // refusal as "this attempt did not tap" rather than letting it fail the whole leg.
        false
    }

/**
 * Tap the centre of the first node matching [selector], by coordinate.
 *
 * Fallback for a node the tree reports but `UiObject2.click` cannot resolve: the Home mode badge
 * lives on a page that is still animating, and the accessibility search then reports it as unusable
 * even though `hasObject` saw it a moment earlier. Tapping the reported bounds does not depend on
 * the node staying live between the search and the tap.
 */
private fun MacrobenchmarkScope.tapFirstMatch(selector: BySelector): Boolean {
    val scoped = selector.pkg(BenchmarkConfig.TargetPackage)
    val nodes =
        device.wait(Until.findObjects(scoped), BenchmarkConfig.UiWaitTimeoutMs) ?: return false
    val bounds =
        nodes.firstNotNullOfOrNull { node ->
            staleSafe { node.visibleBounds }?.takeIf { it.width() > 0 && it.height() > 0 }
        } ?: return false
    device.click(bounds.centerX(), bounds.centerY())
    device.waitForIdle()
    return true
}

/** @return `true` when [label] was found (by text or description) and tapped. */
private fun MacrobenchmarkScope.clickFirstLabel(label: String): Boolean =
    clickFirst(By.textContains(label)) || clickFirst(By.descContains(label))

/**
 * Tap the first node matching [label], by node and then by its reported bounds.
 *
 * `UiObject2.click` reports a node that is still animating in as unusable even though the search
 * found it - the wizard's start arrow is faded/scaled in after a 680ms delay, so tapping it right
 * after it appears is exactly that case. Tapping the reported bounds does not depend on the node
 * staying usable between the search and the tap.
 */
private fun MacrobenchmarkScope.tapFirstLabel(label: String): Boolean =
    clickFirstLabel(label) ||
        tapFirstMatch(By.textContains(label)) ||
        tapFirstMatch(By.descContains(label))

/**
 * Tap [label] until it disappears, up to [OnboardingTapAttempts] times.
 *
 * Only valid for a control used by a single step (the start arrow): every other step's primary
 * action is also called "Next", so "still present" would not mean "the tap failed". The arrow is
 * faded/scaled in over `RevealDurationMs` starting 680ms in, and a tap that lands while it is still
 * animating is swallowed, so a single attempt can silently advance nothing.
 */
private fun MacrobenchmarkScope.tapUntilGone(label: String): Boolean {
    repeat(OnboardingTapAttempts) {
        if (tapFirstLabel(label)) {
            device.waitForIdle()
            if (awaitAnyLabel(listOf(label), 0L) == null) return true
        }
        SystemClock.sleep(OnboardingTapRetryMs)
    }
    return awaitAnyLabel(listOf(label), 0L) == null
}

/**
 * @return the label that was clicked, or `null` when no candidate was on screen.
 *
 * [appOnly] keeps the search inside the app's own windows; pass `false` only for the surfaces the
 * app hands off to (the system VPN/notification consent dialogs), which live in another package.
 */
private fun MacrobenchmarkScope.clickFirstMatching(
    vararg needles: String,
    appOnly: Boolean = true,
): String? {
    for (needle in needles) {
        if (clickFirst(By.textContains(needle), appOnly)) return needle
        if (clickFirst(By.descContains(needle), appOnly)) return needle
    }
    return null
}

/**
 * Tap the first node matching [selector].
 *
 * Selectors are constrained to [BenchmarkConfig.TargetPackage] by default: the accessibility tree
 * also carries windows from other packages (the launcher, the system Settings app), and matching
 * those made whole journeys report success while the app under test was still on its first-run
 * wizard.
 */
private fun MacrobenchmarkScope.clickFirst(selector: BySelector, appOnly: Boolean = true): Boolean {
    val scoped = if (appOnly) selector.pkg(BenchmarkConfig.TargetPackage) else selector
    // A tap races the screen it is tapped on, so a node replaced between the search and the tap is
    // searched for once more instead of aborting the leg that is driving the UI.
    repeat(2) {
        val node =
            device.wait(Until.findObject(scoped), BenchmarkConfig.UiWaitTimeoutMs) ?: return false
        if (clickNode(node)) {
            device.waitForIdle()
            return true
        }
    }
    return false
}
