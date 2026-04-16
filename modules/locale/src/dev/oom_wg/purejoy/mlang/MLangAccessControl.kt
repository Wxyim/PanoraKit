@file:Suppress(
    "PackageDirectoryMismatch",
    "PackageName",
    "ClassName",
    "ObjectPropertyName",
    "PropertyName",
    "FunctionName",
    "NonAsciiCharacters",
    "RemoveRedundantBackticks",
    "REDUNDANT_ELSE_IN_WHEN",
    "UnusedExpression",
    "unused",
)

package dev.oom_wg.purejoy.mlang

import androidx.compose.runtime.Composable
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig
import dev.oom_wg.purejoy.fyl.fytxt.compose.observe
import dev.oom_wg.purejoy.fyl.fytxt.strfmt.fmt
import dev.oom_wg.purejoy.mlang.MLang.`MLangGroups` as RootMLangGroups
import dev.oom_wg.purejoy.mlang.MLang.`MLangTags` as RootMLangTags

object MLangAccessControl {
    init {
        RootMLangGroups
    }

    /** 访问控制 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Access Control"""
                    RootMLangTags.ZH -> """访问控制"""
                    else -> null
                }
            } ?: """访问控制"""

    /** 访问控制 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Search` {
        init {
            RootMLangGroups
        }

        /** 搜索应用... */
        val `Placeholder`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Search apps..."""
                        RootMLangTags.ZH -> """搜索应用..."""
                        else -> null
                    }
                } ?: """搜索应用..."""

        /** 搜索应用... */
        @Composable
        fun `Placeholder`(vararg args: Any?) = FYTxtConfig.observe { `Placeholder`.fmt(args) }

        /** 没有找到匹配的应用 */
        val `EmptyResults`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No matching apps found"""
                        RootMLangTags.ZH -> """没有找到匹配的应用"""
                        else -> null
                    }
                } ?: """没有找到匹配的应用"""

        /** 没有找到匹配的应用 */
        @Composable
        fun `EmptyResults`(vararg args: Any?) = FYTxtConfig.observe { `EmptyResults`.fmt(args) }
    }

    object `AppList` {
        init {
            RootMLangGroups
        }

        /** 应用列表 (%d 已选择) */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """App List (%d selected)"""
                        RootMLangTags.ZH -> """应用列表 (%d 已选择)"""
                        else -> null
                    }
                } ?: """应用列表 (%d 已选择)"""

        /** 应用列表 (%d 已选择) */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 加载中... */
        val `Loading`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Loading..."""
                        RootMLangTags.ZH -> """加载中..."""
                        else -> null
                    }
                } ?: """加载中..."""

        /** 加载中... */
        @Composable fun `Loading`(vararg args: Any?) = FYTxtConfig.observe { `Loading`.fmt(args) }

        /** 在授予设备权限前，暂时无法浏览已安装应用。 */
        val `BrowseUnavailablePermission`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Installed-app browsing is unavailable until the device permission is granted."""
                        RootMLangTags.ZH -> """在授予设备权限前，暂时无法浏览已安装应用。"""
                        else -> null
                    }
                } ?: """在授予设备权限前，暂时无法浏览已安装应用。"""

        /** 在授予设备权限前，暂时无法浏览已安装应用。 */
        @Composable
        fun `BrowseUnavailablePermission`(vararg args: Any?) =
            FYTxtConfig.observe { `BrowseUnavailablePermission`.fmt(args) }

        /** 当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。 */
        val `BrowseUnavailableManual`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Installed-app browsing is unavailable on this device build. You can still manage access control by importing or manually adding package names."""
                        RootMLangTags.ZH -> """当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。"""
                        else -> null
                    }
                } ?: """当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。"""

        /** 当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。 */
        @Composable
        fun `BrowseUnavailableManual`(vararg args: Any?) =
            FYTxtConfig.observe { `BrowseUnavailableManual`.fmt(args) }

        /** 打开权限设置 */
        val `OpenPermissionSettings`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Open permission settings"""
                        RootMLangTags.ZH -> """打开权限设置"""
                        else -> null
                    }
                } ?: """打开权限设置"""

        /** 打开权限设置 */
        @Composable
        fun `OpenPermissionSettings`(vararg args: Any?) =
            FYTxtConfig.observe { `OpenPermissionSettings`.fmt(args) }

        /** 无法打开权限设置页 */
        val `PermissionSettingsUnavailable`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unable to open the permission settings page"""
                        RootMLangTags.ZH -> """无法打开权限设置页"""
                        else -> null
                    }
                } ?: """无法打开权限设置页"""

        /** 无法打开权限设置页 */
        @Composable
        fun `PermissionSettingsUnavailable`(vararg args: Any?) =
            FYTxtConfig.observe { `PermissionSettingsUnavailable`.fmt(args) }

        /** 手动添加包名 */
        val `ManualAddTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add package name manually"""
                        RootMLangTags.ZH -> """手动添加包名"""
                        else -> null
                    }
                } ?: """手动添加包名"""

        /** 手动添加包名 */
        @Composable
        fun `ManualAddTitle`(vararg args: Any?) = FYTxtConfig.observe { `ManualAddTitle`.fmt(args) }

        /** com.example.app */
        val `ManualAddPlaceholder`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """com.example.app"""
                        RootMLangTags.ZH -> """com.example.app"""
                        else -> null
                    }
                } ?: """com.example.app"""

        /** com.example.app */
        @Composable
        fun `ManualAddPlaceholder`(vararg args: Any?) =
            FYTxtConfig.observe { `ManualAddPlaceholder`.fmt(args) }

        /** 添加包名 */
        val `AddPackage`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add package"""
                        RootMLangTags.ZH -> """添加包名"""
                        else -> null
                    }
                } ?: """添加包名"""

        /** 添加包名 */
        @Composable
        fun `AddPackage`(vararg args: Any?) = FYTxtConfig.observe { `AddPackage`.fmt(args) }

        /** 包名无效 */
        val `InvalidPackage`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Invalid package name"""
                        RootMLangTags.ZH -> """包名无效"""
                        else -> null
                    }
                } ?: """包名无效"""

        /** 包名无效 */
        @Composable
        fun `InvalidPackage`(vararg args: Any?) = FYTxtConfig.observe { `InvalidPackage`.fmt(args) }

        /** 已选包名 (%d) */
        val `SelectedPackagesTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Selected packages (%d)"""
                        RootMLangTags.ZH -> """已选包名 (%d)"""
                        else -> null
                    }
                } ?: """已选包名 (%d)"""

        /** 已选包名 (%d) */
        @Composable
        fun `SelectedPackagesTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `SelectedPackagesTitle`.fmt(args) }

        /** 当前还没有已选包名 */
        val `NoSelectedPackages`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No package names selected yet"""
                        RootMLangTags.ZH -> """当前还没有已选包名"""
                        else -> null
                    }
                } ?: """当前还没有已选包名"""

        /** 当前还没有已选包名 */
        @Composable
        fun `NoSelectedPackages`(vararg args: Any?) =
            FYTxtConfig.observe { `NoSelectedPackages`.fmt(args) }
    }

    object `Settings` {
        init {
            RootMLangGroups
        }

        /** 访问控制设置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Access Control Settings"""
                        RootMLangTags.ZH -> """访问控制设置"""
                        else -> null
                    }
                } ?: """访问控制设置"""

        /** 访问控制设置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 显示系统应用 */
        val `ShowSystemApps`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Show System Apps"""
                        RootMLangTags.ZH -> """显示系统应用"""
                        else -> null
                    }
                } ?: """显示系统应用"""

        /** 显示系统应用 */
        @Composable
        fun `ShowSystemApps`(vararg args: Any?) = FYTxtConfig.observe { `ShowSystemApps`.fmt(args) }

        /** 倒序排列 */
        val `DescendingOrder`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Descending Order"""
                        RootMLangTags.ZH -> """倒序排列"""
                        else -> null
                    }
                } ?: """倒序排列"""

        /** 倒序排列 */
        @Composable
        fun `DescendingOrder`(vararg args: Any?) =
            FYTxtConfig.observe { `DescendingOrder`.fmt(args) }

        /** 已选应用优先 */
        val `SelectedFirst`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Selected Apps First"""
                        RootMLangTags.ZH -> """已选应用优先"""
                        else -> null
                    }
                } ?: """已选应用优先"""

        /** 已选应用优先 */
        @Composable
        fun `SelectedFirst`(vararg args: Any?) = FYTxtConfig.observe { `SelectedFirst`.fmt(args) }

        /** 排序方式 */
        val `SortMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Sort Mode"""
                        RootMLangTags.ZH -> """排序方式"""
                        else -> null
                    }
                } ?: """排序方式"""

        /** 排序方式 */
        @Composable fun `SortMode`(vararg args: Any?) = FYTxtConfig.observe { `SortMode`.fmt(args) }

        /** 当前：%s */
        val `SortModeCurrent`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Current: %s"""
                        RootMLangTags.ZH -> """当前：%s"""
                        else -> null
                    }
                } ?: """当前：%s"""

        /** 当前：%s */
        @Composable
        fun `SortModeCurrent`(vararg args: Any?) =
            FYTxtConfig.observe { `SortModeCurrent`.fmt(args) }

        /** 批量操作 */
        val `BatchOperation`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Batch Operation"""
                        RootMLangTags.ZH -> """批量操作"""
                        else -> null
                    }
                } ?: """批量操作"""

        /** 批量操作 */
        @Composable
        fun `BatchOperation`(vararg args: Any?) = FYTxtConfig.observe { `BatchOperation`.fmt(args) }

        /** 对当前筛选结果中的应用批量操作 */
        val `BatchOperationSummaryBrowse`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Apply bulk actions to apps in the current filtered list"""
                        RootMLangTags.ZH -> """对当前筛选结果中的应用批量操作"""
                        else -> null
                    }
                } ?: """对当前筛选结果中的应用批量操作"""

        /** 对当前筛选结果中的应用批量操作 */
        @Composable
        fun `BatchOperationSummaryBrowse`(vararg args: Any?) =
            FYTxtConfig.observe { `BatchOperationSummaryBrowse`.fmt(args) }

        /** 对当前已选包名列表执行批量操作 */
        val `BatchOperationSummaryManual`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Run batch actions against the selected package list"""
                        RootMLangTags.ZH -> """对当前已选包名列表执行批量操作"""
                        else -> null
                    }
                } ?: """对当前已选包名列表执行批量操作"""

        /** 对当前已选包名列表执行批量操作 */
        @Composable
        fun `BatchOperationSummaryManual`(vararg args: Any?) =
            FYTxtConfig.observe { `BatchOperationSummaryManual`.fmt(args) }

        /** 全选 */
        val `SelectAll`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select All"""
                        RootMLangTags.ZH -> """全选"""
                        else -> null
                    }
                } ?: """全选"""

        /** 全选 */
        @Composable
        fun `SelectAll`(vararg args: Any?) = FYTxtConfig.observe { `SelectAll`.fmt(args) }

        /** 全不选 */
        val `DeselectAll`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Deselect All"""
                        RootMLangTags.ZH -> """全不选"""
                        else -> null
                    }
                } ?: """全不选"""

        /** 全不选 */
        @Composable
        fun `DeselectAll`(vararg args: Any?) = FYTxtConfig.observe { `DeselectAll`.fmt(args) }

        /** 反选 */
        val `Invert`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Invert"""
                        RootMLangTags.ZH -> """反选"""
                        else -> null
                    }
                } ?: """反选"""

        /** 反选 */
        @Composable fun `Invert`(vararg args: Any?) = FYTxtConfig.observe { `Invert`.fmt(args) }

        /** 复制已选 */
        val `CopySelected`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Copy Selected"""
                        RootMLangTags.ZH -> """复制已选"""
                        else -> null
                    }
                } ?: """复制已选"""

        /** 复制已选 */
        @Composable
        fun `CopySelected`(vararg args: Any?) = FYTxtConfig.observe { `CopySelected`.fmt(args) }

        /** 清空已选 */
        val `ClearSelected`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Clear Selected"""
                        RootMLangTags.ZH -> """清空已选"""
                        else -> null
                    }
                } ?: """清空已选"""

        /** 清空已选 */
        @Composable
        fun `ClearSelected`(vararg args: Any?) = FYTxtConfig.observe { `ClearSelected`.fmt(args) }

        /** 已清空 %d 个包名 */
        val `ClearSelectedResult`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cleared %d packages"""
                        RootMLangTags.ZH -> """已清空 %d 个包名"""
                        else -> null
                    }
                } ?: """已清空 %d 个包名"""

        /** 已清空 %d 个包名 */
        @Composable
        fun `ClearSelectedResult`(vararg args: Any?) =
            FYTxtConfig.observe { `ClearSelectedResult`.fmt(args) }

        /** 导入/导出 */
        val `ImportExport`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import/Export"""
                        RootMLangTags.ZH -> """导入/导出"""
                        else -> null
                    }
                } ?: """导入/导出"""

        /** 导入/导出 */
        @Composable
        fun `ImportExport`(vararg args: Any?) = FYTxtConfig.observe { `ImportExport`.fmt(args) }

        /** 导入时仅保留当前设备可识别的包名 */
        val `ImportExportSummaryBrowse`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Import keeps only package names recognized on this device"""
                        RootMLangTags.ZH -> """导入时仅保留当前设备可识别的包名"""
                        else -> null
                    }
                } ?: """导入时仅保留当前设备可识别的包名"""

        /** 导入时仅保留当前设备可识别的包名 */
        @Composable
        fun `ImportExportSummaryBrowse`(vararg args: Any?) =
            FYTxtConfig.observe { `ImportExportSummaryBrowse`.fmt(args) }

        /** 导入时保留格式有效的包名，导出当前已选包名 */
        val `ImportExportSummaryManual`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Import keeps package names with valid format and exports the current selected list"""
                        RootMLangTags.ZH -> """导入时保留格式有效的包名，导出当前已选包名"""
                        else -> null
                    }
                } ?: """导入时保留格式有效的包名，导出当前已选包名"""

        /** 导入时保留格式有效的包名，导出当前已选包名 */
        @Composable
        fun `ImportExportSummaryManual`(vararg args: Any?) =
            FYTxtConfig.observe { `ImportExportSummaryManual`.fmt(args) }

        /** 从剪贴板导入 */
        val `Import`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import from Clipboard"""
                        RootMLangTags.ZH -> """从剪贴板导入"""
                        else -> null
                    }
                } ?: """从剪贴板导入"""

        /** 从剪贴板导入 */
        @Composable fun `Import`(vararg args: Any?) = FYTxtConfig.observe { `Import`.fmt(args) }

        /** 导出到剪贴板 */
        val `Export`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Export to Clipboard"""
                        RootMLangTags.ZH -> """导出到剪贴板"""
                        else -> null
                    }
                } ?: """导出到剪贴板"""

        /** 导出到剪贴板 */
        @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

        /** 导入成功：%d 个包名 */
        val `ImportSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Imported: %d package names"""
                        RootMLangTags.ZH -> """导入成功：%d 个包名"""
                        else -> null
                    }
                } ?: """导入成功：%d 个包名"""

        /** 导入成功：%d 个包名 */
        @Composable
        fun `ImportSuccess`(vararg args: Any?) = FYTxtConfig.observe { `ImportSuccess`.fmt(args) }

        /** 导入 %d 行，新增 %d 个，忽略 %d 个 */
        val `ImportPartial`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Processed %d lines, added %d, ignored %d"""
                        RootMLangTags.ZH -> """导入 %d 行，新增 %d 个，忽略 %d 个"""
                        else -> null
                    }
                } ?: """导入 %d 行，新增 %d 个，忽略 %d 个"""

        /** 导入 %d 行，新增 %d 个，忽略 %d 个 */
        @Composable
        fun `ImportPartial`(vararg args: Any?) = FYTxtConfig.observe { `ImportPartial`.fmt(args) }

        /** 已复制 %d 个包名到剪贴板 */
        val `ExportSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Copied %d package names to clipboard"""
                        RootMLangTags.ZH -> """已复制 %d 个包名到剪贴板"""
                        else -> null
                    }
                } ?: """已复制 %d 个包名到剪贴板"""

        /** 已复制 %d 个包名到剪贴板 */
        @Composable
        fun `ExportSuccess`(vararg args: Any?) = FYTxtConfig.observe { `ExportSuccess`.fmt(args) }

        /** 导入失败 */
        val `ImportFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import failed"""
                        RootMLangTags.ZH -> """导入失败"""
                        else -> null
                    }
                } ?: """导入失败"""

        /** 导入失败 */
        @Composable
        fun `ImportFailed`(vararg args: Any?) = FYTxtConfig.observe { `ImportFailed`.fmt(args) }

        /** 地区快捷选择 */
        val `RegionQuickSelect`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Region Quick Select"""
                        RootMLangTags.ZH -> """地区快捷选择"""
                        else -> null
                    }
                } ?: """地区快捷选择"""

        /** 地区快捷选择 */
        @Composable
        fun `RegionQuickSelect`(vararg args: Any?) =
            FYTxtConfig.observe { `RegionQuickSelect`.fmt(args) }

        /** 对当前筛选结果中的应用按地区快速选择 */
        val `RegionQuickSelectSummaryBrowse`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Quick-select apps in the current filtered list by region"""
                        RootMLangTags.ZH -> """对当前筛选结果中的应用按地区快速选择"""
                        else -> null
                    }
                } ?: """对当前筛选结果中的应用按地区快速选择"""

        /** 对当前筛选结果中的应用按地区快速选择 */
        @Composable
        fun `RegionQuickSelectSummaryBrowse`(vararg args: Any?) =
            FYTxtConfig.observe { `RegionQuickSelectSummaryBrowse`.fmt(args) }

        /** 仅保留当前已选包名中的地区匹配项 */
        val `RegionQuickSelectSummaryManual`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Keep only region-matched packages from the selected list"""
                        RootMLangTags.ZH -> """仅保留当前已选包名中的地区匹配项"""
                        else -> null
                    }
                } ?: """仅保留当前已选包名中的地区匹配项"""

        /** 仅保留当前已选包名中的地区匹配项 */
        @Composable
        fun `RegionQuickSelectSummaryManual`(vararg args: Any?) =
            FYTxtConfig.observe { `RegionQuickSelectSummaryManual`.fmt(args) }

        /** 中国应用 */
        val `ChinaApps`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """China Apps"""
                        RootMLangTags.ZH -> """中国应用"""
                        else -> null
                    }
                } ?: """中国应用"""

        /** 中国应用 */
        @Composable
        fun `ChinaApps`(vararg args: Any?) = FYTxtConfig.observe { `ChinaApps`.fmt(args) }

        /** 非中国应用 */
        val `OverseasApps`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Non-China Apps"""
                        RootMLangTags.ZH -> """非中国应用"""
                        else -> null
                    }
                } ?: """非中国应用"""

        /** 非中国应用 */
        @Composable
        fun `OverseasApps`(vararg args: Any?) = FYTxtConfig.observe { `OverseasApps`.fmt(args) }

        /** 已按「%s」快捷选择，共 %d 个 */
        val `RegionSelectResult`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Quick selected by "%s", total %d"""
                        RootMLangTags.ZH -> """已按「%s」快捷选择，共 %d 个"""
                        else -> null
                    }
                } ?: """已按「%s」快捷选择，共 %d 个"""

        /** 已按「%s」快捷选择，共 %d 个 */
        @Composable
        fun `RegionSelectResult`(vararg args: Any?) =
            FYTxtConfig.observe { `RegionSelectResult`.fmt(args) }
    }

    object `SortMode` {
        init {
            RootMLangGroups
        }

        /** 包名 */
        val `PackageName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Package Name"""
                        RootMLangTags.ZH -> """包名"""
                        else -> null
                    }
                } ?: """包名"""

        /** 包名 */
        @Composable
        fun `PackageName`(vararg args: Any?) = FYTxtConfig.observe { `PackageName`.fmt(args) }

        /** 应用名称 */
        val `Label`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """App Name"""
                        RootMLangTags.ZH -> """应用名称"""
                        else -> null
                    }
                } ?: """应用名称"""

        /** 应用名称 */
        @Composable fun `Label`(vararg args: Any?) = FYTxtConfig.observe { `Label`.fmt(args) }

        /** 安装时间 */
        val `InstallTime`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Install Time"""
                        RootMLangTags.ZH -> """安装时间"""
                        else -> null
                    }
                } ?: """安装时间"""

        /** 安装时间 */
        @Composable
        fun `InstallTime`(vararg args: Any?) = FYTxtConfig.observe { `InstallTime`.fmt(args) }

        /** 更新时间 */
        val `UpdateTime`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update Time"""
                        RootMLangTags.ZH -> """更新时间"""
                        else -> null
                    }
                } ?: """更新时间"""

        /** 更新时间 */
        @Composable
        fun `UpdateTime`(vararg args: Any?) = FYTxtConfig.observe { `UpdateTime`.fmt(args) }
    }

    object `Button` {
        init {
            RootMLangGroups
        }

        /** 取消 */
        val `Cancel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cancel"""
                        RootMLangTags.ZH -> """取消"""
                        else -> null
                    }
                } ?: """取消"""

        /** 取消 */
        @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

        /** 确定 */
        val `Confirm`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Confirm"""
                        RootMLangTags.ZH -> """确定"""
                        else -> null
                    }
                } ?: """确定"""

        /** 确定 */
        @Composable fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }
    }
}
