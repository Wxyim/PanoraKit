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
 *
 */

package com.github.nomadboxlab.monadbox.service.runtime.util

import android.os.Looper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

fun <T> runSuspendBlocking(block: suspend () -> T): T {
    val result = CompletableFuture<T>()
    val job =
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { block() }
                .onSuccess(result::complete)
                .onFailure(result::completeExceptionally)
        }

    return try {
        result.get()
    } catch (error: InterruptedException) {
        job.cancel()
        Thread.currentThread().interrupt()
        throw IllegalStateException("Blocking coroutine bridge interrupted", error)
    } catch (error: ExecutionException) {
        throw (error.cause ?: error)
    }
}

private fun waitForJobCompletionBlocking(job: Job) {
    val completion = CompletableFuture<Unit>()
    job.invokeOnCompletion { error ->
        if (error != null && error !is kotlinx.coroutines.CancellationException) {
            completion.completeExceptionally(error)
        } else {
            completion.complete(Unit)
        }
    }

    try {
        completion.get()
    } catch (error: InterruptedException) {
        Thread.currentThread().interrupt()
        throw IllegalStateException("Job cancellation interrupted", error)
    } catch (error: ExecutionException) {
        throw (error.cause ?: error)
    }
}

fun CoroutineScope.cancelAndJoinBlocking() {
    val scope = this
    val job = scope.coroutineContext.job

    job.cancel()

    if (Looper.myLooper() == Looper.getMainLooper()) {
        return
    }

    waitForJobCompletionBlocking(job)
}
