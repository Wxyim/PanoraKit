package com.github.yumelira.yumebox.screen.settings

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.github.yumelira.yumebox.common.util.InstalledAppsAccess
import com.github.yumelira.yumebox.service.root.RootPackageShell
import dev.oom_wg.purejoy.mlang.MLang

data class AccessControlAppInfo(
    val packageName: String,
    val label: String,
    val isSystemApp: Boolean,
    val isChinaApp: Boolean,
    val isSelected: Boolean,
    val installTime: Long = 0L,
    val updateTime: Long = 0L,
)

enum class AccessControlSortMode {
    PACKAGE_NAME,
    LABEL,
    INSTALL_TIME,
    UPDATE_TIME;

    val displayName: String
        get() =
            when (this) {
                PACKAGE_NAME -> MLang.AccessControl.SortMode.PackageName
                LABEL -> MLang.AccessControl.SortMode.Label
                INSTALL_TIME -> MLang.AccessControl.SortMode.InstallTime
                UPDATE_TIME -> MLang.AccessControl.SortMode.UpdateTime
            }
}

@androidx.compose.runtime.Stable
data class AccessControlUiState(
    val isLoading: Boolean = true,
    val apps: List<AccessControlAppInfo> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val searchQuery: String = "",
    val showSystemApps: Boolean = false,
    val sortMode: AccessControlSortMode = AccessControlSortMode.LABEL,
    val selectedFirst: Boolean = true,
    val needsMiuiPermission: Boolean = false,
    val canBrowseApps: Boolean = true,
    val manualPackageName: String = "",
)

internal object AccessControlAppLoader {
    fun loadInstalledApps(
        application: Application,
        selectedPackages: Set<String>,
    ): List<AccessControlAppInfo> {
        val pm = application.packageManager
        val selfPackageName = application.packageName
        check(InstalledAppsAccess.resolve(application).canEnumerateInstalledApps) {
            "Installed app enumeration is unavailable on this device"
        }
        val packages =
            runCatching { pm.getInstalledApplications(PackageManager.GET_META_DATA) }
                .getOrElse { error ->
                    if (error is SecurityException) {
                        loadInstalledAppsFromRoot(pm, selfPackageName)
                    } else {
                        throw error
                    }
                }

        return packages
            .filter { it.packageName != selfPackageName }
            .map { appInfo ->
                val pkgInfo = runCatching { pm.getPackageInfo(appInfo.packageName, 0) }.getOrNull()
                AccessControlAppInfo(
                    packageName = appInfo.packageName,
                    label = appInfo.loadLabel(pm).toString(),
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                    isChinaApp = AccessControlClassifier.isChinaPackage(appInfo.packageName),
                    isSelected = selectedPackages.contains(appInfo.packageName),
                    installTime = pkgInfo?.firstInstallTime ?: 0L,
                    updateTime = pkgInfo?.lastUpdateTime ?: 0L,
                )
            }
    }

    private fun loadInstalledAppsFromRoot(
        pm: PackageManager,
        selfPackageName: String,
    ): List<ApplicationInfo> {
        val packageNames =
            RootPackageShell.queryInstalledPackageNames()
                ?: throw SecurityException("Unable to query installed packages from root shell")

        return packageNames
            .asSequence()
            .filterNot { it == selfPackageName }
            .mapNotNull { packageName ->
                runCatching { pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA) }
                    .getOrNull()
            }
            .toList()
    }
}

internal object AccessControlFilter {
    fun filterApps(
        apps: List<AccessControlAppInfo>,
        query: String,
        showSystemApps: Boolean,
        sortMode: AccessControlSortMode,
        selectedFirst: Boolean,
        descending: Boolean = false,
    ): List<AccessControlAppInfo> {
        val filtered =
            apps.filter { app ->
                val matchesQuery =
                    query.isEmpty() ||
                        app.label.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true)
                val matchesSystemFilter = showSystemApps || !app.isSystemApp
                matchesQuery && matchesSystemFilter
            }
        val comparator =
            when (sortMode) {
                AccessControlSortMode.PACKAGE_NAME ->
                    compareBy<AccessControlAppInfo> { it.packageName.lowercase() }
                AccessControlSortMode.LABEL -> compareBy { it.label.lowercase() }
                AccessControlSortMode.INSTALL_TIME -> compareBy { it.installTime }
                AccessControlSortMode.UPDATE_TIME -> compareBy { it.updateTime }
            }
        val sorted =
            if (descending) filtered.sortedWith(comparator.reversed())
            else filtered.sortedWith(comparator)
        return if (selectedFirst) sorted.sortedByDescending { it.isSelected } else sorted
    }
}

