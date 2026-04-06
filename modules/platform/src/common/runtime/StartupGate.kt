/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */



package com.github.yumelira.yumebox.common.runtime

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import com.android.apksig.ApkVerifier
import com.github.yumelira.yumebox.core.android.BuildConfig
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.File
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import kotlin.system.exitProcess

object StartupGate {
    private const val PREFS_NAME = "startup_gate"
    private const val VERIFIED_STAMP_KEY = "verified_stamp"

    fun verify(application: Application) {
        val isDebuggable =
            (application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val strict = BuildConfig.STARTUP_GATE_STRICT
        val requireSigner = strict || BuildConfig.STARTUP_GATE_ENFORCE_SIGNER
        val requireApkV2 = strict || BuildConfig.STARTUP_GATE_ENFORCE_APK_V2
        if (isVerificationCached(application)) {
            return
        }
        runCatching {
            val ctx = application.applicationContext
            if (!checkPkg(ctx.packageName, BuildConfig.STARTUP_GATE_EXPECTED_PACKAGE)) die()
            if (!checkAppClass(ctx::class.java.name, BuildConfig.STARTUP_GATE_EXPECTED_APP_CLASS)) die()
            if (!checkAppParent(ctx::class.java.superclass?.name, BuildConfig.STARTUP_GATE_EXPECTED_APP_PARENT)) die()
            if (!checkSigner(
                    pm = application.packageManager,
                    packageName = ctx.packageName,
                    expectedFingerprint = BuildConfig.STARTUP_GATE_RELEASE_FINGERPRINT,
                    requireSigner = requireSigner,
                )
            ) die()
            if (!checkApkV2(
                    sourceDir = application.applicationInfo.sourceDir,
                    isDebuggable = isDebuggable,
                    requireApkV2 = requireApkV2,
                )
            ) die()
            cacheVerification(application)
        }.getOrElse { throwable ->
            if (isDebuggable) {
                Timber.e(throwable, "startup gate failed")
            }
            die()
        }
    }

    fun loadPrimary() {
        // Legacy guard library loading has been retired. Native runtime now
        // flows through the rebuildable bridge/override/clash libraries.
    }

    private fun checkPkg(actual: String, expected: String): Boolean = actual == expected

    private fun checkApkV2(sourceDir: String?, isDebuggable: Boolean, requireApkV2: Boolean): Boolean {
        if (!requireApkV2) return true
        if (sourceDir.isNullOrBlank()) return false
        return runCatching {
            ApkVerifier.Builder(File(sourceDir)).build().verify().isVerifiedUsingV2Scheme
        }.getOrElse { error ->
            if (isDebuggable) {
                Timber.w(error, "startup gate apk v2 verification failed")
            }
            false
        }
    }

    private fun checkSigner(
        pm: PackageManager,
        packageName: String,
        expectedFingerprint: String,
        requireSigner: Boolean,
    ): Boolean {
        if (!requireSigner) return true
        val digests = getSignerSha256(pm, packageName)
        if (digests.isEmpty()) return false
        val isDebuggable = isDebuggablePackage(pm, packageName)
        if (isDebuggable) return true
        if (expectedFingerprint.isBlank()) return false
        return digests.any { it == expectedFingerprint }
    }

    private fun isDebuggablePackage(pm: PackageManager, packageName: String): Boolean {
        val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getApplicationInfo(packageName, 0)
        }
        return (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    private fun getSignerSha256(pm: PackageManager, packageName: String): List<String> {
        val pkg = runCatching { getPackageInfoCompat(pm, packageName) }.getOrNull() ?: return emptyList()
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pkg.signingInfo?.apkContentsSigners?.map { it.toByteArray() }.orEmpty()
        } else {
            @Suppress("DEPRECATION")
            pkg.signatures?.map { it.toByteArray() }.orEmpty()
        }
        if (signatures.isEmpty()) return emptyList()

        val md = MessageDigest.getInstance("SHA-256")
        val certFactory = CertificateFactory.getInstance("X.509")
        return signatures.map { raw ->
            val cert = certFactory.generateCertificate(ByteArrayInputStream(raw)) as X509Certificate
            val digest = md.digest(cert.encoded)
            digest.joinToString(":") { byte -> "%02X".format(Locale.US, byte.toInt() and 0xFF) }
        }
    }

    private fun getPackageInfoCompat(pm: PackageManager, packageName: String): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong()),
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        }
    }

    private fun checkAppClass(actual: String?, expected: String): Boolean = actual == expected

    private fun checkAppParent(actual: String?, expected: String): Boolean = actual == expected

    private fun isVerificationCached(application: Application): Boolean {
        val stamp = buildVerificationStamp(application) ?: return false
        val prefs = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE)
        return prefs.getString(VERIFIED_STAMP_KEY, null) == stamp
    }

    private fun cacheVerification(application: Application) {
        val stamp = buildVerificationStamp(application) ?: return
        application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE)
            .edit()
            .putString(VERIFIED_STAMP_KEY, stamp)
            .apply()
    }

    private fun buildVerificationStamp(application: Application): String? {
        val sourceDir = application.applicationInfo.sourceDir ?: return null
        val apkFile = File(sourceDir)
        val packageInfo = runCatching {
            getPackageInfoCompat(application.packageManager, application.packageName)
        }.getOrNull() ?: return null
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
        return listOf(
            application.packageName,
            versionCode,
            packageInfo.lastUpdateTime,
            apkFile.length(),
            apkFile.lastModified(),
        ).joinToString(separator = ":")
    }

    private fun die(): Nothing {
        Process.killProcess(Process.myPid())
        exitProcess(-1)
    }
}
