package com.github.yumelira.yumebox.startup

internal fun shouldInitializeDeferredStartup(
    initialSetupCompleted: Boolean,
    alreadyInitialized: Boolean,
): Boolean = initialSetupCompleted && !alreadyInitialized

internal fun isStaleTempDownloadCandidate(
    fileName: String,
    isRegularFile: Boolean,
    lastModifiedAt: Long,
    now: Long,
    staleAfterMs: Long,
): Boolean {
    return isRegularFile &&
        fileName.startsWith("temp_") &&
        fileName.endsWith(".yaml") &&
        now - lastModifiedAt >= staleAfterMs
}