internal object AccessControlSelection {
    fun visiblePackages(state: AccessControlUiState): Set<String> {
        return AccessControlFilter.filterApps(
                apps = state.apps,
                query = state.searchQuery,
                showSystemApps = state.showSystemApps,
                sortMode = state.sortMode,
                selectedFirst = state.selectedFirst,
            )
            .mapTo(linkedSetOf()) { it.packageName }
    }

    fun updateSelection(state: AccessControlUiState, packages: Set<String>): AccessControlUiState {
        if (packages == state.selectedPackages) return state
        val newApps =
            state.apps.map { app ->
                val selected = app.packageName in packages
                if (app.isSelected == selected) app else app.copy(isSelected = selected)
            }
        return state.copy(selectedPackages = packages, apps = newApps)
    }

    fun normalizeManualPackageName(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return ""
        val packageRegex = Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z0-9_]+)+$")
        return trimmed.takeIf { packageRegex.matches(it) }.orEmpty()
    }
}

internal object AccessControlClassifier {
    private val skipPrefixList =
        listOf(
            "com.google",
            "com.android.chrome",
            "com.android.vending",
            "com.microsoft",
            "com.apple",
            "com.zhiliaoapp.musically",
            "com.android.providers.downloads",
        )

    private val chinaAppPrefixList =
        listOf(
            "com.tencent",
            "com.tencent.mobileqq",
            "com.tencent.mm",
            "com.tencent.qqlive",
            "com.tencent.news",
            "com.tencent.wework",
            "com.tencent.weishi",
            "com.tencent.karaoke",
            "com.tencent.qqmusic",
            "com.alibaba",
            "com.alibaba.android",
            "com.alibaba.wireless",
            "com.alibaba.rimet",
            "com.umeng",
            "com.qihoo",
            "com.ali",
            "com.alipay",
            "com.amap",
            "com.sina",
            "com.weibo",
            "com.sankuai",
            "com.sankuai.meituan",
            "com.sankuai.meituan.takeoutnew",
            "com.dianping",
            "com.jingdong",
            "com.xunmeng",
            "com.xingin",
            "com.zhihu",
            "com.bilibili",
            "com.coolapk",
            "tv.danmaku",
            "com.kuaishou",
            "com.smile.gifmaker",
            "com.ss.android",
            "com.ss.android.ugc",
            "com.ss.android.article",
            "com.qiyi",
            "com.youku",
            "com.youku.phone",
            "com.sohu",
            "com.autonavi",
            "com.sogou",
            "com.sogou.inputmethod",
            "com.iflytek",
            "com.iflytek.inputmethod",
            "com.kingsoft",
            "com.qzone",
            "com.vivo",
            "com.xiaomi",
            "com.huawei",
            "com.taobao",
            "com.taobao.idlefish",
            "com.secneo",
            "s.h.e.l.l",
            "com.stub",
            "com.kiwisec",
            "com.secshell",
            "com.wrapper",
            "cn.securitystack",
            "com.mogosec",
            "com.secoen",
            "com.netease",
            "com.mx",
            "com.qq.e",
            "com.baidu",
            "com.bytedance",
            "com.bugly",
            "com.miui",
            "com.oppo",
            "com.coloros",
            "com.iqoo",
            "com.meizu",
            "com.gionee",
            "com.oplus",
            "andes.oplus",
            "com.unionpay",
        )

    private val chinaAppRegex by lazy {
        ("(" + chinaAppPrefixList.joinToString("|").replace(".", "\\.") + ").*").toRegex()
    }

    fun isChinaPackage(packageName: String): Boolean {
        val normalized = packageName.lowercase()
        skipPrefixList.forEach {
            if (normalized == it || normalized.startsWith("$it.")) {
                return false
            }
        }
        if (
            normalized.startsWith("cn.") ||
                normalized.contains(".cn.") ||
                normalized.endsWith(".cn")
        ) {
            return true
        }
        return normalized.matches(chinaAppRegex)
    }
}
