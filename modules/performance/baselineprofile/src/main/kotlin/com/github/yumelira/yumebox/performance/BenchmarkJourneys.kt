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
