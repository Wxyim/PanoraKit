package com.github.yumelira.yumebox.startup

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.yumelira.yumebox.common.util.StorageCleanupManager
import org.koin.core.context.GlobalContext

class StorageCleanupWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val koin = GlobalContext.getOrNull() ?: return Result.retry()
        val manager = koin.get<StorageCleanupManager>()
        return runCatching {
                manager.runAutoCleanupIfNeeded()
                Result.success()
            }
            .getOrElse { Result.retry() }
    }
}
