package com.github.yumelira.yumebox.performance

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
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
