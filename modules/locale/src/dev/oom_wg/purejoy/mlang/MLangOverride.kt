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

object MLangOverride {
    init {
        RootMLangGroups
    }

    /** 覆写配置 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Override Configs"""
                    RootMLangTags.ZH -> """覆写配置"""
                    else -> null
                }
            } ?: """覆写配置"""

    /** 覆写配置 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Action` {
        init {
            RootMLangGroups
        }

        /** 创建配置 */
        val `Create`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Create Config"""
                        RootMLangTags.ZH -> """创建配置"""
                        else -> null
                    }
                } ?: """创建配置"""

        /** 创建配置 */
        @Composable fun `Create`(vararg args: Any?) = FYTxtConfig.observe { `Create`.fmt(args) }

        /** 新建配置 */
        val `New`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """New Config"""
                        RootMLangTags.ZH -> """新建配置"""
                        else -> null
                    }
                } ?: """新建配置"""

        /** 新建配置 */
        @Composable fun `New`(vararg args: Any?) = FYTxtConfig.observe { `New`.fmt(args) }

        /** 导入配置/规则 */
        val `Import`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import Config/Rules"""
                        RootMLangTags.ZH -> """导入配置/规则"""
                        else -> null
                    }
                } ?: """导入配置/规则"""

        /** 导入配置/规则 */
        @Composable fun `Import`(vararg args: Any?) = FYTxtConfig.observe { `Import`.fmt(args) }

        /** 导入配置/规则文件 */
        val `ImportFile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import Config/Rules File"""
                        RootMLangTags.ZH -> """导入配置/规则文件"""
                        else -> null
                    }
                } ?: """导入配置/规则文件"""

        /** 导入配置/规则文件 */
        @Composable
        fun `ImportFile`(vararg args: Any?) = FYTxtConfig.observe { `ImportFile`.fmt(args) }

        /** 从 URL 导入配置/规则 */
        val `ImportFromUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import Config/Rules from URL"""
                        RootMLangTags.ZH -> """从 URL 导入配置/规则"""
                        else -> null
                    }
                } ?: """从 URL 导入配置/规则"""

        /** 从 URL 导入配置/规则 */
        @Composable
        fun `ImportFromUrl`(vararg args: Any?) = FYTxtConfig.observe { `ImportFromUrl`.fmt(args) }

        /** 导入 Surge/Loon 插件规则 */
        val `ImportSurge`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import Surge/Loon Plugin Rules"""
                        RootMLangTags.ZH -> """导入 Surge/Loon 插件规则"""
                        else -> null
                    }
                } ?: """导入 Surge/Loon 插件规则"""

        /** 导入 Surge/Loon 插件规则 */
        @Composable
        fun `ImportSurge`(vararg args: Any?) = FYTxtConfig.observe { `ImportSurge`.fmt(args) }

        /** 从 URL 导入配置 */
        val `ImportJsonUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import Config from URL"""
                        RootMLangTags.ZH -> """从 URL 导入配置"""
                        else -> null
                    }
                } ?: """从 URL 导入配置"""

        /** 从 URL 导入配置 */
        @Composable
        fun `ImportJsonUrl`(vararg args: Any?) = FYTxtConfig.observe { `ImportJsonUrl`.fmt(args) }

        /** 从 URL 导入 Surge 插件规则 */
        val `ImportSurgeUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import Surge Plugin Rules from URL"""
                        RootMLangTags.ZH -> """从 URL 导入 Surge 插件规则"""
                        else -> null
                    }
                } ?: """从 URL 导入 Surge 插件规则"""

        /** 从 URL 导入 Surge 插件规则 */
        @Composable
        fun `ImportSurgeUrl`(vararg args: Any?) = FYTxtConfig.observe { `ImportSurgeUrl`.fmt(args) }
    }

    object `Empty` {
        init {
            RootMLangGroups
        }

        /** 暂无覆写配置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No override configs"""
                        RootMLangTags.ZH -> """暂无覆写配置"""
                        else -> null
                    }
                } ?: """暂无覆写配置"""

        /** 暂无覆写配置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 点击下方按钮创建新配置， 或导入规则（JSON / Surge / Loon） */
        val `Hint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Click the button below to create a new config, or import JSON / Surge / Loon rules"""
                        RootMLangTags.ZH ->
                            """点击下方按钮创建新配置，
或导入规则（JSON / Surge / Loon）"""
                        else -> null
                    }
                }
                    ?: """点击下方按钮创建新配置，
或导入规则（JSON / Surge / Loon）"""

        /** 点击下方按钮创建新配置， 或导入规则（JSON / Surge / Loon） */
        @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
    }

    object `Status` {
        init {
            RootMLangGroups
        }

        /** 使用中 */
        val `InUse`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """In Use"""
                        RootMLangTags.ZH -> """使用中"""
                        else -> null
                    }
                } ?: """使用中"""

        /** 使用中 */
        @Composable fun `InUse`(vararg args: Any?) = FYTxtConfig.observe { `InUse`.fmt(args) }

        /** 未使用 */
        val `NotInUse`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Not in Use"""
                        RootMLangTags.ZH -> """未使用"""
                        else -> null
                    }
                } ?: """未使用"""

        /** 未使用 */
        @Composable fun `NotInUse`(vararg args: Any?) = FYTxtConfig.observe { `NotInUse`.fmt(args) }
    }

    object `Card` {
        init {
            RootMLangGroups
        }

        /** 复制配置 */
        val `Copy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Copy Config"""
                        RootMLangTags.ZH -> """复制配置"""
                        else -> null
                    }
                } ?: """复制配置"""

        /** 复制配置 */
        @Composable fun `Copy`(vararg args: Any?) = FYTxtConfig.observe { `Copy`.fmt(args) }

        /** 导出配置 */
        val `Export`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Export Config"""
                        RootMLangTags.ZH -> """导出配置"""
                        else -> null
                    }
                } ?: """导出配置"""

        /** 导出配置 */
        @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

        /** 编辑配置 */
        val `Edit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Config"""
                        RootMLangTags.ZH -> """编辑配置"""
                        else -> null
                    }
                } ?: """编辑配置"""

        /** 编辑配置 */
        @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

        /** 删除配置 */
        val `Delete`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete Config"""
                        RootMLangTags.ZH -> """删除配置"""
                        else -> null
                    }
                } ?: """删除配置"""

        /** 删除配置 */
        @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

        /** 编辑 */
        val `EditButton`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit"""
                        RootMLangTags.ZH -> """编辑"""
                        else -> null
                    }
                } ?: """编辑"""

        /** 编辑 */
        @Composable
        fun `EditButton`(vararg args: Any?) = FYTxtConfig.observe { `EditButton`.fmt(args) }

        /** 删除 */
        val `DeleteButton`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete"""
                        RootMLangTags.ZH -> """删除"""
                        else -> null
                    }
                } ?: """删除"""

        /** 删除 */
        @Composable
        fun `DeleteButton`(vararg args: Any?) = FYTxtConfig.observe { `DeleteButton`.fmt(args) }

        /** 未填写描述 */
        val `NoDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No description"""
                        RootMLangTags.ZH -> """未填写描述"""
                        else -> null
                    }
                } ?: """未填写描述"""

        /** 未填写描述 */
        @Composable
        fun `NoDescription`(vararg args: Any?) = FYTxtConfig.observe { `NoDescription`.fmt(args) }
    }

    object `Import` {
        init {
            RootMLangGroups
        }

        /** 无法读取导入文件 */
        val `ReadError`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cannot read import file"""
                        RootMLangTags.ZH -> """无法读取导入文件"""
                        else -> null
                    }
                } ?: """无法读取导入文件"""

        /** 无法读取导入文件 */
        @Composable
        fun `ReadError`(vararg args: Any?) = FYTxtConfig.observe { `ReadError`.fmt(args) }

        /** 已从 %s 导入 %d 个配置 */
        val `Success`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Imported %d configs from %s"""
                        RootMLangTags.ZH -> """已从 %s 导入 %d 个配置"""
                        else -> null
                    }
                } ?: """已从 %s 导入 %d 个配置"""

        /** 已从 %s 导入 %d 个配置 */
        @Composable fun `Success`(vararg args: Any?) = FYTxtConfig.observe { `Success`.fmt(args) }

        /** 已导入 %d 个配置 */
        val `SuccessDefault`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Imported %d configs"""
                        RootMLangTags.ZH -> """已导入 %d 个配置"""
                        else -> null
                    }
                } ?: """已导入 %d 个配置"""

        /** 已导入 %d 个配置 */
        @Composable
        fun `SuccessDefault`(vararg args: Any?) = FYTxtConfig.observe { `SuccessDefault`.fmt(args) }

        /** 导入失败: %s */
        val `Failed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import failed: %s"""
                        RootMLangTags.ZH -> """导入失败: %s"""
                        else -> null
                    }
                } ?: """导入失败: %s"""

        /** 导入失败: %s */
        @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

        /** 读取文件失败: %s */
        val `FileError`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to read file: %s"""
                        RootMLangTags.ZH -> """读取文件失败: %s"""
                        else -> null
                    }
                } ?: """读取文件失败: %s"""

        /** 读取文件失败: %s */
        @Composable
        fun `FileError`(vararg args: Any?) = FYTxtConfig.observe { `FileError`.fmt(args) }

        /** 已从 %s 导入 %d 条插件规则 */
        val `SurgeSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Imported %d plugin rules from %s"""
                        RootMLangTags.ZH -> """已从 %s 导入 %d 条插件规则"""
                        else -> null
                    }
                } ?: """已从 %s 导入 %d 条插件规则"""

        /** 已从 %s 导入 %d 条插件规则 */
        @Composable
        fun `SurgeSuccess`(vararg args: Any?) = FYTxtConfig.observe { `SurgeSuccess`.fmt(args) }

        /** 已导入 %d 条插件规则 */
        val `SurgeSuccessDefault`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Imported %d plugin rules"""
                        RootMLangTags.ZH -> """已导入 %d 条插件规则"""
                        else -> null
                    }
                } ?: """已导入 %d 条插件规则"""

        /** 已导入 %d 条插件规则 */
        @Composable
        fun `SurgeSuccessDefault`(vararg args: Any?) =
            FYTxtConfig.observe { `SurgeSuccessDefault`.fmt(args) }

        /** 未在 [Rule] 节中找到可用规则 */
        val `SurgeNoRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No usable rules found in [Rule] section"""
                        RootMLangTags.ZH -> """未在 [Rule] 节中找到可用规则"""
                        else -> null
                    }
                } ?: """未在 [Rule] 节中找到可用规则"""

        /** 未在 [Rule] 节中找到可用规则 */
        @Composable
        fun `SurgeNoRules`(vararg args: Any?) = FYTxtConfig.observe { `SurgeNoRules`.fmt(args) }

        /** 从 Surge 插件导入的规则 */
        val `SurgeImportDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rules imported from Surge plugin"""
                        RootMLangTags.ZH -> """从 Surge 插件导入的规则"""
                        else -> null
                    }
                } ?: """从 Surge 插件导入的规则"""

        /** 从 Surge 插件导入的规则 */
        @Composable
        fun `SurgeImportDescription`(vararg args: Any?) =
            FYTxtConfig.observe { `SurgeImportDescription`.fmt(args) }

        /** 未在 [Rule] 节中找到可用规则 */
        val `PluginNoRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No usable rules found in [Rule] section"""
                        RootMLangTags.ZH -> """未在 [Rule] 节中找到可用规则"""
                        else -> null
                    }
                } ?: """未在 [Rule] 节中找到可用规则"""

        /** 未在 [Rule] 节中找到可用规则 */
        @Composable
        fun `PluginNoRules`(vararg args: Any?) = FYTxtConfig.observe { `PluginNoRules`.fmt(args) }

        /** 从 Surge/Loon 插件导入的规则 */
        val `PluginImportDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rules imported from Surge/Loon plugin"""
                        RootMLangTags.ZH -> """从 Surge/Loon 插件导入的规则"""
                        else -> null
                    }
                } ?: """从 Surge/Loon 插件导入的规则"""

        /** 从 Surge/Loon 插件导入的规则 */
        @Composable
        fun `PluginImportDescription`(vararg args: Any?) =
            FYTxtConfig.observe { `PluginImportDescription`.fmt(args) }

        /** 未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件 */
        val `AutoDetectFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """No importable content found. Expect JSON or a plugin with [Rule] section"""
                        RootMLangTags.ZH -> """未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件"""
                        else -> null
                    }
                } ?: """未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件"""

        /** 未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件 */
        @Composable
        fun `AutoDetectFailed`(vararg args: Any?) =
            FYTxtConfig.observe { `AutoDetectFailed`.fmt(args) }

        /** 远程 URL */
        val `UrlLabel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Remote URL"""
                        RootMLangTags.ZH -> """远程 URL"""
                        else -> null
                    }
                } ?: """远程 URL"""

        /** 远程 URL */
        @Composable fun `UrlLabel`(vararg args: Any?) = FYTxtConfig.observe { `UrlLabel`.fmt(args) }

        /** 正在下载，请稍候… */
        val `UrlDownloading`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Downloading, please wait…"""
                        RootMLangTags.ZH -> """正在下载，请稍候…"""
                        else -> null
                    }
                } ?: """正在下载，请稍候…"""

        /** 正在下载，请稍候… */
        @Composable
        fun `UrlDownloading`(vararg args: Any?) = FYTxtConfig.observe { `UrlDownloading`.fmt(args) }

        /** 仅支持 http:// 或 https:// 链接 */
        val `UrlInvalidScheme`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Only http:// or https:// URLs are supported"""
                        RootMLangTags.ZH -> """仅支持 http:// 或 https:// 链接"""
                        else -> null
                    }
                } ?: """仅支持 http:// 或 https:// 链接"""

        /** 仅支持 http:// 或 https:// 链接 */
        @Composable
        fun `UrlInvalidScheme`(vararg args: Any?) =
            FYTxtConfig.observe { `UrlInvalidScheme`.fmt(args) }

        /** 仅允许 https:// 链接（localhost 调试地址除外） */
        val `UrlHttpsRequired`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Only https:// URLs are allowed (except localhost for debugging)"""
                        RootMLangTags.ZH -> """仅允许 https:// 链接（localhost 调试地址除外）"""
                        else -> null
                    }
                } ?: """仅允许 https:// 链接（localhost 调试地址除外）"""

        /** 仅允许 https:// 链接（localhost 调试地址除外） */
        @Composable
        fun `UrlHttpsRequired`(vararg args: Any?) =
            FYTxtConfig.observe { `UrlHttpsRequired`.fmt(args) }

        /** 请求失败：HTTP %d */
        val `UrlHttpError`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Request failed: HTTP %d"""
                        RootMLangTags.ZH -> """请求失败：HTTP %d"""
                        else -> null
                    }
                } ?: """请求失败：HTTP %d"""

        /** 请求失败：HTTP %d */
        @Composable
        fun `UrlHttpError`(vararg args: Any?) = FYTxtConfig.observe { `UrlHttpError`.fmt(args) }

        /** 远程内容超过 %dMB 大小限制 */
        val `UrlContentTooLarge`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Remote content exceeds %dMB size limit"""
                        RootMLangTags.ZH -> """远程内容超过 %dMB 大小限制"""
                        else -> null
                    }
                } ?: """远程内容超过 %dMB 大小限制"""

        /** 远程内容超过 %dMB 大小限制 */
        @Composable
        fun `UrlContentTooLarge`(vararg args: Any?) =
            FYTxtConfig.observe { `UrlContentTooLarge`.fmt(args) }

        /** 重定向地址无效，无法继续下载 */
        val `UrlRedirectInvalid`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Invalid redirect target, download aborted"""
                        RootMLangTags.ZH -> """重定向地址无效，无法继续下载"""
                        else -> null
                    }
                } ?: """重定向地址无效，无法继续下载"""

        /** 重定向地址无效，无法继续下载 */
        @Composable
        fun `UrlRedirectInvalid`(vararg args: Any?) =
            FYTxtConfig.observe { `UrlRedirectInvalid`.fmt(args) }

        /** 重定向次数过多，已停止下载 */
        val `UrlTooManyRedirects`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Too many redirects, download aborted"""
                        RootMLangTags.ZH -> """重定向次数过多，已停止下载"""
                        else -> null
                    }
                } ?: """重定向次数过多，已停止下载"""

        /** 重定向次数过多，已停止下载 */
        @Composable
        fun `UrlTooManyRedirects`(vararg args: Any?) =
            FYTxtConfig.observe { `UrlTooManyRedirects`.fmt(args) }

        object `UrlSheet` {
            init {
                RootMLangGroups
            }

            /** 从 URL 自动导入配置/规则 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Auto Import Config/Rules from URL"""
                            RootMLangTags.ZH -> """从 URL 自动导入配置/规则"""
                            else -> null
                        }
                    } ?: """从 URL 自动导入配置/规则"""

            /** 从 URL 自动导入配置/规则 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 从 URL 导入配置 */
            val `JsonTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Import Config from URL"""
                            RootMLangTags.ZH -> """从 URL 导入配置"""
                            else -> null
                        }
                    } ?: """从 URL 导入配置"""

            /** 从 URL 导入配置 */
            @Composable
            fun `JsonTitle`(vararg args: Any?) = FYTxtConfig.observe { `JsonTitle`.fmt(args) }

            /** 从 URL 导入 Surge 插件规则 */
            val `SurgeTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Import Surge Plugin Rules from URL"""
                            RootMLangTags.ZH -> """从 URL 导入 Surge 插件规则"""
                            else -> null
                        }
                    } ?: """从 URL 导入 Surge 插件规则"""

            /** 从 URL 导入 Surge 插件规则 */
            @Composable
            fun `SurgeTitle`(vararg args: Any?) = FYTxtConfig.observe { `SurgeTitle`.fmt(args) }
        }
    }

    object `Export` {
        init {
            RootMLangGroups
        }

        /** 导出失败：%s */
        val `Failed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Export failed: %s"""
                        RootMLangTags.ZH -> """导出失败：%s"""
                        else -> null
                    }
                } ?: """导出失败：%s"""

        /** 导出失败：%s */
        @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

        /** 已导出配置：%s */
        val `Success`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Exported config: %s"""
                        RootMLangTags.ZH -> """已导出配置：%s"""
                        else -> null
                    }
                } ?: """已导出配置：%s"""

        /** 已导出配置：%s */
        @Composable fun `Success`(vararg args: Any?) = FYTxtConfig.observe { `Success`.fmt(args) }
    }

    object `Dialog` {
        init {
            RootMLangGroups
        }

        object `Create` {
            init {
                RootMLangGroups
            }

            /** 添加配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Add Config"""
                            RootMLangTags.ZH -> """添加配置"""
                            else -> null
                        }
                    } ?: """添加配置"""

            /** 添加配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 配置名称 */
            val `Name`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Config Name"""
                            RootMLangTags.ZH -> """配置名称"""
                            else -> null
                        }
                    } ?: """配置名称"""

            /** 配置名称 */
            @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }

            /** 配置描述 */
            val `Description`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Config Description"""
                            RootMLangTags.ZH -> """配置描述"""
                            else -> null
                        }
                    } ?: """配置描述"""

            /** 配置描述 */
            @Composable
            fun `Description`(vararg args: Any?) = FYTxtConfig.observe { `Description`.fmt(args) }

            /** 自动识别 JSON 配置，或 Surge/Loon 插件中的 [Rule] 规则 */
            val `ImportHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Auto-detect a JSON config, or extract [Rule] entries from a Surge/Loon plugin"""
                            RootMLangTags.ZH -> """自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""
                            else -> null
                        }
                    } ?: """自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""

            /** 自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则 */
            @Composable
            fun `ImportHint`(vararg args: Any?) = FYTxtConfig.observe { `ImportHint`.fmt(args) }

            /** 输入远程 URL，自动识别 JSON 或 Surge/Loon 插件规则 */
            val `ImportFromUrlHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Enter a remote URL to auto-detect a JSON config, or extract [Rule] entries from a Surge/Loon plugin"""
                            RootMLangTags.ZH ->
                                """输入远程 URL；可自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""
                            else -> null
                        }
                    } ?: """输入远程 URL；可自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""

            /** 输入远程 URL；可自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则 */
            @Composable
            fun `ImportFromUrlHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportFromUrlHint`.fmt(args) }

            /** 选择 .sgmodule 文件提取 [Rule] 规则 */
            val `ImportSurgeHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Select a .sgmodule file to extract [Rule] rules"""
                            RootMLangTags.ZH -> """选择 .sgmodule 文件提取 [Rule] 规则"""
                            else -> null
                        }
                    } ?: """选择 .sgmodule 文件提取 [Rule] 规则"""

            /** 选择 .sgmodule 文件提取 [Rule] 规则 */
            @Composable
            fun `ImportSurgeHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportSurgeHint`.fmt(args) }

            /** 输入 JSON 配置的远程 URL */
            val `ImportJsonUrlHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Enter the remote URL of a JSON config"""
                            RootMLangTags.ZH -> """输入 JSON 配置的远程 URL"""
                            else -> null
                        }
                    } ?: """输入 JSON 配置的远程 URL"""

            /** 输入 JSON 配置的远程 URL */
            @Composable
            fun `ImportJsonUrlHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportJsonUrlHint`.fmt(args) }

            /** 输入 .sgmodule 的远程 URL */
            val `ImportSurgeUrlHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Enter the remote URL of a .sgmodule file"""
                            RootMLangTags.ZH -> """输入 .sgmodule 的远程 URL"""
                            else -> null
                        }
                    } ?: """输入 .sgmodule 的远程 URL"""

            /** 输入 .sgmodule 的远程 URL */
            @Composable
            fun `ImportSurgeUrlHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportSurgeUrlHint`.fmt(args) }
        }

        object `Delete` {
            init {
                RootMLangGroups
            }

            /** 删除配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Delete Config"""
                            RootMLangTags.ZH -> """删除配置"""
                            else -> null
                        }
                    } ?: """删除配置"""

            /** 删除配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。 */
            val `InUseMessage`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Config %s is being used by subscriptions. Deleting will unbind the relationship. Are you sure? This action cannot be undone."""
                            RootMLangTags.ZH -> """配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。"""
                            else -> null
                        }
                    } ?: """配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。"""

            /** 配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。 */
            @Composable
            fun `InUseMessage`(vararg args: Any?) = FYTxtConfig.observe { `InUseMessage`.fmt(args) }

            /** 确定要删除配置 %s 吗？此操作不可恢复。 */
            val `Message`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Are you sure you want to delete config %s? This action cannot be undone."""
                            RootMLangTags.ZH -> """确定要删除配置 %s 吗？此操作不可恢复。"""
                            else -> null
                        }
                    } ?: """确定要删除配置 %s 吗？此操作不可恢复。"""

            /** 确定要删除配置 %s 吗？此操作不可恢复。 */
            @Composable
            fun `Message`(vararg args: Any?) = FYTxtConfig.observe { `Message`.fmt(args) }
        }

        object `EditOptions` {
            init {
                RootMLangGroups
            }

            /** 编辑配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Edit Config"""
                            RootMLangTags.ZH -> """编辑配置"""
                            else -> null
                        }
                    } ?: """编辑配置"""

            /** 编辑配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 代码编辑器 */
            val `CodeEditor`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Code Editor"""
                            RootMLangTags.ZH -> """代码编辑器"""
                            else -> null
                        }
                    } ?: """代码编辑器"""

            /** 代码编辑器 */
            @Composable
            fun `CodeEditor`(vararg args: Any?) = FYTxtConfig.observe { `CodeEditor`.fmt(args) }

            /** 可视化编辑 */
            val `VisualEditor`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Visual Editor"""
                            RootMLangTags.ZH -> """可视化编辑"""
                            else -> null
                        }
                    } ?: """可视化编辑"""

            /** 可视化编辑 */
            @Composable
            fun `VisualEditor`(vararg args: Any?) = FYTxtConfig.observe { `VisualEditor`.fmt(args) }
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

            /** 删除 */
            val `Delete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Delete"""
                            RootMLangTags.ZH -> """删除"""
                            else -> null
                        }
                    } ?: """删除"""

            /** 删除 */
            @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }
        }
    }

    object `Edit` {
        init {
            RootMLangGroups
        }

        /** 新建配置 */
        val `TitleNew`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """New Config"""
                        RootMLangTags.ZH -> """新建配置"""
                        else -> null
                    }
                } ?: """新建配置"""

        /** 新建配置 */
        @Composable fun `TitleNew`(vararg args: Any?) = FYTxtConfig.observe { `TitleNew`.fmt(args) }

        /** 使用 JSON 格式进行高级编辑 */
        val `JsonEditHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Edit the raw JSON directly for fields not covered by the structured form"""
                        RootMLangTags.ZH -> """直接编辑原始 JSON，适合补充结构化表单未覆盖的字段"""
                        else -> null
                    }
                } ?: """直接编辑原始 JSON，适合补充结构化表单未覆盖的字段"""

        /** 直接编辑原始 JSON，适合补充结构化表单未覆盖的字段 */
        @Composable
        fun `JsonEditHint`(vararg args: Any?) = FYTxtConfig.observe { `JsonEditHint`.fmt(args) }

        /** 使用结构化对象列表编辑 */
        val `StructuredObjectListHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Edit the object list item by item with the structured form"""
                        RootMLangTags.ZH -> """使用结构化表单逐项编辑对象列表"""
                        else -> null
                    }
                } ?: """使用结构化表单逐项编辑对象列表"""

        /** 使用结构化表单逐项编辑对象列表 */
        @Composable
        fun `StructuredObjectListHint`(vararg args: Any?) =
            FYTxtConfig.observe { `StructuredObjectListHint`.fmt(args) }

        /** 使用结构化提供者字典编辑 */
        val `StructuredProviderDictHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Edit the provider dictionary with the structured form; each key maps to one provider"""
                        RootMLangTags.ZH -> """使用结构化表单编辑 provider 字典；每个键对应一个 provider"""
                        else -> null
                    }
                } ?: """使用结构化表单编辑 provider 字典；每个键对应一个 provider"""

        /** 使用结构化表单编辑 provider 字典；每个键对应一个 provider */
        @Composable
        fun `StructuredProviderDictHint`(vararg args: Any?) =
            FYTxtConfig.observe { `StructuredProviderDictHint`.fmt(args) }

        /** 可在此部分定义规则组 */
        val `SubRuleGroupHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Manage sub-rule groups here; each group can contain multiple rules"""
                        RootMLangTags.ZH -> """在这里维护子规则组；每个分组可包含多条规则"""
                        else -> null
                    }
                } ?: """在这里维护子规则组；每个分组可包含多条规则"""

        /** 在这里维护子规则组；每个分组可包含多条规则 */
        @Composable
        fun `SubRuleGroupHint`(vararg args: Any?) =
            FYTxtConfig.observe { `SubRuleGroupHint`.fmt(args) }

        /** JSON 对象字段需为键值对格式 */
        val `JsonKeyValueHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Fields must be valid JSON key-value pairs, for example { \"name\": \"proxy\" }"""
                        RootMLangTags.ZH -> """字段必须写成合法的 JSON 键值对，例如 { \"name\": \"proxy\" }"""
                        else -> null
                    }
                } ?: """字段必须写成合法的 JSON 键值对，例如 { \"name\": \"proxy\" }"""

        /** 字段必须写成合法的 JSON 键值对，例如 { "name": "proxy" } */
        @Composable
        fun `JsonKeyValueHint`(vararg args: Any?) =
            FYTxtConfig.observe { `JsonKeyValueHint`.fmt(args) }

        /** 每行一个提供者名称 */
        val `OneProviderPerLineHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Enter one provider name per line; each name must match an existing provider"""
                        RootMLangTags.ZH -> """每行填写一个提供者名称；名称需与已定义的 provider 一致"""
                        else -> null
                    }
                } ?: """每行填写一个提供者名称；名称需与已定义的 provider 一致"""

        /** 每行填写一个提供者名称；名称需与已定义的 provider 一致 */
        @Composable
        fun `OneProviderPerLineHint`(vararg args: Any?) =
            FYTxtConfig.observe { `OneProviderPerLineHint`.fmt(args) }

        /** 编辑配置 */
        val `TitleEdit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Config"""
                        RootMLangTags.ZH -> """编辑配置"""
                        else -> null
                    }
                } ?: """编辑配置"""

        /** 编辑配置 */
        @Composable
        fun `TitleEdit`(vararg args: Any?) = FYTxtConfig.observe { `TitleEdit`.fmt(args) }

        object `EmptyName` {
            init {
                RootMLangGroups
            }

            /** 名称为空 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Empty Name"""
                            RootMLangTags.ZH -> """名称为空"""
                            else -> null
                        }
                    } ?: """名称为空"""

            /** 名称为空 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？ */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Current name is empty, cannot save in real-time. Are you sure to discard these unsaved changes?"""
                            RootMLangTags.ZH -> """当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？"""
                            else -> null
                        }
                    } ?: """当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？"""

            /** 当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？ */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
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

            /** 放弃 */
            val `Discard`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Discard"""
                            RootMLangTags.ZH -> """放弃"""
                            else -> null
                        }
                    } ?: """放弃"""

            /** 放弃 */
            @Composable
            fun `Discard`(vararg args: Any?) = FYTxtConfig.observe { `Discard`.fmt(args) }
        }

        /** 已更新预设分流模板 */
        val `PresetApplied`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Preset routing template applied and routing sections rebuilt"""
                        RootMLangTags.ZH -> """已应用预设分流模板，并重建相关分流配置"""
                        else -> null
                    }
                } ?: """已应用预设分流模板，并重建相关分流配置"""

        /** 已应用预设分流模板，并重建相关分流配置 */
        @Composable
        fun `PresetApplied`(vararg args: Any?) = FYTxtConfig.observe { `PresetApplied`.fmt(args) }
    }

    object `Section` {
        init {
            RootMLangGroups
        }

        object `General` {
            init {
                RootMLangGroups
            }

            /** 全局配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """General"""
                            RootMLangTags.ZH -> """全局配置"""
                            else -> null
                        }
                    } ?: """全局配置"""

            /** 全局配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 运行模式、控制器、持久化与 GEO */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Run mode, controller, persistence & GEO"""
                            RootMLangTags.ZH -> """运行模式、控制器、持久化与 GEO"""
                            else -> null
                        }
                    } ?: """运行模式、控制器、持久化与 GEO"""

            /** 运行模式、控制器、持久化与 GEO */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `Dns` {
            init {
                RootMLangGroups
            }

            /** DNS */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """DNS"""
                            RootMLangTags.ZH -> """DNS"""
                            else -> null
                        }
                    } ?: """DNS"""

            /** DNS */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 基础开关、Fake-IP、上游与策略 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Basic switches, Fake-IP, upstream & policy"""
                            RootMLangTags.ZH -> """基础开关、Fake-IP、上游与策略"""
                            else -> null
                        }
                    } ?: """基础开关、Fake-IP、上游与策略"""

            /** 基础开关、Fake-IP、上游与策略 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `Sniffer` {
            init {
                RootMLangGroups
            }

            /** 域名嗅探 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Domain Sniffer"""
                            RootMLangTags.ZH -> """域名嗅探"""
                            else -> null
                        }
                    } ?: """域名嗅探"""

            /** 域名嗅探 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 策略开关、协议端口、跳过规则 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Policy switches, protocol ports, skip rules"""
                            RootMLangTags.ZH -> """策略开关、协议端口、跳过规则"""
                            else -> null
                        }
                    } ?: """策略开关、协议端口、跳过规则"""

            /** 策略开关、协议端口、跳过规则 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `Inbound` {
            init {
                RootMLangGroups
            }

            /** 入站 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Inbound"""
                            RootMLangTags.ZH -> """入站"""
                            else -> null
                        }
                    } ?: """入站"""

            /** 入站 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 端口、鉴权、局域网访问 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Ports, authentication, LAN access"""
                            RootMLangTags.ZH -> """端口、鉴权、局域网访问"""
                            else -> null
                        }
                    } ?: """端口、鉴权、局域网访问"""

            /** 端口、鉴权、局域网访问 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `Tun` {
            init {
                RootMLangGroups
            }

            /** Tun */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Tun"""
                            RootMLangTags.ZH -> """Tun"""
                            else -> null
                        }
                    } ?: """Tun"""

            /** Tun */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 入站 Tun、路由与应用范围 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Tun inbound, routing & app scope"""
                            RootMLangTags.ZH -> """入站 Tun、路由与应用范围"""
                            else -> null
                        }
                    } ?: """入站 Tun、路由与应用范围"""

            /** 入站 Tun、路由与应用范围 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `Rules` {
            init {
                RootMLangGroups
            }

            /** 路由规则 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Routing Rules"""
                            RootMLangTags.ZH -> """路由规则"""
                            else -> null
                        }
                    } ?: """路由规则"""

            /** 路由规则 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 规则链与匹配顺序 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule chain & matching order"""
                            RootMLangTags.ZH -> """规则链与匹配顺序"""
                            else -> null
                        }
                    } ?: """规则链与匹配顺序"""

            /** 规则链与匹配顺序 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `Proxies` {
            init {
                RootMLangGroups
            }

            /** 出站代理 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Outbound Proxies"""
                            RootMLangTags.ZH -> """出站代理"""
                            else -> null
                        }
                    } ?: """出站代理"""

            /** 出站代理 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 代理节点与协议对象 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy nodes & protocol objects"""
                            RootMLangTags.ZH -> """代理节点与协议对象"""
                            else -> null
                        }
                    } ?: """代理节点与协议对象"""

            /** 代理节点与协议对象 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `ProxyProviders` {
            init {
                RootMLangGroups
            }

            /** 代理集合 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy Providers"""
                            RootMLangTags.ZH -> """代理集合"""
                            else -> null
                        }
                    } ?: """代理集合"""

            /** 代理集合 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 提供者来源、路径与更新配置 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Provider sources, paths & update settings"""
                            RootMLangTags.ZH -> """提供者来源、路径与更新配置"""
                            else -> null
                        }
                    } ?: """提供者来源、路径与更新配置"""

            /** 提供者来源、路径与更新配置 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `ProxyGroups` {
            init {
                RootMLangGroups
            }

            /** 代理组 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy Groups"""
                            RootMLangTags.ZH -> """代理组"""
                            else -> null
                        }
                    } ?: """代理组"""

            /** 代理组 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 分组策略、成员与顺序 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Group strategy, members & order"""
                            RootMLangTags.ZH -> """分组策略、成员与顺序"""
                            else -> null
                        }
                    } ?: """分组策略、成员与顺序"""

            /** 分组策略、成员与顺序 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `RuleProviders` {
            init {
                RootMLangGroups
            }

            /** 规则集合 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule Providers"""
                            RootMLangTags.ZH -> """规则集合"""
                            else -> null
                        }
                    } ?: """规则集合"""

            /** 规则集合 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 规则来源、路径与更新配置 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule sources, paths & update settings"""
                            RootMLangTags.ZH -> """规则来源、路径与更新配置"""
                            else -> null
                        }
                    } ?: """规则来源、路径与更新配置"""

            /** 规则来源、路径与更新配置 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `SubRules` {
            init {
                RootMLangGroups
            }

            /** 子规则 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Sub Rules"""
                            RootMLangTags.ZH -> """子规则"""
                            else -> null
                        }
                    } ?: """子规则"""

            /** 子规则 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 规则分组与引用关系 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule groups & references"""
                            RootMLangTags.ZH -> """规则分组与引用关系"""
                            else -> null
                        }
                    } ?: """规则分组与引用关系"""

            /** 规则分组与引用关系 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }
    }

    object `Modifier` {
        init {
            RootMLangGroups
        }

        /** 覆盖 */
        val `Replace`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Replace"""
                        RootMLangTags.ZH -> """覆盖"""
                        else -> null
                    }
                } ?: """覆盖"""

        /** 覆盖 */
        @Composable fun `Replace`(vararg args: Any?) = FYTxtConfig.observe { `Replace`.fmt(args) }

        /** 前置追加 */
        val `Start`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Prepend"""
                        RootMLangTags.ZH -> """前置追加"""
                        else -> null
                    }
                } ?: """前置追加"""

        /** 前置追加 */
        @Composable fun `Start`(vararg args: Any?) = FYTxtConfig.observe { `Start`.fmt(args) }

        /** 后置追加 */
        val `End`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Append"""
                        RootMLangTags.ZH -> """后置追加"""
                        else -> null
                    }
                } ?: """后置追加"""

        /** 后置追加 */
        @Composable fun `End`(vararg args: Any?) = FYTxtConfig.observe { `End`.fmt(args) }

        /** 合并 */
        val `Merge`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Merge"""
                        RootMLangTags.ZH -> """合并"""
                        else -> null
                    }
                } ?: """合并"""

        /** 合并 */
        @Composable fun `Merge`(vararg args: Any?) = FYTxtConfig.observe { `Merge`.fmt(args) }

        /** 强制覆盖 */
        val `Force`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Force Replace"""
                        RootMLangTags.ZH -> """强制覆盖"""
                        else -> null
                    }
                } ?: """强制覆盖"""

        /** 强制覆盖 */
        @Composable fun `Force`(vararg args: Any?) = FYTxtConfig.observe { `Force`.fmt(args) }

        /** 未修改 */
        val `NotModified`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Not Modified"""
                        RootMLangTags.ZH -> """未修改"""
                        else -> null
                    }
                } ?: """未修改"""

        /** 未修改 */
        @Composable
        fun `NotModified`(vararg args: Any?) = FYTxtConfig.observe { `NotModified`.fmt(args) }

        /** %d 项 */
        val `ItemsCount`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d items"""
                        RootMLangTags.ZH -> """%d 项"""
                        else -> null
                    }
                } ?: """%d 项"""

        /** %d 项 */
        @Composable
        fun `ItemsCount`(vararg args: Any?) = FYTxtConfig.observe { `ItemsCount`.fmt(args) }

        /** 暂无改动 */
        val `NoChanges`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No changes"""
                        RootMLangTags.ZH -> """暂无改动"""
                        else -> null
                    }
                } ?: """暂无改动"""

        /** 暂无改动 */
        @Composable
        fun `NoChanges`(vararg args: Any?) = FYTxtConfig.observe { `NoChanges`.fmt(args) }
    }

    object `Structured` {
        init {
            RootMLangGroups
        }

        object `Proxies` {
            init {
                RootMLangGroups
            }

            /** 代理节点 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy Nodes"""
                            RootMLangTags.ZH -> """代理节点"""
                            else -> null
                        }
                    } ?: """代理节点"""

            /** 代理节点 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 代理节点 */
            val `ItemLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy Node"""
                            RootMLangTags.ZH -> """代理节点"""
                            else -> null
                        }
                    } ?: """代理节点"""

            /** 代理节点 */
            @Composable
            fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }

            /** 暂无代理节点 */
            val `EmptyHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """No proxy nodes"""
                            RootMLangTags.ZH -> """暂无代理节点"""
                            else -> null
                        }
                    } ?: """暂无代理节点"""

            /** 暂无代理节点 */
            @Composable
            fun `EmptyHint`(vararg args: Any?) = FYTxtConfig.observe { `EmptyHint`.fmt(args) }
        }

        object `ProxyGroups` {
            init {
                RootMLangGroups
            }

            /** 策略组 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy Groups"""
                            RootMLangTags.ZH -> """策略组"""
                            else -> null
                        }
                    } ?: """策略组"""

            /** 策略组 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 策略组 */
            val `ItemLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy Group"""
                            RootMLangTags.ZH -> """策略组"""
                            else -> null
                        }
                    } ?: """策略组"""

            /** 策略组 */
            @Composable
            fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }

            /** 暂无策略组 */
            val `EmptyHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """No proxy groups"""
                            RootMLangTags.ZH -> """暂无策略组"""
                            else -> null
                        }
                    } ?: """暂无策略组"""

            /** 暂无策略组 */
            @Composable
            fun `EmptyHint`(vararg args: Any?) = FYTxtConfig.observe { `EmptyHint`.fmt(args) }
        }

        object `RuleProviders` {
            init {
                RootMLangGroups
            }

            /** 规则提供者 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule Providers"""
                            RootMLangTags.ZH -> """规则提供者"""
                            else -> null
                        }
                    } ?: """规则提供者"""

            /** 规则提供者 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** Provider */
            val `ItemLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Provider"""
                            RootMLangTags.ZH -> """Provider"""
                            else -> null
                        }
                    } ?: """Provider"""

            /** Provider */
            @Composable
            fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }
        }

        object `ProxyProviders` {
            init {
                RootMLangGroups
            }

            /** 代理提供者 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Proxy Providers"""
                            RootMLangTags.ZH -> """代理提供者"""
                            else -> null
                        }
                    } ?: """代理提供者"""

            /** 代理提供者 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** Provider */
            val `ItemLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Provider"""
                            RootMLangTags.ZH -> """Provider"""
                            else -> null
                        }
                    } ?: """Provider"""

            /** Provider */
            @Composable
            fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }
        }

        object `SubRules` {
            init {
                RootMLangGroups
            }

            /** 子规则 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Sub Rules"""
                            RootMLangTags.ZH -> """子规则"""
                            else -> null
                        }
                    } ?: """子规则"""

            /** 子规则 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 子规则组 */
            val `ItemLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Sub Rule Group"""
                            RootMLangTags.ZH -> """子规则组"""
                            else -> null
                        }
                    } ?: """子规则组"""

            /** 子规则组 */
            @Composable
            fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }
        }
    }

    object `Editor` {
        init {
            RootMLangGroups
        }

        object `Mode` {
            init {
                RootMLangGroups
            }

            /** 修饰符模式 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Modifier Mode"""
                            RootMLangTags.ZH -> """修饰符模式"""
                            else -> null
                        }
                    } ?: """修饰符模式"""

            /** 修饰符模式 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 修改方式 */
            val `EditTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Edit Mode"""
                            RootMLangTags.ZH -> """修改方式"""
                            else -> null
                        }
                    } ?: """修改方式"""

            /** 修改方式 */
            @Composable
            fun `EditTitle`(vararg args: Any?) = FYTxtConfig.observe { `EditTitle`.fmt(args) }

            /** 直接修改 */
            val `DirectEdit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Direct Edit"""
                            RootMLangTags.ZH -> """直接修改"""
                            else -> null
                        }
                    } ?: """直接修改"""

            /** 直接修改 */
            @Composable
            fun `DirectEdit`(vararg args: Any?) = FYTxtConfig.observe { `DirectEdit`.fmt(args) }
        }

        object `ClearDialog` {
            init {
                RootMLangGroups
            }

            /** 清空%s */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Clear %s"""
                            RootMLangTags.ZH -> """清空%s"""
                            else -> null
                        }
                    } ?: """清空%s"""

            /** 清空%s */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 清空后将移除当前模式里的所有%s。 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Clearing will remove all %s in current mode."""
                            RootMLangTags.ZH -> """清空后将移除当前模式里的所有%s。"""
                            else -> null
                        }
                    } ?: """清空后将移除当前模式里的所有%s。"""

            /** 清空后将移除当前模式里的所有%s。 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        /** 新增 */
        val `New`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add"""
                        RootMLangTags.ZH -> """新增"""
                        else -> null
                    }
                } ?: """新增"""

        /** 新增 */
        @Composable fun `New`(vararg args: Any?) = FYTxtConfig.observe { `New`.fmt(args) }

        /** 新增%s */
        val `AddNamedItem`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add %s"""
                        RootMLangTags.ZH -> """新增%s"""
                        else -> null
                    }
                } ?: """新增%s"""

        /** 新增%s */
        @Composable
        fun `AddNamedItem`(vararg args: Any?) = FYTxtConfig.observe { `AddNamedItem`.fmt(args) }

        /** 新增规则 */
        val `NewRule`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add New Rule"""
                        RootMLangTags.ZH -> """新增规则"""
                        else -> null
                    }
                } ?: """新增规则"""

        /** 新增规则 */
        @Composable fun `NewRule`(vararg args: Any?) = FYTxtConfig.observe { `NewRule`.fmt(args) }

        /** 编辑规则 */
        val `EditRule`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Rule"""
                        RootMLangTags.ZH -> """编辑规则"""
                        else -> null
                    }
                } ?: """编辑规则"""

        /** 编辑规则 */
        @Composable fun `EditRule`(vararg args: Any?) = FYTxtConfig.observe { `EditRule`.fmt(args) }

        /** 新增子规则组 */
        val `NewSubRuleGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add New Sub Rule Group"""
                        RootMLangTags.ZH -> """新增子规则组"""
                        else -> null
                    }
                } ?: """新增子规则组"""

        /** 新增子规则组 */
        @Composable
        fun `NewSubRuleGroup`(vararg args: Any?) =
            FYTxtConfig.observe { `NewSubRuleGroup`.fmt(args) }

        /** 未命名子规则组 */
        val `UnnamedSubRuleGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unnamed Sub Rule Group"""
                        RootMLangTags.ZH -> """未命名子规则组"""
                        else -> null
                    }
                } ?: """未命名子规则组"""

        /** 未命名子规则组 */
        @Composable
        fun `UnnamedSubRuleGroup`(vararg args: Any?) =
            FYTxtConfig.observe { `UnnamedSubRuleGroup`.fmt(args) }

        /** 编辑子规则组 */
        val `EditSubRuleGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Sub Rule Group"""
                        RootMLangTags.ZH -> """编辑子规则组"""
                        else -> null
                    }
                } ?: """编辑子规则组"""

        /** 编辑子规则组 */
        @Composable
        fun `EditSubRuleGroup`(vararg args: Any?) =
            FYTxtConfig.observe { `EditSubRuleGroup`.fmt(args) }

        /** 清空子规则 */
        val `ClearSubRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Clear Sub Rules"""
                        RootMLangTags.ZH -> """清空子规则"""
                        else -> null
                    }
                } ?: """清空子规则"""

        /** 清空子规则 */
        @Composable
        fun `ClearSubRules`(vararg args: Any?) = FYTxtConfig.observe { `ClearSubRules`.fmt(args) }

        /** 未命名%s */
        val `Unnamed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unnamed %s"""
                        RootMLangTags.ZH -> """未命名%s"""
                        else -> null
                    }
                } ?: """未命名%s"""

        /** 未命名%s */
        @Composable fun `Unnamed`(vararg args: Any?) = FYTxtConfig.observe { `Unnamed`.fmt(args) }

        /** 未命名规则 */
        val `UnnamedRule`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unnamed Rule"""
                        RootMLangTags.ZH -> """未命名规则"""
                        else -> null
                    }
                } ?: """未命名规则"""

        /** 未命名规则 */
        @Composable
        fun `UnnamedRule`(vararg args: Any?) = FYTxtConfig.observe { `UnnamedRule`.fmt(args) }

        /** 编辑 */
        val `Edit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit"""
                        RootMLangTags.ZH -> """编辑"""
                        else -> null
                    }
                } ?: """编辑"""

        /** 编辑 */
        @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

        /** 拖拽排序 */
        val `DragToSort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Drag to Sort"""
                        RootMLangTags.ZH -> """拖拽排序"""
                        else -> null
                    }
                } ?: """拖拽排序"""

        /** 拖拽排序 */
        @Composable
        fun `DragToSort`(vararg args: Any?) = FYTxtConfig.observe { `DragToSort`.fmt(args) }

        /** 取消删除 */
        val `CancelDelete`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cancel Delete"""
                        RootMLangTags.ZH -> """取消删除"""
                        else -> null
                    }
                } ?: """取消删除"""

        /** 取消删除 */
        @Composable
        fun `CancelDelete`(vararg args: Any?) = FYTxtConfig.observe { `CancelDelete`.fmt(args) }

        /** 删除已选条目 */
        val `DeleteSelected`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete Selected"""
                        RootMLangTags.ZH -> """删除已选条目"""
                        else -> null
                    }
                } ?: """删除已选条目"""

        /** 删除已选条目 */
        @Composable
        fun `DeleteSelected`(vararg args: Any?) = FYTxtConfig.observe { `DeleteSelected`.fmt(args) }

        /** 删除已选%s */
        val `DeleteSelectedNamedItem`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete selected %s"""
                        RootMLangTags.ZH -> """删除已选%s"""
                        else -> null
                    }
                } ?: """删除已选%s"""

        /** 删除已选%s */
        @Composable
        fun `DeleteSelectedNamedItem`(vararg args: Any?) =
            FYTxtConfig.observe { `DeleteSelectedNamedItem`.fmt(args) }

        /** 删除已选规则 */
        val `DeleteSelectedRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete Selected Rules"""
                        RootMLangTags.ZH -> """删除已选规则"""
                        else -> null
                    }
                } ?: """删除已选规则"""

        /** 删除已选规则 */
        @Composable
        fun `DeleteSelectedRules`(vararg args: Any?) =
            FYTxtConfig.observe { `DeleteSelectedRules`.fmt(args) }

        /** 清空当前模式 */
        val `ClearMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Clear Current Mode"""
                        RootMLangTags.ZH -> """清空当前模式"""
                        else -> null
                    }
                } ?: """清空当前模式"""

        /** 清空当前模式 */
        @Composable
        fun `ClearMode`(vararg args: Any?) = FYTxtConfig.observe { `ClearMode`.fmt(args) }

        /** 进入删除模式进入删除模式 */
        val `EnterDeleteMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enter Delete Mode"""
                        RootMLangTags.ZH -> """进入删除模式"""
                        else -> null
                    }
                } ?: """进入删除模式"""

        /** 进入删除模式 */
        @Composable
        fun `EnterDeleteMode`(vararg args: Any?) =
            FYTxtConfig.observe { `EnterDeleteMode`.fmt(args) }

        /** 空字符串 */
        val `EmptyString`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Empty String"""
                        RootMLangTags.ZH -> """空字符串"""
                        else -> null
                    }
                } ?: """空字符串"""

        /** 空字符串 */
        @Composable
        fun `EmptyString`(vararg args: Any?) = FYTxtConfig.observe { `EmptyString`.fmt(args) }

        /** 数组 %d 项 */
        val `ArrayItems`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Array %d items"""
                        RootMLangTags.ZH -> """数组 %d 项"""
                        else -> null
                    }
                } ?: """数组 %d 项"""

        /** 数组 %d 项 */
        @Composable
        fun `ArrayItems`(vararg args: Any?) = FYTxtConfig.observe { `ArrayItems`.fmt(args) }

        /** 对象 %d 个字段 */
        val `ObjectFields`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Object %d fields"""
                        RootMLangTags.ZH -> """对象 %d 个字段"""
                        else -> null
                    }
                } ?: """对象 %d 个字段"""

        /** 对象 %d 个字段 */
        @Composable
        fun `ObjectFields`(vararg args: Any?) = FYTxtConfig.observe { `ObjectFields`.fmt(args) }

        /** 清空 */
        val `Clear`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Clear"""
                        RootMLangTags.ZH -> """清空"""
                        else -> null
                    }
                } ?: """清空"""

        /** 清空 */
        @Composable fun `Clear`(vararg args: Any?) = FYTxtConfig.observe { `Clear`.fmt(args) }

        /** 规则 */
        val `Rules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rules"""
                        RootMLangTags.ZH -> """规则"""
                        else -> null
                    }
                } ?: """规则"""

        /** 规则 */
        @Composable fun `Rules`(vararg args: Any?) = FYTxtConfig.observe { `Rules`.fmt(args) }

        /** 添加自定义 */
        val `AddCustom`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Custom"""
                        RootMLangTags.ZH -> """添加自定义"""
                        else -> null
                    }
                } ?: """添加自定义"""

        /** 添加自定义 */
        @Composable
        fun `AddCustom`(vararg args: Any?) = FYTxtConfig.observe { `AddCustom`.fmt(args) }

        /** 内容不能为空 */
        val `ContentEmpty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Content cannot be empty"""
                        RootMLangTags.ZH -> """内容不能为空"""
                        else -> null
                    }
                } ?: """内容不能为空"""

        /** 内容不能为空 */
        @Composable
        fun `ContentEmpty`(vararg args: Any?) = FYTxtConfig.observe { `ContentEmpty`.fmt(args) }

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

        /** 每行一个条目 */
        val `OneItemPerLine`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enter one item per line; blank lines are ignored"""
                        RootMLangTags.ZH -> """每行填写一个条目；空行会被忽略"""
                        else -> null
                    }
                } ?: """每行填写一个条目；空行会被忽略"""

        /** 每行填写一个条目；空行会被忽略 */
        @Composable
        fun `OneItemPerLine`(vararg args: Any?) = FYTxtConfig.observe { `OneItemPerLine`.fmt(args) }

        /** 新增一项 */
        val `AddItem`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Item"""
                        RootMLangTags.ZH -> """新增一项"""
                        else -> null
                    }
                } ?: """新增一项"""

        /** 新增一项 */
        @Composable fun `AddItem`(vararg args: Any?) = FYTxtConfig.observe { `AddItem`.fmt(args) }

        /** 删除最后一项 */
        val `DeleteLastItem`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete Last Item"""
                        RootMLangTags.ZH -> """删除最后一项"""
                        else -> null
                    }
                } ?: """删除最后一项"""

        /** 删除最后一项 */
        @Composable
        fun `DeleteLastItem`(vararg args: Any?) = FYTxtConfig.observe { `DeleteLastItem`.fmt(args) }

        /** 复制 */
        val `Copy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Copy"""
                        RootMLangTags.ZH -> """复制"""
                        else -> null
                    }
                } ?: """复制"""

        /** 复制 */
        @Composable fun `Copy`(vararg args: Any?) = FYTxtConfig.observe { `Copy`.fmt(args) }

        /** 删除 */
        val `Delete`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete"""
                        RootMLangTags.ZH -> """删除"""
                        else -> null
                    }
                } ?: """删除"""

        /** 删除 */
        @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

        /** 上移 */
        val `MoveUp`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Move Up"""
                        RootMLangTags.ZH -> """上移"""
                        else -> null
                    }
                } ?: """上移"""

        /** 上移 */
        @Composable fun `MoveUp`(vararg args: Any?) = FYTxtConfig.observe { `MoveUp`.fmt(args) }

        /** 下移 */
        val `MoveDown`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Move Down"""
                        RootMLangTags.ZH -> """下移"""
                        else -> null
                    }
                } ?: """下移"""

        /** 下移 */
        @Composable fun `MoveDown`(vararg args: Any?) = FYTxtConfig.observe { `MoveDown`.fmt(args) }

        /** 新增对象 */
        val `AddObject`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Object"""
                        RootMLangTags.ZH -> """新增对象"""
                        else -> null
                    }
                } ?: """新增对象"""

        /** 新增对象 */
        @Composable
        fun `AddObject`(vararg args: Any?) = FYTxtConfig.observe { `AddObject`.fmt(args) }

        /** 子规则名称 */
        val `SubRuleName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Sub Rule Name"""
                        RootMLangTags.ZH -> """子规则名称"""
                        else -> null
                    }
                } ?: """子规则名称"""

        /** 子规则名称 */
        @Composable
        fun `SubRuleName`(vararg args: Any?) = FYTxtConfig.observe { `SubRuleName`.fmt(args) }

        /** 暂无规则 */
        val `NoRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No rules"""
                        RootMLangTags.ZH -> """暂无规则"""
                        else -> null
                    }
                } ?: """暂无规则"""

        /** 暂无规则 */
        @Composable fun `NoRules`(vararg args: Any?) = FYTxtConfig.observe { `NoRules`.fmt(args) }

        /** 已配置 %d 条规则 */
        val `RulesConfiguredInline`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d rules configured"""
                        RootMLangTags.ZH -> """已配置 %d 条规则"""
                        else -> null
                    }
                } ?: """已配置 %d 条规则"""

        /** 已配置 %d 条规则 */
        @Composable
        fun `RulesConfiguredInline`(vararg args: Any?) =
            FYTxtConfig.observe { `RulesConfiguredInline`.fmt(args) }

        /** 新增子规则组 */
        val `AddSubRuleGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Sub Rule Group"""
                        RootMLangTags.ZH -> """新增子规则组"""
                        else -> null
                    }
                } ?: """新增子规则组"""

        /** 新增子规则组 */
        @Composable
        fun `AddSubRuleGroup`(vararg args: Any?) =
            FYTxtConfig.observe { `AddSubRuleGroup`.fmt(args) }

        /** 编辑子规则 */
        val `EditSubRule`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Sub Rule"""
                        RootMLangTags.ZH -> """编辑子规则"""
                        else -> null
                    }
                } ?: """编辑子规则"""

        /** 编辑子规则 */
        @Composable
        fun `EditSubRule`(vararg args: Any?) = FYTxtConfig.observe { `EditSubRule`.fmt(args) }

        /** 键名 */
        val `KeyName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Key Name"""
                        RootMLangTags.ZH -> """键名"""
                        else -> null
                    }
                } ?: """键名"""

        /** 键名 */
        @Composable fun `KeyName`(vararg args: Any?) = FYTxtConfig.observe { `KeyName`.fmt(args) }

        /** 列表 */
        val `List`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """List"""
                        RootMLangTags.ZH -> """列表"""
                        else -> null
                    }
                } ?: """列表"""

        /** 列表 */
        @Composable fun `List`(vararg args: Any?) = FYTxtConfig.observe { `List`.fmt(args) }

        /** 编辑条目 */
        val `EditItem`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Item"""
                        RootMLangTags.ZH -> """编辑条目"""
                        else -> null
                    }
                } ?: """编辑条目"""

        /** 编辑条目 */
        @Composable fun `EditItem`(vararg args: Any?) = FYTxtConfig.observe { `EditItem`.fmt(args) }

        /** 清空当前模式 */
        val `ClearCurrentMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Clear Current Mode"""
                        RootMLangTags.ZH -> """清空当前模式"""
                        else -> null
                    }
                } ?: """清空当前模式"""

        /** 清空当前模式 */
        @Composable
        fun `ClearCurrentMode`(vararg args: Any?) =
            FYTxtConfig.observe { `ClearCurrentMode`.fmt(args) }

        /** 新增代理节点 */
        val `NewProxyNode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Proxy Node"""
                        RootMLangTags.ZH -> """新增代理节点"""
                        else -> null
                    }
                } ?: """新增代理节点"""

        /** 新增代理节点 */
        @Composable
        fun `NewProxyNode`(vararg args: Any?) = FYTxtConfig.observe { `NewProxyNode`.fmt(args) }

        /** 新增策略组 */
        val `NewProxyGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Proxy Group"""
                        RootMLangTags.ZH -> """新增策略组"""
                        else -> null
                    }
                } ?: """新增策略组"""

        /** 新增策略组 */
        @Composable
        fun `NewProxyGroup`(vararg args: Any?) = FYTxtConfig.observe { `NewProxyGroup`.fmt(args) }

        /** 编辑代理节点 */
        val `EditProxyNode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Proxy Node"""
                        RootMLangTags.ZH -> """编辑代理节点"""
                        else -> null
                    }
                } ?: """编辑代理节点"""

        /** 编辑代理节点 */
        @Composable
        fun `EditProxyNode`(vararg args: Any?) = FYTxtConfig.observe { `EditProxyNode`.fmt(args) }

        /** 编辑策略组 */
        val `EditProxyGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Proxy Group"""
                        RootMLangTags.ZH -> """编辑策略组"""
                        else -> null
                    }
                } ?: """编辑策略组"""

        /** 编辑策略组 */
        @Composable
        fun `EditProxyGroup`(vararg args: Any?) = FYTxtConfig.observe { `EditProxyGroup`.fmt(args) }

        /** 未命名代理节点 */
        val `UnnamedProxyNode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unnamed Proxy Node"""
                        RootMLangTags.ZH -> """未命名代理节点"""
                        else -> null
                    }
                } ?: """未命名代理节点"""

        /** 未命名代理节点 */
        @Composable
        fun `UnnamedProxyNode`(vararg args: Any?) =
            FYTxtConfig.observe { `UnnamedProxyNode`.fmt(args) }

        /** 未命名策略组 */
        val `UnnamedProxyGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unnamed Proxy Group"""
                        RootMLangTags.ZH -> """未命名策略组"""
                        else -> null
                    }
                } ?: """未命名策略组"""

        /** 未命名策略组 */
        @Composable
        fun `UnnamedProxyGroup`(vararg args: Any?) =
            FYTxtConfig.observe { `UnnamedProxyGroup`.fmt(args) }

        /** 代理节点 */
        val `ProxyNode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Node"""
                        RootMLangTags.ZH -> """代理节点"""
                        else -> null
                    }
                } ?: """代理节点"""

        /** 代理节点 */
        @Composable
        fun `ProxyNode`(vararg args: Any?) = FYTxtConfig.observe { `ProxyNode`.fmt(args) }

        /** 策略组 */
        val `ProxyGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Group"""
                        RootMLangTags.ZH -> """策略组"""
                        else -> null
                    }
                } ?: """策略组"""

        /** 策略组 */
        @Composable
        fun `ProxyGroup`(vararg args: Any?) = FYTxtConfig.observe { `ProxyGroup`.fmt(args) }

        /** 规则编辑 */
        val `RuleEdit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule Edit"""
                        RootMLangTags.ZH -> """规则编辑"""
                        else -> null
                    }
                } ?: """规则编辑"""

        /** 规则编辑 */
        @Composable fun `RuleEdit`(vararg args: Any?) = FYTxtConfig.observe { `RuleEdit`.fmt(args) }

        /** 子规则目标 */
        val `SubRuleTarget`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Sub Rule Target"""
                        RootMLangTags.ZH -> """子规则目标"""
                        else -> null
                    }
                } ?: """子规则目标"""

        /** 子规则目标 */
        @Composable
        fun `SubRuleTarget`(vararg args: Any?) = FYTxtConfig.observe { `SubRuleTarget`.fmt(args) }

        /** 策略组目标 */
        val `ProxyGroupTarget`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Group Target"""
                        RootMLangTags.ZH -> """策略组目标"""
                        else -> null
                    }
                } ?: """策略组目标"""

        /** 策略组目标 */
        @Composable
        fun `ProxyGroupTarget`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyGroupTarget`.fmt(args) }

        /** 规则类型不能为空 */
        val `RuleTypeEmpty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule type cannot be empty"""
                        RootMLangTags.ZH -> """规则类型不能为空"""
                        else -> null
                    }
                } ?: """规则类型不能为空"""

        /** 规则类型不能为空 */
        @Composable
        fun `RuleTypeEmpty`(vararg args: Any?) = FYTxtConfig.observe { `RuleTypeEmpty`.fmt(args) }

        /** 匹配内容不能为空 */
        val `PayloadEmpty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Payload cannot be empty"""
                        RootMLangTags.ZH -> """匹配内容不能为空"""
                        else -> null
                    }
                } ?: """匹配内容不能为空"""

        /** 匹配内容不能为空 */
        @Composable
        fun `PayloadEmpty`(vararg args: Any?) = FYTxtConfig.observe { `PayloadEmpty`.fmt(args) }

        /** 目标不能为空 */
        val `TargetEmpty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Target cannot be empty"""
                        RootMLangTags.ZH -> """目标不能为空"""
                        else -> null
                    }
                } ?: """目标不能为空"""

        /** 目标不能为空 */
        @Composable
        fun `TargetEmpty`(vararg args: Any?) = FYTxtConfig.observe { `TargetEmpty`.fmt(args) }

        /** 匹配结果 */
        val `MatchResult`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Match Result"""
                        RootMLangTags.ZH -> """匹配结果"""
                        else -> null
                    }
                } ?: """匹配结果"""

        /** 匹配结果 */
        @Composable
        fun `MatchResult`(vararg args: Any?) = FYTxtConfig.observe { `MatchResult`.fmt(args) }

        /** 选择匹配结果 */
        val `SelectMatchResult`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select Match Result"""
                        RootMLangTags.ZH -> """选择匹配结果"""
                        else -> null
                    }
                } ?: """选择匹配结果"""

        /** 选择匹配结果 */
        @Composable
        fun `SelectMatchResult`(vararg args: Any?) =
            FYTxtConfig.observe { `SelectMatchResult`.fmt(args) }

        /** 自定义匹配结果 */
        val `CustomMatchResult`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Custom Match Result"""
                        RootMLangTags.ZH -> """自定义匹配结果"""
                        else -> null
                    }
                } ?: """自定义匹配结果"""

        /** 自定义匹配结果 */
        @Composable
        fun `CustomMatchResult`(vararg args: Any?) =
            FYTxtConfig.observe { `CustomMatchResult`.fmt(args) }

        /** 选择子规则目标 */
        val `SelectSubRuleTarget`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select Sub Rule Target"""
                        RootMLangTags.ZH -> """选择子规则目标"""
                        else -> null
                    }
                } ?: """选择子规则目标"""

        /** 选择子规则目标 */
        @Composable
        fun `SelectSubRuleTarget`(vararg args: Any?) =
            FYTxtConfig.observe { `SelectSubRuleTarget`.fmt(args) }

        /** 选择策略组目标 */
        val `SelectProxyGroupTarget`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select Proxy Group Target"""
                        RootMLangTags.ZH -> """选择策略组目标"""
                        else -> null
                    }
                } ?: """选择策略组目标"""

        /** 选择策略组目标 */
        @Composable
        fun `SelectProxyGroupTarget`(vararg args: Any?) =
            FYTxtConfig.observe { `SelectProxyGroupTarget`.fmt(args) }

        /** 自定义子规则目标 */
        val `CustomSubRuleTarget`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Custom Sub Rule Target"""
                        RootMLangTags.ZH -> """自定义子规则目标"""
                        else -> null
                    }
                } ?: """自定义子规则目标"""

        /** 自定义子规则目标 */
        @Composable
        fun `CustomSubRuleTarget`(vararg args: Any?) =
            FYTxtConfig.observe { `CustomSubRuleTarget`.fmt(args) }

        /** 自定义策略组目标 */
        val `CustomProxyGroupTarget`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Custom Proxy Group Target"""
                        RootMLangTags.ZH -> """自定义策略组目标"""
                        else -> null
                    }
                } ?: """自定义策略组目标"""

        /** 自定义策略组目标 */
        @Composable
        fun `CustomProxyGroupTarget`(vararg args: Any?) =
            FYTxtConfig.observe { `CustomProxyGroupTarget`.fmt(args) }

        /** 选择规则提供者 */
        val `SelectRuleProvider`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select Rule Provider"""
                        RootMLangTags.ZH -> """选择规则提供者"""
                        else -> null
                    }
                } ?: """选择规则提供者"""

        /** 选择规则提供者 */
        @Composable
        fun `SelectRuleProvider`(vararg args: Any?) =
            FYTxtConfig.observe { `SelectRuleProvider`.fmt(args) }

        /** 规则主体 */
        val `RuleBody`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule Body"""
                        RootMLangTags.ZH -> """规则主体"""
                        else -> null
                    }
                } ?: """规则主体"""

        /** 规则主体 */
        @Composable fun `RuleBody`(vararg args: Any?) = FYTxtConfig.observe { `RuleBody`.fmt(args) }

        /** 类型 */
        val `RuleType`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Type"""
                        RootMLangTags.ZH -> """类型"""
                        else -> null
                    }
                } ?: """类型"""

        /** 类型 */
        @Composable fun `RuleType`(vararg args: Any?) = FYTxtConfig.observe { `RuleType`.fmt(args) }

        /** 匹配内容 */
        val `Payload`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Payload"""
                        RootMLangTags.ZH -> """匹配内容"""
                        else -> null
                    }
                } ?: """匹配内容"""

        /** 匹配内容 */
        @Composable fun `Payload`(vararg args: Any?) = FYTxtConfig.observe { `Payload`.fmt(args) }

        /** 附加参数 */
        val `AdditionalParams`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Additional Params"""
                        RootMLangTags.ZH -> """附加参数"""
                        else -> null
                    }
                } ?: """附加参数"""

        /** 附加参数 */
        @Composable
        fun `AdditionalParams`(vararg args: Any?) =
            FYTxtConfig.observe { `AdditionalParams`.fmt(args) }

        /** 基础连接 */
        val `BasicConnection`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Basic Connection"""
                        RootMLangTags.ZH -> """基础连接"""
                        else -> null
                    }
                } ?: """基础连接"""

        /** 基础连接 */
        @Composable
        fun `BasicConnection`(vararg args: Any?) =
            FYTxtConfig.observe { `BasicConnection`.fmt(args) }

        /** 网络与路由 */
        val `NetworkAndRoute`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Network & Route"""
                        RootMLangTags.ZH -> """网络与路由"""
                        else -> null
                    }
                } ?: """网络与路由"""

        /** 网络与路由 */
        @Composable
        fun `NetworkAndRoute`(vararg args: Any?) =
            FYTxtConfig.observe { `NetworkAndRoute`.fmt(args) }

        /** 留空表示不覆写端口 */
        val `PortEmptyHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Leave empty to not override port"""
                        RootMLangTags.ZH -> """留空表示不覆写端口"""
                        else -> null
                    }
                } ?: """留空表示不覆写端口"""

        /** 留空表示不覆写端口 */
        @Composable
        fun `PortEmptyHint`(vararg args: Any?) = FYTxtConfig.observe { `PortEmptyHint`.fmt(args) }

        /** 类型不能为空 */
        val `TypeEmpty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Type cannot be empty"""
                        RootMLangTags.ZH -> """类型不能为空"""
                        else -> null
                    }
                } ?: """类型不能为空"""

        /** 类型不能为空 */
        @Composable
        fun `TypeEmpty`(vararg args: Any?) = FYTxtConfig.observe { `TypeEmpty`.fmt(args) }

        /** 成员来源 */
        val `MemberSource`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Member Source"""
                        RootMLangTags.ZH -> """成员来源"""
                        else -> null
                    }
                } ?: """成员来源"""

        /** 成员来源 */
        @Composable
        fun `MemberSource`(vararg args: Any?) = FYTxtConfig.observe { `MemberSource`.fmt(args) }

        /** 健康检查与过滤 */
        val `HealthCheckAndFilter`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Health Check & Filter"""
                        RootMLangTags.ZH -> """健康检查与过滤"""
                        else -> null
                    }
                } ?: """健康检查与过滤"""

        /** 健康检查与过滤 */
        @Composable
        fun `HealthCheckAndFilter`(vararg args: Any?) =
            FYTxtConfig.observe { `HealthCheckAndFilter`.fmt(args) }

        /** 选择策略组成员 */
        val `SelectProxyGroupMember`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select Proxy Group Member"""
                        RootMLangTags.ZH -> """选择策略组成员"""
                        else -> null
                    }
                } ?: """选择策略组成员"""

        /** 选择策略组成员 */
        @Composable
        fun `SelectProxyGroupMember`(vararg args: Any?) =
            FYTxtConfig.observe { `SelectProxyGroupMember`.fmt(args) }

        /** 自定义成员 */
        val `CustomMember`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Custom Member"""
                        RootMLangTags.ZH -> """自定义成员"""
                        else -> null
                    }
                } ?: """自定义成员"""

        /** 自定义成员 */
        @Composable
        fun `CustomMember`(vararg args: Any?) = FYTxtConfig.observe { `CustomMember`.fmt(args) }

        /** 保存代理节点 */
        val `SaveProxyNode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Save Proxy Node"""
                        RootMLangTags.ZH -> """保存代理节点"""
                        else -> null
                    }
                } ?: """保存代理节点"""

        /** 保存代理节点 */
        @Composable
        fun `SaveProxyNode`(vararg args: Any?) = FYTxtConfig.observe { `SaveProxyNode`.fmt(args) }

        /** 保存策略组 */
        val `SaveProxyGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Save Proxy Group"""
                        RootMLangTags.ZH -> """保存策略组"""
                        else -> null
                    }
                } ?: """保存策略组"""

        /** 保存策略组 */
        @Composable
        fun `SaveProxyGroup`(vararg args: Any?) = FYTxtConfig.observe { `SaveProxyGroup`.fmt(args) }

        /** 保存规则 */
        val `SaveRule`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Save Rule"""
                        RootMLangTags.ZH -> """保存规则"""
                        else -> null
                    }
                } ?: """保存规则"""

        /** 保存规则 */
        @Composable fun `SaveRule`(vararg args: Any?) = FYTxtConfig.observe { `SaveRule`.fmt(args) }

        /** 输入框是自定义内容；留空时使用下面选中的规则提供者 */
        val `RuleProviderInputHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """This field accepts a custom payload. Leave it empty to use the selected rule provider below"""
                        RootMLangTags.ZH -> """这里填写的是自定义 payload；留空时改为使用下方选中的规则提供者"""
                        else -> null
                    }
                } ?: """这里填写的是自定义 payload；留空时改为使用下方选中的规则提供者"""

        /** 这里填写的是自定义 payload；留空时改为使用下方选中的规则提供者 */
        @Composable
        fun `RuleProviderInputHint`(vararg args: Any?) =
            FYTxtConfig.observe { `RuleProviderInputHint`.fmt(args) }

        /** 逻辑规则可直接填写完整 payload */
        val `LogicalRuleHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Logical rules should fill in the complete payload directly"""
                        RootMLangTags.ZH -> """逻辑规则需要直接填写完整 payload"""
                        else -> null
                    }
                } ?: """逻辑规则需要直接填写完整 payload"""

        /** 逻辑规则需要直接填写完整 payload */
        @Composable
        fun `LogicalRuleHint`(vararg args: Any?) =
            FYTxtConfig.observe { `LogicalRuleHint`.fmt(args) }

        /** 其他附加参数，多个值用逗号分隔 */
        val `OtherExtraParams`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Other extra params, comma-separated"""
                        RootMLangTags.ZH -> """其他附加参数，多个值用逗号分隔"""
                        else -> null
                    }
                } ?: """其他附加参数，多个值用逗号分隔"""

        /** 其他附加参数，多个值用逗号分隔 */
        @Composable
        fun `OtherExtraParams`(vararg args: Any?) =
            FYTxtConfig.observe { `OtherExtraParams`.fmt(args) }

        /** 例如 src,no-resolve 之外的额外参数 逻辑规则请直接填写完整 payload，例如 ((DOMAIN,google.com),(NETWORK,udp))。 */
        val `ExtraParamsHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Extra parameters other than src and no-resolve.
For logical rules, enter the complete payload here, e.g. ((DOMAIN,google.com),(NETWORK,udp))."""
                        RootMLangTags.ZH ->
                            """这里填写除 src、no-resolve 之外的附加参数。
逻辑规则请直接在这里填写完整 payload，例如 ((DOMAIN,google.com),(NETWORK,udp))。"""
                        else -> null
                    }
                }
                    ?: """这里填写除 src、no-resolve 之外的附加参数。
逻辑规则请直接在这里填写完整 payload，例如 ((DOMAIN,google.com),(NETWORK,udp))。"""

        /**
         * 这里填写除 src、no-resolve 之外的附加参数。 逻辑规则请直接在这里填写完整 payload，例如
         * ((DOMAIN,google.com),(NETWORK,udp))。
         */
        @Composable
        fun `ExtraParamsHint`(vararg args: Any?) =
            FYTxtConfig.observe { `ExtraParamsHint`.fmt(args) }
    }

    object `Draft` {
        init {
            RootMLangGroups
        }

        /** 对象 */
        val `Object`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Object"""
                        RootMLangTags.ZH -> """对象"""
                        else -> null
                    }
                } ?: """对象"""

        /** 对象 */
        @Composable fun `Object`(vararg args: Any?) = FYTxtConfig.observe { `Object`.fmt(args) }

        /** 子规则组 */
        val `SubRuleGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Sub Rule Group"""
                        RootMLangTags.ZH -> """子规则组"""
                        else -> null
                    }
                } ?: """子规则组"""

        /** 子规则组 */
        @Composable
        fun `SubRuleGroup`(vararg args: Any?) = FYTxtConfig.observe { `SubRuleGroup`.fmt(args) }

        /** 名称 */
        val `Name`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Name"""
                        RootMLangTags.ZH -> """名称"""
                        else -> null
                    }
                } ?: """名称"""

        /** 名称 */
        @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }

        /** 名称不能为空 */
        val `NameRequired`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Name cannot be empty"""
                        RootMLangTags.ZH -> """名称不能为空"""
                        else -> null
                    }
                } ?: """名称不能为空"""

        /** 名称不能为空 */
        @Composable
        fun `NameRequired`(vararg args: Any?) = FYTxtConfig.observe { `NameRequired`.fmt(args) }

        /** 保存 */
        val `Save`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Save"""
                        RootMLangTags.ZH -> """保存"""
                        else -> null
                    }
                } ?: """保存"""

        /** 保存 */
        @Composable fun `Save`(vararg args: Any?) = FYTxtConfig.observe { `Save`.fmt(args) }

        /** 基础信息 */
        val `BasicInfo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Basic Info"""
                        RootMLangTags.ZH -> """基础信息"""
                        else -> null
                    }
                } ?: """基础信息"""

        /** 基础信息 */
        @Composable
        fun `BasicInfo`(vararg args: Any?) = FYTxtConfig.observe { `BasicInfo`.fmt(args) }

        /** 基础身份 */
        val `BasicIdentity`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Basic Identity"""
                        RootMLangTags.ZH -> """基础身份"""
                        else -> null
                    }
                } ?: """基础身份"""

        /** 基础身份 */
        @Composable
        fun `BasicIdentity`(vararg args: Any?) = FYTxtConfig.observe { `BasicIdentity`.fmt(args) }

        /** 核心来源 */
        val `CoreSource`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Core Source"""
                        RootMLangTags.ZH -> """核心来源"""
                        else -> null
                    }
                } ?: """核心来源"""

        /** 核心来源 */
        @Composable
        fun `CoreSource`(vararg args: Any?) = FYTxtConfig.observe { `CoreSource`.fmt(args) }

        /** 网络与认证 */
        val `NetworkAuth`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Network & Auth"""
                        RootMLangTags.ZH -> """网络与认证"""
                        else -> null
                    }
                } ?: """网络与认证"""

        /** 网络与认证 */
        @Composable
        fun `NetworkAuth`(vararg args: Any?) = FYTxtConfig.observe { `NetworkAuth`.fmt(args) }

        /** 类型 */
        val `Type`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Type"""
                        RootMLangTags.ZH -> """类型"""
                        else -> null
                    }
                } ?: """类型"""

        /** 类型 */
        @Composable fun `Type`(vararg args: Any?) = FYTxtConfig.observe { `Type`.fmt(args) }

        /** 载体 */
        val `Vehicle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Vehicle"""
                        RootMLangTags.ZH -> """载体"""
                        else -> null
                    }
                } ?: """载体"""

        /** 载体 */
        @Composable fun `Vehicle`(vararg args: Any?) = FYTxtConfig.observe { `Vehicle`.fmt(args) }

        /** 行为 */
        val `Behavior`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Behavior"""
                        RootMLangTags.ZH -> """行为"""
                        else -> null
                    }
                } ?: """行为"""

        /** 行为 */
        @Composable fun `Behavior`(vararg args: Any?) = FYTxtConfig.observe { `Behavior`.fmt(args) }

        /** 格式 */
        val `Format`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Format"""
                        RootMLangTags.ZH -> """格式"""
                        else -> null
                    }
                } ?: """格式"""

        /** 格式 */
        @Composable fun `Format`(vararg args: Any?) = FYTxtConfig.observe { `Format`.fmt(args) }

        /** 健康检查 */
        val `HealthCheck`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Health Check"""
                        RootMLangTags.ZH -> """健康检查"""
                        else -> null
                    }
                } ?: """健康检查"""

        /** 健康检查 */
        @Composable
        fun `HealthCheck`(vararg args: Any?) = FYTxtConfig.observe { `HealthCheck`.fmt(args) }

        /** 覆写 */
        val `Override`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override"""
                        RootMLangTags.ZH -> """覆写"""
                        else -> null
                    }
                } ?: """覆写"""

        /** 覆写 */
        @Composable fun `Override`(vararg args: Any?) = FYTxtConfig.observe { `Override`.fmt(args) }

        /** 规则列表 */
        val `RuleList`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule List"""
                        RootMLangTags.ZH -> """规则列表"""
                        else -> null
                    }
                } ?: """规则列表"""

        /** 规则列表 */
        @Composable fun `RuleList`(vararg args: Any?) = FYTxtConfig.observe { `RuleList`.fmt(args) }

        /** 未配置规则 */
        val `NoRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No rules configured"""
                        RootMLangTags.ZH -> """未配置规则"""
                        else -> null
                    }
                } ?: """未配置规则"""

        /** 未配置规则 */
        @Composable fun `NoRules`(vararg args: Any?) = FYTxtConfig.observe { `NoRules`.fmt(args) }

        /** 已配置 %d 条规则 */
        val `RulesConfigured`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d rules configured"""
                        RootMLangTags.ZH -> """已配置 %d 条规则"""
                        else -> null
                    }
                } ?: """已配置 %d 条规则"""

        /** 已配置 %d 条规则 */
        @Composable
        fun `RulesConfigured`(vararg args: Any?) =
            FYTxtConfig.observe { `RulesConfigured`.fmt(args) }

        /** 编辑子规则 */
        val `EditSubRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Sub Rules"""
                        RootMLangTags.ZH -> """编辑子规则"""
                        else -> null
                    }
                } ?: """编辑子规则"""

        /** 编辑子规则 */
        @Composable
        fun `EditSubRules`(vararg args: Any?) = FYTxtConfig.observe { `EditSubRules`.fmt(args) }

        /** 额外字段 */
        val `ExtraFields`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Extra Fields"""
                        RootMLangTags.ZH -> """额外字段"""
                        else -> null
                    }
                } ?: """额外字段"""

        /** 额外字段 */
        @Composable
        fun `ExtraFields`(vararg args: Any?) = FYTxtConfig.observe { `ExtraFields`.fmt(args) }

        /** 新增额外字段 */
        val `AddExtraField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Extra Field"""
                        RootMLangTags.ZH -> """新增额外字段"""
                        else -> null
                    }
                } ?: """新增额外字段"""

        /** 新增额外字段 */
        @Composable
        fun `AddExtraField`(vararg args: Any?) = FYTxtConfig.observe { `AddExtraField`.fmt(args) }

        /** 编辑额外字段 */
        val `EditExtraField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Extra Field"""
                        RootMLangTags.ZH -> """编辑额外字段"""
                        else -> null
                    }
                } ?: """编辑额外字段"""

        /** 编辑额外字段 */
        @Composable
        fun `EditExtraField`(vararg args: Any?) = FYTxtConfig.observe { `EditExtraField`.fmt(args) }

        /** 新增 health-check 额外字段 */
        val `AddHealthCheckField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add health-check extra field"""
                        RootMLangTags.ZH -> """新增 health-check 额外字段"""
                        else -> null
                    }
                } ?: """新增 health-check 额外字段"""

        /** 新增 health-check 额外字段 */
        @Composable
        fun `AddHealthCheckField`(vararg args: Any?) =
            FYTxtConfig.observe { `AddHealthCheckField`.fmt(args) }

        /** 编辑 health-check 额外字段 */
        val `EditHealthCheckField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit health-check extra field"""
                        RootMLangTags.ZH -> """编辑 health-check 额外字段"""
                        else -> null
                    }
                } ?: """编辑 health-check 额外字段"""

        /** 编辑 health-check 额外字段 */
        @Composable
        fun `EditHealthCheckField`(vararg args: Any?) =
            FYTxtConfig.observe { `EditHealthCheckField`.fmt(args) }

        /** 新增 override 额外字段 */
        val `AddOverrideField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add override extra field"""
                        RootMLangTags.ZH -> """新增 override 额外字段"""
                        else -> null
                    }
                } ?: """新增 override 额外字段"""

        /** 新增 override 额外字段 */
        @Composable
        fun `AddOverrideField`(vararg args: Any?) =
            FYTxtConfig.observe { `AddOverrideField`.fmt(args) }

        /** 编辑 override 额外字段 */
        val `EditOverrideField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit override extra field"""
                        RootMLangTags.ZH -> """编辑 override 额外字段"""
                        else -> null
                    }
                } ?: """编辑 override 额外字段"""

        /** 编辑 override 额外字段 */
        @Composable
        fun `EditOverrideField`(vararg args: Any?) =
            FYTxtConfig.observe { `EditOverrideField`.fmt(args) }

        /** Health Check 开关 */
        val `HealthCheckSwitch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Health Check Switch"""
                        RootMLangTags.ZH -> """Health Check 开关"""
                        else -> null
                    }
                } ?: """Health Check 开关"""

        /** Health Check 开关 */
        @Composable
        fun `HealthCheckSwitch`(vararg args: Any?) =
            FYTxtConfig.observe { `HealthCheckSwitch`.fmt(args) }

        /** Health Check 额外字段 */
        val `HealthCheckFields`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Health Check Extra Fields"""
                        RootMLangTags.ZH -> """Health Check 额外字段"""
                        else -> null
                    }
                } ?: """Health Check 额外字段"""

        /** Health Check 额外字段 */
        @Composable
        fun `HealthCheckFields`(vararg args: Any?) =
            FYTxtConfig.observe { `HealthCheckFields`.fmt(args) }

        /** Override 开关 */
        val `OverrideSwitch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override Switch"""
                        RootMLangTags.ZH -> """Override 开关"""
                        else -> null
                    }
                } ?: """Override 开关"""

        /** Override 开关 */
        @Composable
        fun `OverrideSwitch`(vararg args: Any?) = FYTxtConfig.observe { `OverrideSwitch`.fmt(args) }

        /** Override 额外字段 */
        val `OverrideFields`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override Extra Fields"""
                        RootMLangTags.ZH -> """Override 额外字段"""
                        else -> null
                    }
                } ?: """Override 额外字段"""

        /** Override 额外字段 */
        @Composable
        fun `OverrideFields`(vararg args: Any?) = FYTxtConfig.observe { `OverrideFields`.fmt(args) }

        /** 布尔选项 */
        val `BooleanOptions`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Boolean Options"""
                        RootMLangTags.ZH -> """布尔选项"""
                        else -> null
                    }
                } ?: """布尔选项"""

        /** 布尔选项 */
        @Composable
        fun `BooleanOptions`(vararg args: Any?) = FYTxtConfig.observe { `BooleanOptions`.fmt(args) }

        /** 每行一个 header，格式：Key: value1 | value2 */
        val `HeaderHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Enter one header per line in the form Key: value. If a header has multiple values, separate them with |"""
                        RootMLangTags.ZH ->
                            """每行填写一个 header，写法为 Key: value。一个 header 有多个值时，用 | 分隔"""
                        else -> null
                    }
                } ?: """每行填写一个 header，写法为 Key: value。一个 header 有多个值时，用 | 分隔"""

        /** 每行填写一个 header，写法为 Key: value。一个 header 有多个值时，用 | 分隔 */
        @Composable
        fun `HeaderHint`(vararg args: Any?) = FYTxtConfig.observe { `HeaderHint`.fmt(args) }

        /** 配置名称 */
        val `ConfigName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config Name"""
                        RootMLangTags.ZH -> """配置名称"""
                        else -> null
                    }
                } ?: """配置名称"""

        /** 配置名称 */
        @Composable
        fun `ConfigName`(vararg args: Any?) = FYTxtConfig.observe { `ConfigName`.fmt(args) }

        /** 配置说明 */
        val `ConfigDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config Description"""
                        RootMLangTags.ZH -> """配置说明"""
                        else -> null
                    }
                } ?: """配置说明"""

        /** 配置说明 */
        @Composable
        fun `ConfigDescription`(vararg args: Any?) =
            FYTxtConfig.observe { `ConfigDescription`.fmt(args) }

        /** 预设分流模板 */
        val `PresetTemplate`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Preset Routing Template"""
                        RootMLangTags.ZH -> """预设分流模板"""
                        else -> null
                    }
                } ?: """预设分流模板"""

        /** 预设分流模板 */
        @Composable
        fun `PresetTemplate`(vararg args: Any?) = FYTxtConfig.observe { `PresetTemplate`.fmt(args) }

        /** 官方 MRS 常用分流 */
        val `OfficialMrs`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Official MRS Common Routing"""
                        RootMLangTags.ZH -> """官方 MRS 常用分流"""
                        else -> null
                    }
                } ?: """官方 MRS 常用分流"""

        /** 官方 MRS 常用分流 */
        @Composable
        fun `OfficialMrs`(vararg args: Any?) = FYTxtConfig.observe { `OfficialMrs`.fmt(args) }

        /** 顶部模板编辑器，支持地区自动组和每个分流项单独开关；应用时会重建当前覆写里的规则三块。 */
        val `OfficialMrsSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Use Mihomo's official meta/geo mrs rulesets. Supports regional auto-groups and per-item switches. Applying it rebuilds the rule providers, proxy groups, and rules in the current override."""
                        RootMLangTags.ZH ->
                            """使用 Mihomo 官方 meta/geo mrs 规则集，支持地区自动分组和逐项开关。应用后会重建当前覆写中的规则提供者、策略组和规则。"""
                        else -> null
                    }
                } ?: """使用 Mihomo 官方 meta/geo mrs 规则集，支持地区自动分组和逐项开关。应用后会重建当前覆写中的规则提供者、策略组和规则。"""

        /** 使用 Mihomo 官方 meta/geo mrs 规则集，支持地区自动分组和逐项开关。应用后会重建当前覆写中的规则提供者、策略组和规则。 */
        @Composable
        fun `OfficialMrsSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `OfficialMrsSummary`.fmt(args) }

        /** 配置分区 */
        val `ConfigSections`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config Sections"""
                        RootMLangTags.ZH -> """配置分区"""
                        else -> null
                    }
                } ?: """配置分区"""

        /** 配置分区 */
        @Composable
        fun `ConfigSections`(vararg args: Any?) = FYTxtConfig.observe { `ConfigSections`.fmt(args) }

        /** 应用 */
        val `Apply`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Apply"""
                        RootMLangTags.ZH -> """应用"""
                        else -> null
                    }
                } ?: """应用"""

        /** 应用 */
        @Composable fun `Apply`(vararg args: Any?) = FYTxtConfig.observe { `Apply`.fmt(args) }

        /** 地区自动组 */
        val `RegionalAutoGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Regional Auto Group"""
                        RootMLangTags.ZH -> """地区自动组"""
                        else -> null
                    }
                } ?: """地区自动组"""

        /** 地区自动组 */
        @Composable
        fun `RegionalAutoGroup`(vararg args: Any?) =
            FYTxtConfig.observe { `RegionalAutoGroup`.fmt(args) }

        /** 基础分流 */
        val `BasicRouting`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Basic Routing"""
                        RootMLangTags.ZH -> """基础分流"""
                        else -> null
                    }
                } ?: """基础分流"""

        /** 基础分流 */
        @Composable
        fun `BasicRouting`(vararg args: Any?) = FYTxtConfig.observe { `BasicRouting`.fmt(args) }

        /** 服务分流 */
        val `ServiceRouting`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Service Routing"""
                        RootMLangTags.ZH -> """服务分流"""
                        else -> null
                    }
                } ?: """服务分流"""

        /** 服务分流 */
        @Composable
        fun `ServiceRouting`(vararg args: Any?) = FYTxtConfig.observe { `ServiceRouting`.fmt(args) }

        /** 香港自动组 */
        val `RegionHongKong`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Hong Kong Auto Group"""
                        RootMLangTags.ZH -> """香港自动组"""
                        else -> null
                    }
                } ?: """香港自动组"""

        /** 香港自动组 */
        @Composable
        fun `RegionHongKong`(vararg args: Any?) = FYTxtConfig.observe { `RegionHongKong`.fmt(args) }

        /** 台湾自动组 */
        val `RegionTaiwan`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Taiwan Auto Group"""
                        RootMLangTags.ZH -> """台湾自动组"""
                        else -> null
                    }
                } ?: """台湾自动组"""

        /** 台湾自动组 */
        @Composable
        fun `RegionTaiwan`(vararg args: Any?) = FYTxtConfig.observe { `RegionTaiwan`.fmt(args) }

        /** 日本自动组 */
        val `RegionJapan`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Japan Auto Group"""
                        RootMLangTags.ZH -> """日本自动组"""
                        else -> null
                    }
                } ?: """日本自动组"""

        /** 日本自动组 */
        @Composable
        fun `RegionJapan`(vararg args: Any?) = FYTxtConfig.observe { `RegionJapan`.fmt(args) }

        /** 新加坡自动组 */
        val `RegionSingapore`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Singapore Auto Group"""
                        RootMLangTags.ZH -> """新加坡自动组"""
                        else -> null
                    }
                } ?: """新加坡自动组"""

        /** 新加坡自动组 */
        @Composable
        fun `RegionSingapore`(vararg args: Any?) =
            FYTxtConfig.observe { `RegionSingapore`.fmt(args) }

        /** 美国自动组 */
        val `RegionUnitedStates`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """United States Auto Group"""
                        RootMLangTags.ZH -> """美国自动组"""
                        else -> null
                    }
                } ?: """美国自动组"""

        /** 美国自动组 */
        @Composable
        fun `RegionUnitedStates`(vararg args: Any?) =
            FYTxtConfig.observe { `RegionUnitedStates`.fmt(args) }

        /** 广告拦截 */
        val `ItemAds`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Ad Blocking"""
                        RootMLangTags.ZH -> """广告拦截"""
                        else -> null
                    }
                } ?: """广告拦截"""

        /** 广告拦截 */
        @Composable fun `ItemAds`(vararg args: Any?) = FYTxtConfig.observe { `ItemAds`.fmt(args) }

        /** 私有地址直连 */
        val `ItemPrivate`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Private Network Direct"""
                        RootMLangTags.ZH -> """私有地址直连"""
                        else -> null
                    }
                } ?: """私有地址直连"""

        /** 私有地址直连 */
        @Composable
        fun `ItemPrivate`(vararg args: Any?) = FYTxtConfig.observe { `ItemPrivate`.fmt(args) }

        /** Google */
        val `ItemGoogle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Google"""
                        RootMLangTags.ZH -> """Google"""
                        else -> null
                    }
                } ?: """Google"""

        /** Google */
        @Composable
        fun `ItemGoogle`(vararg args: Any?) = FYTxtConfig.observe { `ItemGoogle`.fmt(args) }

        /** Telegram */
        val `ItemTelegram`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Telegram"""
                        RootMLangTags.ZH -> """Telegram"""
                        else -> null
                    }
                } ?: """Telegram"""

        /** Telegram */
        @Composable
        fun `ItemTelegram`(vararg args: Any?) = FYTxtConfig.observe { `ItemTelegram`.fmt(args) }

        /** GitHub */
        val `ItemGitHub`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GitHub"""
                        RootMLangTags.ZH -> """GitHub"""
                        else -> null
                    }
                } ?: """GitHub"""

        /** GitHub */
        @Composable
        fun `ItemGitHub`(vararg args: Any?) = FYTxtConfig.observe { `ItemGitHub`.fmt(args) }

        /** Microsoft */
        val `ItemMicrosoft`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Microsoft"""
                        RootMLangTags.ZH -> """Microsoft"""
                        else -> null
                    }
                } ?: """Microsoft"""

        /** Microsoft */
        @Composable
        fun `ItemMicrosoft`(vararg args: Any?) = FYTxtConfig.observe { `ItemMicrosoft`.fmt(args) }

        /** Apple */
        val `ItemApple`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Apple"""
                        RootMLangTags.ZH -> """Apple"""
                        else -> null
                    }
                } ?: """Apple"""

        /** Apple */
        @Composable
        fun `ItemApple`(vararg args: Any?) = FYTxtConfig.observe { `ItemApple`.fmt(args) }

        /** YouTube */
        val `ItemYouTube`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """YouTube"""
                        RootMLangTags.ZH -> """YouTube"""
                        else -> null
                    }
                } ?: """YouTube"""

        /** YouTube */
        @Composable
        fun `ItemYouTube`(vararg args: Any?) = FYTxtConfig.observe { `ItemYouTube`.fmt(args) }

        /** Netflix */
        val `ItemNetflix`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Netflix"""
                        RootMLangTags.ZH -> """Netflix"""
                        else -> null
                    }
                } ?: """Netflix"""

        /** Netflix */
        @Composable
        fun `ItemNetflix`(vararg args: Any?) = FYTxtConfig.observe { `ItemNetflix`.fmt(args) }

        /** Spotify */
        val `ItemSpotify`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Spotify"""
                        RootMLangTags.ZH -> """Spotify"""
                        else -> null
                    }
                } ?: """Spotify"""

        /** Spotify */
        @Composable
        fun `ItemSpotify`(vararg args: Any?) = FYTxtConfig.observe { `ItemSpotify`.fmt(args) }

        /** OpenAI */
        val `ItemOpenAI`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """OpenAI"""
                        RootMLangTags.ZH -> """OpenAI"""
                        else -> null
                    }
                } ?: """OpenAI"""

        /** OpenAI */
        @Composable
        fun `ItemOpenAI`(vararg args: Any?) = FYTxtConfig.observe { `ItemOpenAI`.fmt(args) }

        /** Steam */
        val `ItemSteam`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Steam"""
                        RootMLangTags.ZH -> """Steam"""
                        else -> null
                    }
                } ?: """Steam"""

        /** Steam */
        @Composable
        fun `ItemSteam`(vararg args: Any?) = FYTxtConfig.observe { `ItemSteam`.fmt(args) }

        /** 中国大陆直连 */
        val `ItemCn`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Mainland China Direct"""
                        RootMLangTags.ZH -> """中国大陆直连"""
                        else -> null
                    }
                } ?: """中国大陆直连"""

        /** 中国大陆直连 */
        @Composable fun `ItemCn`(vararg args: Any?) = FYTxtConfig.observe { `ItemCn`.fmt(args) }

        /** 代理规则集 */
        val `ItemProxy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Ruleset"""
                        RootMLangTags.ZH -> """代理规则集"""
                        else -> null
                    }
                } ?: """代理规则集"""

        /** 代理规则集 */
        @Composable
        fun `ItemProxy`(vararg args: Any?) = FYTxtConfig.observe { `ItemProxy`.fmt(args) }

        /** 境外地理规则 */
        val `ItemGeolocationNotCn`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Overseas Geolocation Rules"""
                        RootMLangTags.ZH -> """境外地理规则"""
                        else -> null
                    }
                } ?: """境外地理规则"""

        /** 境外地理规则 */
        @Composable
        fun `ItemGeolocationNotCn`(vararg args: Any?) =
            FYTxtConfig.observe { `ItemGeolocationNotCn`.fmt(args) }

        /** 兜底 MATCH */
        val `ItemMatch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Final MATCH"""
                        RootMLangTags.ZH -> """兜底 MATCH"""
                        else -> null
                    }
                } ?: """兜底 MATCH"""

        /** 兜底 MATCH */
        @Composable
        fun `ItemMatch`(vararg args: Any?) = FYTxtConfig.observe { `ItemMatch`.fmt(args) }

        /** 外部资源 URL */
        val `RemoteSourceUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Remote Resource URL"""
                        RootMLangTags.ZH -> """外部资源 URL"""
                        else -> null
                    }
                } ?: """外部资源 URL"""

        /** 外部资源 URL */
        @Composable
        fun `RemoteSourceUrl`(vararg args: Any?) =
            FYTxtConfig.observe { `RemoteSourceUrl`.fmt(args) }

        /** 更新间隔（秒） */
        val `RemoteUpdateInterval`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update Interval (seconds)"""
                        RootMLangTags.ZH -> """更新间隔（秒）"""
                        else -> null
                    }
                } ?: """更新间隔（秒）"""

        /** 更新间隔（秒） */
        @Composable
        fun `RemoteUpdateInterval`(vararg args: Any?) =
            FYTxtConfig.observe { `RemoteUpdateInterval`.fmt(args) }

        /** 最小 60，默认 86400 */
        val `RemoteUpdateIntervalPlaceholder`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Minimum 60, default 86400"""
                        RootMLangTags.ZH -> """最小 60，默认 86400"""
                        else -> null
                    }
                } ?: """最小 60，默认 86400"""

        /** 最小 60，默认 86400 */
        @Composable
        fun `RemoteUpdateIntervalPlaceholder`(vararg args: Any?) =
            FYTxtConfig.observe { `RemoteUpdateIntervalPlaceholder`.fmt(args) }

        /** 点击添加额外字段 */
        val `ClickToAddExtraField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Click to add extra field"""
                        RootMLangTags.ZH -> """点击添加额外字段"""
                        else -> null
                    }
                } ?: """点击添加额外字段"""

        /** 点击添加额外字段 */
        @Composable
        fun `ClickToAddExtraField`(vararg args: Any?) =
            FYTxtConfig.observe { `ClickToAddExtraField`.fmt(args) }

        /** 删除额外字段 */
        val `DeleteExtraField`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete extra field"""
                        RootMLangTags.ZH -> """删除额外字段"""
                        else -> null
                    }
                } ?: """删除额外字段"""

        /** 删除额外字段 */
        @Composable
        fun `DeleteExtraField`(vararg args: Any?) =
            FYTxtConfig.observe { `DeleteExtraField`.fmt(args) }

        /** 值类型 */
        val `ValueType`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Value Type"""
                        RootMLangTags.ZH -> """值类型"""
                        else -> null
                    }
                } ?: """值类型"""

        /** 值类型 */
        @Composable
        fun `ValueType`(vararg args: Any?) = FYTxtConfig.observe { `ValueType`.fmt(args) }

        /** 字符串值 */
        val `StringValue`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """String value"""
                        RootMLangTags.ZH -> """字符串值"""
                        else -> null
                    }
                } ?: """字符串值"""

        /** 字符串值 */
        @Composable
        fun `StringValue`(vararg args: Any?) = FYTxtConfig.observe { `StringValue`.fmt(args) }

        /** 整数值 */
        val `IntValue`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Integer value"""
                        RootMLangTags.ZH -> """整数值"""
                        else -> null
                    }
                } ?: """整数值"""

        /** 整数值 */
        @Composable fun `IntValue`(vararg args: Any?) = FYTxtConfig.observe { `IntValue`.fmt(args) }

        /** 浮点数值 */
        val `DoubleValue`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Double value"""
                        RootMLangTags.ZH -> """浮点数值"""
                        else -> null
                    }
                } ?: """浮点数值"""

        /** 浮点数值 */
        @Composable
        fun `DoubleValue`(vararg args: Any?) = FYTxtConfig.observe { `DoubleValue`.fmt(args) }

        /** 单个 JSON 片段 */
        val `JsonFragment`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Single JSON fragment"""
                        RootMLangTags.ZH -> """单个 JSON 片段"""
                        else -> null
                    }
                } ?: """单个 JSON 片段"""

        /** 单个 JSON 片段 */
        @Composable
        fun `JsonFragment`(vararg args: Any?) = FYTxtConfig.observe { `JsonFragment`.fmt(args) }

        /** 键名不能为空 */
        val `KeyNameEmpty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Key name cannot be empty"""
                        RootMLangTags.ZH -> """键名不能为空"""
                        else -> null
                    }
                } ?: """键名不能为空"""

        /** 键名不能为空 */
        @Composable
        fun `KeyNameEmpty`(vararg args: Any?) = FYTxtConfig.observe { `KeyNameEmpty`.fmt(args) }

        /** 当前值与所选类型不匹配 */
        val `ValueTypeMismatch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Current value does not match selected type"""
                        RootMLangTags.ZH -> """当前值与所选类型不匹配"""
                        else -> null
                    }
                } ?: """当前值与所选类型不匹配"""

        /** 当前值与所选类型不匹配 */
        @Composable
        fun `ValueTypeMismatch`(vararg args: Any?) =
            FYTxtConfig.observe { `ValueTypeMismatch`.fmt(args) }
    }

    object `Form` {
        init {
            RootMLangGroups
        }

        /** 规则链 */
        val `RuleChain`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule Chain"""
                        RootMLangTags.ZH -> """规则链"""
                        else -> null
                    }
                } ?: """规则链"""

        /** 规则链 */
        @Composable
        fun `RuleChain`(vararg args: Any?) = FYTxtConfig.observe { `RuleChain`.fmt(args) }

        /** 未设置规则链 */
        val `RuleChainNotSet`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule chain not set"""
                        RootMLangTags.ZH -> """未设置规则链"""
                        else -> null
                    }
                } ?: """未设置规则链"""

        /** 未设置规则链 */
        @Composable
        fun `RuleChainNotSet`(vararg args: Any?) =
            FYTxtConfig.observe { `RuleChainNotSet`.fmt(args) }

        /** 子规则 */
        val `SubRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Sub Rules"""
                        RootMLangTags.ZH -> """子规则"""
                        else -> null
                    }
                } ?: """子规则"""

        /** 子规则 */
        @Composable fun `SubRules`(vararg args: Any?) = FYTxtConfig.observe { `SubRules`.fmt(args) }

        /** 结构化规则组 */
        val `SubRulesHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use the structured form to manage sub-rule groups"""
                        RootMLangTags.ZH -> """使用结构化表单管理子规则组"""
                        else -> null
                    }
                } ?: """使用结构化表单管理子规则组"""

        /** 使用结构化表单管理子规则组 */
        @Composable
        fun `SubRulesHint`(vararg args: Any?) = FYTxtConfig.observe { `SubRulesHint`.fmt(args) }

        /** 复杂子规则结构统一收在高级 JSON 中 */
        val `SubRulesAdvanced`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Complex sub rule structures are collected in Advanced JSON"""
                        RootMLangTags.ZH -> """复杂子规则结构统一收在高级 JSON 中"""
                        else -> null
                    }
                } ?: """复杂子规则结构统一收在高级 JSON 中"""

        /** 复杂子规则结构统一收在高级 JSON 中 */
        @Composable
        fun `SubRulesAdvanced`(vararg args: Any?) =
            FYTxtConfig.observe { `SubRulesAdvanced`.fmt(args) }

        /** 规则提供者 */
        val `RuleProviders`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule Providers"""
                        RootMLangTags.ZH -> """规则提供者"""
                        else -> null
                    }
                } ?: """规则提供者"""

        /** 规则提供者 */
        @Composable
        fun `RuleProviders`(vararg args: Any?) = FYTxtConfig.observe { `RuleProviders`.fmt(args) }

        /** 结构化 Provider */
        val `RuleProvidersHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use the structured form to manage rule providers"""
                        RootMLangTags.ZH -> """使用结构化表单管理规则提供者"""
                        else -> null
                    }
                } ?: """使用结构化表单管理规则提供者"""

        /** 使用结构化表单管理规则提供者 */
        @Composable
        fun `RuleProvidersHint`(vararg args: Any?) =
            FYTxtConfig.observe { `RuleProvidersHint`.fmt(args) }

        /** 需要复杂 Provider 字段时再进入高级 JSON */
        val `RuleProvidersAdvanced`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Enter Advanced JSON when complex Provider fields are needed"""
                        RootMLangTags.ZH -> """需要复杂 Provider 字段时再进入高级 JSON"""
                        else -> null
                    }
                } ?: """需要复杂 Provider 字段时再进入高级 JSON"""

        /** 需要复杂 Provider 字段时再进入高级 JSON */
        @Composable
        fun `RuleProvidersAdvanced`(vararg args: Any?) =
            FYTxtConfig.observe { `RuleProvidersAdvanced`.fmt(args) }

        /** 代理节点 */
        val `ProxyNodes`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Nodes"""
                        RootMLangTags.ZH -> """代理节点"""
                        else -> null
                    }
                } ?: """代理节点"""

        /** 代理节点 */
        @Composable
        fun `ProxyNodes`(vararg args: Any?) = FYTxtConfig.observe { `ProxyNodes`.fmt(args) }

        /** 结构化代理条目 */
        val `ProxyNodesHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use the structured form to manage proxy nodes"""
                        RootMLangTags.ZH -> """使用结构化表单管理代理节点"""
                        else -> null
                    }
                } ?: """使用结构化表单管理代理节点"""

        /** 使用结构化表单管理代理节点 */
        @Composable
        fun `ProxyNodesHint`(vararg args: Any?) = FYTxtConfig.observe { `ProxyNodesHint`.fmt(args) }

        /** 代理提供者 */
        val `ProxyProviders`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Providers"""
                        RootMLangTags.ZH -> """代理提供者"""
                        else -> null
                    }
                } ?: """代理提供者"""

        /** 代理提供者 */
        @Composable
        fun `ProxyProviders`(vararg args: Any?) = FYTxtConfig.observe { `ProxyProviders`.fmt(args) }

        /** 结构化 Provider */
        val `ProxyProvidersHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use the structured form to manage proxy providers"""
                        RootMLangTags.ZH -> """使用结构化表单管理代理提供者"""
                        else -> null
                    }
                } ?: """使用结构化表单管理代理提供者"""

        /** 使用结构化表单管理代理提供者 */
        @Composable
        fun `ProxyProvidersHint`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyProvidersHint`.fmt(args) }

        /** 需要协议细节、校验或额外字段时再进入高级 JSON */
        val `ProxyProvidersAdvanced`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Enter Advanced JSON when protocol details, validation or extra fields are needed"""
                        RootMLangTags.ZH -> """需要协议细节、校验或额外字段时再进入高级 JSON"""
                        else -> null
                    }
                } ?: """需要协议细节、校验或额外字段时再进入高级 JSON"""

        /** 需要协议细节、校验或额外字段时再进入高级 JSON */
        @Composable
        fun `ProxyProvidersAdvanced`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyProvidersAdvanced`.fmt(args) }

        /** 策略组 */
        val `ProxyGroups`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Groups"""
                        RootMLangTags.ZH -> """策略组"""
                        else -> null
                    }
                } ?: """策略组"""

        /** 策略组 */
        @Composable
        fun `ProxyGroups`(vararg args: Any?) = FYTxtConfig.observe { `ProxyGroups`.fmt(args) }

        /** 结构化策略组 */
        val `ProxyGroupsHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use the structured form to manage proxy groups"""
                        RootMLangTags.ZH -> """使用结构化表单管理策略组"""
                        else -> null
                    }
                } ?: """使用结构化表单管理策略组"""

        /** 使用结构化表单管理策略组 */
        @Composable
        fun `ProxyGroupsHint`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyGroupsHint`.fmt(args) }

        /** %s · 结构化编辑 */
        val `StructuredEdit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s · Structured Edit"""
                        RootMLangTags.ZH -> """%s · 结构化编辑"""
                        else -> null
                    }
                } ?: """%s · 结构化编辑"""

        /** %s · 结构化编辑 */
        @Composable
        fun `StructuredEdit`(vararg args: Any?) = FYTxtConfig.observe { `StructuredEdit`.fmt(args) }

        /** %s · 高级 JSON */
        val `AdvancedJson`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s · Advanced JSON"""
                        RootMLangTags.ZH -> """%s · 高级 JSON"""
                        else -> null
                    }
                } ?: """%s · 高级 JSON"""

        /** %s · 高级 JSON */
        @Composable
        fun `AdvancedJson`(vararg args: Any?) = FYTxtConfig.observe { `AdvancedJson`.fmt(args) }

        /** 打开高级编辑 */
        val `OpenAdvancedEdit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Open Advanced Edit"""
                        RootMLangTags.ZH -> """打开高级编辑"""
                        else -> null
                    }
                } ?: """打开高级编辑"""

        /** 打开高级编辑 */
        @Composable
        fun `OpenAdvancedEdit`(vararg args: Any?) =
            FYTxtConfig.observe { `OpenAdvancedEdit`.fmt(args) }

        /** 直接编辑原始对象，用于补充结构化表单未覆盖的字段 */
        val `OpenAdvancedEditSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Edit the raw JSON of the current object directly for fields not covered by the structured form"""
                        RootMLangTags.ZH -> """直接编辑当前对象的原始 JSON，适合补充结构化表单未覆盖的字段"""
                        else -> null
                    }
                } ?: """直接编辑当前对象的原始 JSON，适合补充结构化表单未覆盖的字段"""

        /** 直接编辑当前对象的原始 JSON，适合补充结构化表单未覆盖的字段 */
        @Composable
        fun `OpenAdvancedEditSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `OpenAdvancedEditSummary`.fmt(args) }

        /** 已配置 %d 项 */
        val `ItemsConfigured`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d items configured"""
                        RootMLangTags.ZH -> """已配置%d项"""
                        else -> null
                    }
                } ?: """已配置%d项"""

        /** 已配置 %d 项 */
        @Composable
        fun `ItemsConfigured`(vararg args: Any?) =
            FYTxtConfig.observe { `ItemsConfigured`.fmt(args) }

        /** 代理端口 */
        val `ProxyPorts`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Ports"""
                        RootMLangTags.ZH -> """代理端口"""
                        else -> null
                    }
                } ?: """代理端口"""

        /** 代理端口 */
        @Composable
        fun `ProxyPorts`(vararg args: Any?) = FYTxtConfig.observe { `ProxyPorts`.fmt(args) }

        /** 运行与日志 */
        val `RunAndLog`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Run & Log"""
                        RootMLangTags.ZH -> """运行与日志"""
                        else -> null
                    }
                } ?: """运行与日志"""

        /** 运行与日志 */
        @Composable
        fun `RunAndLog`(vararg args: Any?) = FYTxtConfig.observe { `RunAndLog`.fmt(args) }

        /** 进程匹配模式 */
        val `ProcessMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Process Match Mode"""
                        RootMLangTags.ZH -> """进程匹配模式"""
                        else -> null
                    }
                } ?: """进程匹配模式"""

        /** 进程匹配模式 */
        @Composable
        fun `ProcessMode`(vararg args: Any?) = FYTxtConfig.observe { `ProcessMode`.fmt(args) }

        /** 不修改 */
        val `NotModify`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Don't Modify"""
                        RootMLangTags.ZH -> """不修改"""
                        else -> null
                    }
                } ?: """不修改"""

        /** 不修改 */
        @Composable
        fun `NotModify`(vararg args: Any?) = FYTxtConfig.observe { `NotModify`.fmt(args) }

        /** 统一延迟 */
        val `UnifiedDelay`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unified Delay"""
                        RootMLangTags.ZH -> """统一延迟"""
                        else -> null
                    }
                } ?: """统一延迟"""

        /** 统一延迟 */
        @Composable
        fun `UnifiedDelay`(vararg args: Any?) = FYTxtConfig.observe { `UnifiedDelay`.fmt(args) }

        /** TCP 并发 */
        val `TcpConcurrent`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """TCP Concurrent"""
                        RootMLangTags.ZH -> """TCP 并发"""
                        else -> null
                    }
                } ?: """TCP 并发"""

        /** TCP 并发 */
        @Composable
        fun `TcpConcurrent`(vararg args: Any?) = FYTxtConfig.observe { `TcpConcurrent`.fmt(args) }

        /** Geodata 模式 */
        val `GeodataMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Geodata Mode"""
                        RootMLangTags.ZH -> """Geodata 模式"""
                        else -> null
                    }
                } ?: """Geodata 模式"""

        /** Geodata 模式 */
        @Composable
        fun `GeodataMode`(vararg args: Any?) = FYTxtConfig.observe { `GeodataMode`.fmt(args) }

        /** 运行与日志补充 */
        val `RunAndLogExtra`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Run & Log Extra"""
                        RootMLangTags.ZH -> """运行与日志补充"""
                        else -> null
                    }
                } ?: """运行与日志补充"""

        /** 运行与日志补充 */
        @Composable
        fun `RunAndLogExtra`(vararg args: Any?) = FYTxtConfig.observe { `RunAndLogExtra`.fmt(args) }

        /** 秒 */
        val `Seconds`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """seconds"""
                        RootMLangTags.ZH -> """秒"""
                        else -> null
                    }
                } ?: """秒"""

        /** 秒 */
        @Composable fun `Seconds`(vararg args: Any?) = FYTxtConfig.observe { `Seconds`.fmt(args) }

        /** 连接与网络 */
        val `ConnectionNetwork`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Connection & Network"""
                        RootMLangTags.ZH -> """连接与网络"""
                        else -> null
                    }
                } ?: """连接与网络"""

        /** 连接与网络 */
        @Composable
        fun `ConnectionNetwork`(vararg args: Any?) =
            FYTxtConfig.observe { `ConnectionNetwork`.fmt(args) }

        /** 出站接口 */
        val `OutboundInterface`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Outbound Interface"""
                        RootMLangTags.ZH -> """出站接口"""
                        else -> null
                    }
                } ?: """出站接口"""

        /** 出站接口 */
        @Composable
        fun `OutboundInterface`(vararg args: Any?) =
            FYTxtConfig.observe { `OutboundInterface`.fmt(args) }

        /** 路由标记 */
        val `RoutingMark`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Routing Mark"""
                        RootMLangTags.ZH -> """路由标记"""
                        else -> null
                    }
                } ?: """路由标记"""

        /** 路由标记 */
        @Composable
        fun `RoutingMark`(vararg args: Any?) = FYTxtConfig.observe { `RoutingMark`.fmt(args) }

        /** Geosite 匹配器 */
        val `GeositeMatcher`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Geosite Matcher"""
                        RootMLangTags.ZH -> """Geosite 匹配器"""
                        else -> null
                    }
                } ?: """Geosite 匹配器"""

        /** Geosite 匹配器 */
        @Composable
        fun `GeositeMatcher`(vararg args: Any?) = FYTxtConfig.observe { `GeositeMatcher`.fmt(args) }

        /** 全局客户端指纹 */
        val `GlobalClientFingerprint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Global Client Fingerprint"""
                        RootMLangTags.ZH -> """全局客户端指纹"""
                        else -> null
                    }
                } ?: """全局客户端指纹"""

        /** 全局客户端指纹 */
        @Composable
        fun `GlobalClientFingerprint`(vararg args: Any?) =
            FYTxtConfig.observe { `GlobalClientFingerprint`.fmt(args) }

        /** 局域网访问 */
        val `LanAccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """LAN Access"""
                        RootMLangTags.ZH -> """局域网访问"""
                        else -> null
                    }
                } ?: """局域网访问"""

        /** 局域网访问 */
        @Composable
        fun `LanAccess`(vararg args: Any?) = FYTxtConfig.observe { `LanAccess`.fmt(args) }

        /** 允许 IP 段 */
        val `AllowedIPs`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allowed IPs"""
                        RootMLangTags.ZH -> """允许 IP 段"""
                        else -> null
                    }
                } ?: """允许 IP 段"""

        /** 允许 IP 段 */
        @Composable
        fun `AllowedIPs`(vararg args: Any?) = FYTxtConfig.observe { `AllowedIPs`.fmt(args) }

        /** 禁止 IP 段 */
        val `DisallowedIPs`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Disallowed IPs"""
                        RootMLangTags.ZH -> """禁止 IP 段"""
                        else -> null
                    }
                } ?: """禁止 IP 段"""

        /** 禁止 IP 段 */
        @Composable
        fun `DisallowedIPs`(vararg args: Any?) = FYTxtConfig.observe { `DisallowedIPs`.fmt(args) }

        /** 局域网地址 */
        val `LanAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """LAN Address"""
                        RootMLangTags.ZH -> """局域网地址"""
                        else -> null
                    }
                } ?: """局域网地址"""

        /** 局域网地址 */
        @Composable
        fun `LanAddress`(vararg args: Any?) = FYTxtConfig.observe { `LanAddress`.fmt(args) }

        /** 绑定地址 */
        val `BindAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Bind Address"""
                        RootMLangTags.ZH -> """绑定地址"""
                        else -> null
                    }
                } ?: """绑定地址"""

        /** 绑定地址 */
        @Composable
        fun `BindAddress`(vararg args: Any?) = FYTxtConfig.observe { `BindAddress`.fmt(args) }

        /** 用户验证 */
        val `UserAuth`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """User Auth"""
                        RootMLangTags.ZH -> """用户验证"""
                        else -> null
                    }
                } ?: """用户验证"""

        /** 用户验证 */
        @Composable fun `UserAuth`(vararg args: Any?) = FYTxtConfig.observe { `UserAuth`.fmt(args) }

        /** 跳过鉴权网段 */
        val `SkipAuthIPs`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Skip Auth IPs"""
                        RootMLangTags.ZH -> """跳过鉴权网段"""
                        else -> null
                    }
                } ?: """跳过鉴权网段"""

        /** 跳过鉴权网段 */
        @Composable
        fun `SkipAuthIPs`(vararg args: Any?) = FYTxtConfig.observe { `SkipAuthIPs`.fmt(args) }

        /** 外部控制 */
        val `ExternalControl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """External Control"""
                        RootMLangTags.ZH -> """外部控制"""
                        else -> null
                    }
                } ?: """外部控制"""

        /** 外部控制 */
        @Composable
        fun `ExternalControl`(vararg args: Any?) =
            FYTxtConfig.observe { `ExternalControl`.fmt(args) }

        /** 外部控制器 */
        val `ExternalController`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """External Controller"""
                        RootMLangTags.ZH -> """外部控制器"""
                        else -> null
                    }
                } ?: """外部控制器"""

        /** 外部控制器 */
        @Composable
        fun `ExternalController`(vararg args: Any?) =
            FYTxtConfig.observe { `ExternalController`.fmt(args) }

        /** HTTPS 控制器 */
        val `ExternalControllerHttps`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """HTTPS Controller"""
                        RootMLangTags.ZH -> """HTTPS 控制器"""
                        else -> null
                    }
                } ?: """HTTPS 控制器"""

        /** HTTPS 控制器 */
        @Composable
        fun `ExternalControllerHttps`(vararg args: Any?) =
            FYTxtConfig.observe { `ExternalControllerHttps`.fmt(args) }

        /** 外部 DoH 服务 */
        val `ExternalDoH`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """External DoH Service"""
                        RootMLangTags.ZH -> """外部 DoH 服务"""
                        else -> null
                    }
                } ?: """外部 DoH 服务"""

        /** 外部 DoH 服务 */
        @Composable
        fun `ExternalDoH`(vararg args: Any?) = FYTxtConfig.observe { `ExternalDoH`.fmt(args) }

        /** API 访问密钥 */
        val `ApiSecret`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """API Secret"""
                        RootMLangTags.ZH -> """API 访问密钥"""
                        else -> null
                    }
                } ?: """API 访问密钥"""

        /** API 访问密钥 */
        @Composable
        fun `ApiSecret`(vararg args: Any?) = FYTxtConfig.observe { `ApiSecret`.fmt(args) }

        /** 控制器 CORS */
        val `ControllerCors`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Controller CORS"""
                        RootMLangTags.ZH -> """控制器 CORS"""
                        else -> null
                    }
                } ?: """控制器 CORS"""

        /** 控制器 CORS */
        @Composable
        fun `ControllerCors`(vararg args: Any?) = FYTxtConfig.observe { `ControllerCors`.fmt(args) }

        /** CORS 允许来源 */
        val `CorsAllowOrigins`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """CORS Allow Origins"""
                        RootMLangTags.ZH -> """CORS 允许来源"""
                        else -> null
                    }
                } ?: """CORS 允许来源"""

        /** CORS 允许来源 */
        @Composable
        fun `CorsAllowOrigins`(vararg args: Any?) =
            FYTxtConfig.observe { `CorsAllowOrigins`.fmt(args) }

        /** 允许私有网络 */
        val `AllowPrivateNetwork`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow Private Network"""
                        RootMLangTags.ZH -> """允许私有网络"""
                        else -> null
                    }
                } ?: """允许私有网络"""

        /** 允许私有网络 */
        @Composable
        fun `AllowPrivateNetwork`(vararg args: Any?) =
            FYTxtConfig.observe { `AllowPrivateNetwork`.fmt(args) }

        /** 配置持久化 */
        val `ConfigPersistence`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config Persistence"""
                        RootMLangTags.ZH -> """配置持久化"""
                        else -> null
                    }
                } ?: """配置持久化"""

        /** 配置持久化 */
        @Composable
        fun `ConfigPersistence`(vararg args: Any?) =
            FYTxtConfig.observe { `ConfigPersistence`.fmt(args) }

        /** 保存策略组选择 */
        val `SaveGroupSelection`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Save Group Selection"""
                        RootMLangTags.ZH -> """保存策略组选择"""
                        else -> null
                    }
                } ?: """保存策略组选择"""

        /** 保存策略组选择 */
        @Composable
        fun `SaveGroupSelection`(vararg args: Any?) =
            FYTxtConfig.observe { `SaveGroupSelection`.fmt(args) }

        /** 保存 Fake-IP 映射 */
        val `SaveFakeIpMapping`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Save Fake-IP Mapping"""
                        RootMLangTags.ZH -> """保存 Fake-IP 映射"""
                        else -> null
                    }
                } ?: """保存 Fake-IP 映射"""

        /** 保存 Fake-IP 映射 */
        @Composable
        fun `SaveFakeIpMapping`(vararg args: Any?) =
            FYTxtConfig.observe { `SaveFakeIpMapping`.fmt(args) }

        /** GEO 资源开关 */
        val `GeoResources`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GEO Resources"""
                        RootMLangTags.ZH -> """GEO 资源开关"""
                        else -> null
                    }
                } ?: """GEO 资源开关"""

        /** GEO 资源开关 */
        @Composable
        fun `GeoResources`(vararg args: Any?) = FYTxtConfig.observe { `GeoResources`.fmt(args) }

        /** 自动更新 GEO */
        val `AutoUpdateGeo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Update GEO"""
                        RootMLangTags.ZH -> """自动更新 GEO"""
                        else -> null
                    }
                } ?: """自动更新 GEO"""

        /** 自动更新 GEO */
        @Composable
        fun `AutoUpdateGeo`(vararg args: Any?) = FYTxtConfig.observe { `AutoUpdateGeo`.fmt(args) }

        /** GEO 更新间隔 */
        val `GeoUpdateInterval`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GEO Update Interval"""
                        RootMLangTags.ZH -> """GEO 更新间隔"""
                        else -> null
                    }
                } ?: """GEO 更新间隔"""

        /** GEO 更新间隔 */
        @Composable
        fun `GeoUpdateInterval`(vararg args: Any?) =
            FYTxtConfig.observe { `GeoUpdateInterval`.fmt(args) }

        /** 小时 */
        val `Hours`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """hours"""
                        RootMLangTags.ZH -> """小时"""
                        else -> null
                    }
                } ?: """小时"""

        /** 小时 */
        @Composable fun `Hours`(vararg args: Any?) = FYTxtConfig.observe { `Hours`.fmt(args) }

        /** GeoIP 地址 */
        val `GeoipUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GeoIP URL"""
                        RootMLangTags.ZH -> """GeoIP 地址"""
                        else -> null
                    }
                } ?: """GeoIP 地址"""

        /** GeoIP 地址 */
        @Composable fun `GeoipUrl`(vararg args: Any?) = FYTxtConfig.observe { `GeoipUrl`.fmt(args) }

        /** GeoSite 地址 */
        val `GeositeUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GeoSite URL"""
                        RootMLangTags.ZH -> """GeoSite 地址"""
                        else -> null
                    }
                } ?: """GeoSite 地址"""

        /** GeoSite 地址 */
        @Composable
        fun `GeositeUrl`(vararg args: Any?) = FYTxtConfig.observe { `GeositeUrl`.fmt(args) }

        /** MMDB 地址 */
        val `MmdbUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """MMDB URL"""
                        RootMLangTags.ZH -> """MMDB 地址"""
                        else -> null
                    }
                } ?: """MMDB 地址"""

        /** MMDB 地址 */
        @Composable fun `MmdbUrl`(vararg args: Any?) = FYTxtConfig.observe { `MmdbUrl`.fmt(args) }

        /** 基础开关 */
        val `TunBasicSwitch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Basic Switch"""
                        RootMLangTags.ZH -> """基础开关"""
                        else -> null
                    }
                } ?: """基础开关"""

        /** 基础开关 */
        @Composable
        fun `TunBasicSwitch`(vararg args: Any?) = FYTxtConfig.observe { `TunBasicSwitch`.fmt(args) }

        /** 协议栈 */
        val `Stack`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Stack"""
                        RootMLangTags.ZH -> """协议栈"""
                        else -> null
                    }
                } ?: """协议栈"""

        /** 协议栈 */
        @Composable fun `Stack`(vararg args: Any?) = FYTxtConfig.observe { `Stack`.fmt(args) }

        /** 自动路由 */
        val `AutoRoute`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Route"""
                        RootMLangTags.ZH -> """自动路由"""
                        else -> null
                    }
                } ?: """自动路由"""

        /** 自动路由 */
        @Composable
        fun `AutoRoute`(vararg args: Any?) = FYTxtConfig.observe { `AutoRoute`.fmt(args) }

        /** 自动重定向 */
        val `AutoRedirect`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Redirect"""
                        RootMLangTags.ZH -> """自动重定向"""
                        else -> null
                    }
                } ?: """自动重定向"""

        /** 自动重定向 */
        @Composable
        fun `AutoRedirect`(vararg args: Any?) = FYTxtConfig.observe { `AutoRedirect`.fmt(args) }

        /** 自动识别网卡 */
        val `AutoDetectInterface`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Detect Interface"""
                        RootMLangTags.ZH -> """自动识别网卡"""
                        else -> null
                    }
                } ?: """自动识别网卡"""

        /** 自动识别网卡 */
        @Composable
        fun `AutoDetectInterface`(vararg args: Any?) =
            FYTxtConfig.observe { `AutoDetectInterface`.fmt(args) }

        /** 严格路由 */
        val `StrictRoute`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Strict Route"""
                        RootMLangTags.ZH -> """严格路由"""
                        else -> null
                    }
                } ?: """严格路由"""

        /** 严格路由 */
        @Composable
        fun `StrictRoute`(vararg args: Any?) = FYTxtConfig.observe { `StrictRoute`.fmt(args) }

        /** 独立于端点 NAT */
        val `EndpointIndependentNat`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Endpoint Independent NAT"""
                        RootMLangTags.ZH -> """独立于端点 NAT"""
                        else -> null
                    }
                } ?: """独立于端点 NAT"""

        /** 独立于端点 NAT */
        @Composable
        fun `EndpointIndependentNat`(vararg args: Any?) =
            FYTxtConfig.observe { `EndpointIndependentNat`.fmt(args) }

        /** 网络性能开关 */
        val `NetworkPerfSwitch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Network Perf Switch"""
                        RootMLangTags.ZH -> """网络性能开关"""
                        else -> null
                    }
                } ?: """网络性能开关"""

        /** 网络性能开关 */
        @Composable
        fun `NetworkPerfSwitch`(vararg args: Any?) =
            FYTxtConfig.observe { `NetworkPerfSwitch`.fmt(args) }

        /** 启用 GSO */
        val `EnableGso`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enable GSO"""
                        RootMLangTags.ZH -> """启用 GSO"""
                        else -> null
                    }
                } ?: """启用 GSO"""

        /** 启用 GSO */
        @Composable
        fun `EnableGso`(vararg args: Any?) = FYTxtConfig.observe { `EnableGso`.fmt(args) }

        /** 禁用 ICMP 转发 */
        val `DisableIcmpForward`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Disable ICMP Forward"""
                        RootMLangTags.ZH -> """禁用 ICMP 转发"""
                        else -> null
                    }
                } ?: """禁用 ICMP 转发"""

        /** 禁用 ICMP 转发 */
        @Composable
        fun `DisableIcmpForward`(vararg args: Any?) =
            FYTxtConfig.observe { `DisableIcmpForward`.fmt(args) }

        /** 网络性能参数 */
        val `NetworkPerfParams`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Network Perf Params"""
                        RootMLangTags.ZH -> """网络性能参数"""
                        else -> null
                    }
                } ?: """网络性能参数"""

        /** 网络性能参数 */
        @Composable
        fun `NetworkPerfParams`(vararg args: Any?) =
            FYTxtConfig.observe { `NetworkPerfParams`.fmt(args) }

        /** MTU */
        val `Mtu`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """MTU"""
                        RootMLangTags.ZH -> """MTU"""
                        else -> null
                    }
                } ?: """MTU"""

        /** MTU */
        @Composable fun `Mtu`(vararg args: Any?) = FYTxtConfig.observe { `Mtu`.fmt(args) }

        /** GSO 最大长度 */
        val `GsoMaxSize`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GSO Max Size"""
                        RootMLangTags.ZH -> """GSO 最大长度"""
                        else -> null
                    }
                } ?: """GSO 最大长度"""

        /** GSO 最大长度 */
        @Composable
        fun `GsoMaxSize`(vararg args: Any?) = FYTxtConfig.observe { `GsoMaxSize`.fmt(args) }

        /** 基础开关 */
        val `DnsBasicSwitch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Basic Switch"""
                        RootMLangTags.ZH -> """基础开关"""
                        else -> null
                    }
                } ?: """基础开关"""

        /** 基础开关 */
        @Composable
        fun `DnsBasicSwitch`(vararg args: Any?) = FYTxtConfig.observe { `DnsBasicSwitch`.fmt(args) }

        /** DNS 基础参数 */
        val `DnsBasicParams`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS Basic Params"""
                        RootMLangTags.ZH -> """DNS 基础参数"""
                        else -> null
                    }
                } ?: """DNS 基础参数"""

        /** DNS 基础参数 */
        @Composable
        fun `DnsBasicParams`(vararg args: Any?) = FYTxtConfig.observe { `DnsBasicParams`.fmt(args) }

        /** FakeIP 地址段 */
        val `DnsFakeIpRange`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP Range"""
                        RootMLangTags.ZH -> """FakeIP 地址段"""
                        else -> null
                    }
                } ?: """FakeIP 地址段"""

        /** FakeIP 地址段 */
        @Composable
        fun `DnsFakeIpRange`(vararg args: Any?) = FYTxtConfig.observe { `DnsFakeIpRange`.fmt(args) }

        /** Fake-IP 模式 */
        val `FakeIpMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fake-IP Mode"""
                        RootMLangTags.ZH -> """Fake-IP 模式"""
                        else -> null
                    }
                } ?: """Fake-IP 模式"""

        /** Fake-IP 模式 */
        @Composable
        fun `FakeIpMode`(vararg args: Any?) = FYTxtConfig.observe { `FakeIpMode`.fmt(args) }

        /** Fake-IP 参数 */
        val `FakeIpParams`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fake-IP Params"""
                        RootMLangTags.ZH -> """Fake-IP 参数"""
                        else -> null
                    }
                } ?: """Fake-IP 参数"""

        /** Fake-IP 参数 */
        @Composable
        fun `FakeIpParams`(vararg args: Any?) = FYTxtConfig.observe { `FakeIpParams`.fmt(args) }

        /** 上游服务器 */
        val `DnsUpstream`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Upstream Servers"""
                        RootMLangTags.ZH -> """上游服务器"""
                        else -> null
                    }
                } ?: """上游服务器"""

        /** 上游服务器 */
        @Composable
        fun `DnsUpstream`(vararg args: Any?) = FYTxtConfig.observe { `DnsUpstream`.fmt(args) }

        /** 策略模式 */
        val `DnsPolicyMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Policy Mode"""
                        RootMLangTags.ZH -> """策略模式"""
                        else -> null
                    }
                } ?: """策略模式"""

        /** 策略模式 */
        @Composable
        fun `DnsPolicyMode`(vararg args: Any?) = FYTxtConfig.observe { `DnsPolicyMode`.fmt(args) }

        /** 开关 */
        val `SnifferSwitch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Switch"""
                        RootMLangTags.ZH -> """开关"""
                        else -> null
                    }
                } ?: """开关"""

        /** 开关 */
        @Composable
        fun `SnifferSwitch`(vararg args: Any?) = FYTxtConfig.observe { `SnifferSwitch`.fmt(args) }

        /** 端口 */
        val `SnifferPorts`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Ports"""
                        RootMLangTags.ZH -> """端口"""
                        else -> null
                    }
                } ?: """端口"""

        /** 端口 */
        @Composable
        fun `SnifferPorts`(vararg args: Any?) = FYTxtConfig.observe { `SnifferPorts`.fmt(args) }

        /** 覆写目标 */
        val `SnifferOverride`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override Destination"""
                        RootMLangTags.ZH -> """覆写目标"""
                        else -> null
                    }
                } ?: """覆写目标"""

        /** 覆写目标 */
        @Composable
        fun `SnifferOverride`(vararg args: Any?) =
            FYTxtConfig.observe { `SnifferOverride`.fmt(args) }

        /** 跳过域名 */
        val `SnifferSkipDomain`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Skip Domain"""
                        RootMLangTags.ZH -> """跳过域名"""
                        else -> null
                    }
                } ?: """跳过域名"""

        /** 跳过域名 */
        @Composable
        fun `SnifferSkipDomain`(vararg args: Any?) =
            FYTxtConfig.observe { `SnifferSkipDomain`.fmt(args) }

        /** 强制域名 */
        val `SnifferForceDomain`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Force Domain"""
                        RootMLangTags.ZH -> """强制域名"""
                        else -> null
                    }
                } ?: """强制域名"""

        /** 强制域名 */
        @Composable
        fun `SnifferForceDomain`(vararg args: Any?) =
            FYTxtConfig.observe { `SnifferForceDomain`.fmt(args) }

        /** 解析纯 IP */
        val `SnifferParsePureIp`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Parse Pure IP"""
                        RootMLangTags.ZH -> """解析纯 IP"""
                        else -> null
                    }
                } ?: """解析纯 IP"""

        /** 解析纯 IP */
        @Composable
        fun `SnifferParsePureIp`(vararg args: Any?) =
            FYTxtConfig.observe { `SnifferParsePureIp`.fmt(args) }

        /** 路由与应用 */
        val `TunRouteAndApps`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Route & Apps"""
                        RootMLangTags.ZH -> """路由与应用"""
                        else -> null
                    }
                } ?: """路由与应用"""

        /** 路由与应用 */
        @Composable
        fun `TunRouteAndApps`(vararg args: Any?) =
            FYTxtConfig.observe { `TunRouteAndApps`.fmt(args) }

        /** DNS 劫持 */
        val `DnsHijack`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS Hijack"""
                        RootMLangTags.ZH -> """DNS 劫持"""
                        else -> null
                    }
                } ?: """DNS 劫持"""

        /** DNS 劫持 */
        @Composable
        fun `DnsHijack`(vararg args: Any?) = FYTxtConfig.observe { `DnsHijack`.fmt(args) }

        /** 路由网段 */
        val `RouteAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Route Address"""
                        RootMLangTags.ZH -> """路由网段"""
                        else -> null
                    }
                } ?: """路由网段"""

        /** 路由网段 */
        @Composable
        fun `RouteAddress`(vararg args: Any?) = FYTxtConfig.observe { `RouteAddress`.fmt(args) }

        /** 排除路由网段 */
        val `RouteExcludeAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Route Exclude Address"""
                        RootMLangTags.ZH -> """排除路由网段"""
                        else -> null
                    }
                } ?: """排除路由网段"""

        /** 排除路由网段 */
        @Composable
        fun `RouteExcludeAddress`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteExcludeAddress`.fmt(args) }

        /** 包含应用 */
        val `IncludePackage`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Include Package"""
                        RootMLangTags.ZH -> """包含应用"""
                        else -> null
                    }
                } ?: """包含应用"""

        /** 包含应用 */
        @Composable
        fun `IncludePackage`(vararg args: Any?) = FYTxtConfig.observe { `IncludePackage`.fmt(args) }

        /** 排除应用 */
        val `ExcludePackage`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Exclude Package"""
                        RootMLangTags.ZH -> """排除应用"""
                        else -> null
                    }
                } ?: """排除应用"""

        /** 排除应用 */
        @Composable
        fun `ExcludePackage`(vararg args: Any?) = FYTxtConfig.observe { `ExcludePackage`.fmt(args) }

        /** 缓存上限 */
        val `CacheLimit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cache Limit"""
                        RootMLangTags.ZH -> """缓存上限"""
                        else -> null
                    }
                } ?: """缓存上限"""

        /** 缓存上限 */
        @Composable
        fun `CacheLimit`(vararg args: Any?) = FYTxtConfig.observe { `CacheLimit`.fmt(args) }

        /** 上游服务器 */
        val `DnsUpstreamServers`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Upstream Servers"""
                        RootMLangTags.ZH -> """上游服务器"""
                        else -> null
                    }
                } ?: """上游服务器"""

        /** 上游服务器 */
        @Composable
        fun `DnsUpstreamServers`(vararg args: Any?) =
            FYTxtConfig.observe { `DnsUpstreamServers`.fmt(args) }

        /** 策略映射 */
        val `NameserverPolicySection`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Nameserver Policy"""
                        RootMLangTags.ZH -> """策略映射"""
                        else -> null
                    }
                } ?: """策略映射"""

        /** 策略映射 */
        @Composable
        fun `NameserverPolicySection`(vararg args: Any?) =
            FYTxtConfig.observe { `NameserverPolicySection`.fmt(args) }

        /** 过滤列表 */
        val `FilterList`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Filter List"""
                        RootMLangTags.ZH -> """过滤列表"""
                        else -> null
                    }
                } ?: """过滤列表"""

        /** 过滤列表 */
        @Composable
        fun `FilterList`(vararg args: Any?) = FYTxtConfig.observe { `FilterList`.fmt(args) }

        /** Fallback 开关 */
        val `FallbackSwitch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fallback Switch"""
                        RootMLangTags.ZH -> """Fallback 开关"""
                        else -> null
                    }
                } ?: """Fallback 开关"""

        /** Fallback 开关 */
        @Composable
        fun `FallbackSwitch`(vararg args: Any?) = FYTxtConfig.observe { `FallbackSwitch`.fmt(args) }

        /** Fallback 参数 */
        val `FallbackParams`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fallback Params"""
                        RootMLangTags.ZH -> """Fallback 参数"""
                        else -> null
                    }
                } ?: """Fallback 参数"""

        /** Fallback 参数 */
        @Composable
        fun `FallbackParams`(vararg args: Any?) = FYTxtConfig.observe { `FallbackParams`.fmt(args) }

        /** Fallback 过滤 */
        val `FallbackFilter`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fallback Filter"""
                        RootMLangTags.ZH -> """Fallback 过滤"""
                        else -> null
                    }
                } ?: """Fallback 过滤"""

        /** Fallback 过滤 */
        @Composable
        fun `FallbackFilter`(vararg args: Any?) = FYTxtConfig.observe { `FallbackFilter`.fmt(args) }

        /** 基础策略 */
        val `BasicPolicy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Basic Policy"""
                        RootMLangTags.ZH -> """基础策略"""
                        else -> null
                    }
                } ?: """基础策略"""

        /** 基础策略 */
        @Composable
        fun `BasicPolicy`(vararg args: Any?) = FYTxtConfig.observe { `BasicPolicy`.fmt(args) }

        /** 跳过与强制 */
        val `SkipAndForce`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Skip & Force"""
                        RootMLangTags.ZH -> """跳过与强制"""
                        else -> null
                    }
                } ?: """跳过与强制"""

        /** 跳过与强制 */
        @Composable
        fun `SkipAndForce`(vararg args: Any?) = FYTxtConfig.observe { `SkipAndForce`.fmt(args) }

        /** 跳过来源地址 */
        val `SkipSrcAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Skip Src Address"""
                        RootMLangTags.ZH -> """跳过来源地址"""
                        else -> null
                    }
                } ?: """跳过来源地址"""

        /** 跳过来源地址 */
        @Composable
        fun `SkipSrcAddress`(vararg args: Any?) = FYTxtConfig.observe { `SkipSrcAddress`.fmt(args) }

        /** 跳过目标地址 */
        val `SkipDstAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Skip Dst Address"""
                        RootMLangTags.ZH -> """跳过目标地址"""
                        else -> null
                    }
                } ?: """跳过目标地址"""

        /** 跳过目标地址 */
        @Composable
        fun `SkipDstAddress`(vararg args: Any?) = FYTxtConfig.observe { `SkipDstAddress`.fmt(args) }
    }

    object `Rule` {
        init {
            RootMLangGroups
        }

        /** 规则 #%d 为空 */
        val `EmptyWarning`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule #%d is empty"""
                        RootMLangTags.ZH -> """规则 #%d 为空"""
                        else -> null
                    }
                } ?: """规则 #%d 为空"""

        /** 规则 #%d 为空 */
        @Composable
        fun `EmptyWarning`(vararg args: Any?) = FYTxtConfig.observe { `EmptyWarning`.fmt(args) }

        /** 规则 #%d 格式可能不正确: %s */
        val `InvalidFormatWarning`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule #%d format may be incorrect: %s"""
                        RootMLangTags.ZH -> """规则 #%d 格式可能不正确: %s"""
                        else -> null
                    }
                } ?: """规则 #%d 格式可能不正确: %s"""

        /** 规则 #%d 格式可能不正确: %s */
        @Composable
        fun `InvalidFormatWarning`(vararg args: Any?) =
            FYTxtConfig.observe { `InvalidFormatWarning`.fmt(args) }

        /** 规则 #%d 缺少策略组目标: %s */
        val `MissingTargetWarning`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule #%d missing policy group target: %s"""
                        RootMLangTags.ZH -> """规则 #%d 缺少策略组目标: %s"""
                        else -> null
                    }
                } ?: """规则 #%d 缺少策略组目标: %s"""

        /** 规则 #%d 缺少策略组目标: %s */
        @Composable
        fun `MissingTargetWarning`(vararg args: Any?) =
            FYTxtConfig.observe { `MissingTargetWarning`.fmt(args) }
    }

    object `Save` {
        init {
            RootMLangGroups
        }

        /** 系统预设不可修改 */
        val `PresetNotModifiable`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """System preset cannot be modified"""
                        RootMLangTags.ZH -> """系统预设不可修改"""
                        else -> null
                    }
                } ?: """系统预设不可修改"""

        /** 系统预设不可修改 */
        @Composable
        fun `PresetNotModifiable`(vararg args: Any?) =
            FYTxtConfig.observe { `PresetNotModifiable`.fmt(args) }

        /** 覆写已保存，但重新应用到当前配置失败 */
        val `ApplyFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Override saved, but failed to reapply to current config"""
                        RootMLangTags.ZH -> """覆写已保存，但重新应用到当前配置失败"""
                        else -> null
                    }
                } ?: """覆写已保存，但重新应用到当前配置失败"""

        /** 覆写已保存，但重新应用到当前配置失败 */
        @Composable
        fun `ApplyFailed`(vararg args: Any?) = FYTxtConfig.observe { `ApplyFailed`.fmt(args) }

        /** 保存覆写配置失败 */
        val `Failed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to save override config"""
                        RootMLangTags.ZH -> """保存覆写配置失败"""
                        else -> null
                    }
                } ?: """保存覆写配置失败"""

        /** 保存覆写配置失败 */
        @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

        /** 保存运行时覆写失败 */
        val `RuntimeSaveFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to save runtime override"""
                        RootMLangTags.ZH -> """保存运行时覆写失败"""
                        else -> null
                    }
                } ?: """保存运行时覆写失败"""

        /** 保存运行时覆写失败 */
        @Composable
        fun `RuntimeSaveFailed`(vararg args: Any?) =
            FYTxtConfig.observe { `RuntimeSaveFailed`.fmt(args) }

        /** 运行时覆写 */
        val `RuntimeOverrideName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Runtime Override"""
                        RootMLangTags.ZH -> """运行时覆写"""
                        else -> null
                    }
                } ?: """运行时覆写"""

        /** 运行时覆写 */
        @Composable
        fun `RuntimeOverrideName`(vararg args: Any?) =
            FYTxtConfig.observe { `RuntimeOverrideName`.fmt(args) }

        /** 导入内容不能为空 */
        val `ImportEmpty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import content cannot be empty"""
                        RootMLangTags.ZH -> """导入内容不能为空"""
                        else -> null
                    }
                } ?: """导入内容不能为空"""

        /** 导入内容不能为空 */
        @Composable
        fun `ImportEmpty`(vararg args: Any?) = FYTxtConfig.observe { `ImportEmpty`.fmt(args) }

        /** 导入的覆写配置 */
        val `ImportDefaultName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Imported Override Config"""
                        RootMLangTags.ZH -> """导入的覆写配置"""
                        else -> null
                    }
                } ?: """导入的覆写配置"""

        /** 导入的覆写配置 */
        @Composable
        fun `ImportDefaultName`(vararg args: Any?) =
            FYTxtConfig.observe { `ImportDefaultName`.fmt(args) }
    }

    object `Dns` {
        init {
            RootMLangGroups
        }

        /** DNS 策略 */
        val `Policy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS Policy"""
                        RootMLangTags.ZH -> """DNS 策略"""
                        else -> null
                    }
                } ?: """DNS 策略"""

        /** DNS 策略 */
        @Composable fun `Policy`(vararg args: Any?) = FYTxtConfig.observe { `Policy`.fmt(args) }

        /** 不修改 */
        val `PolicyNotModify`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Don't Modify"""
                        RootMLangTags.ZH -> """不修改"""
                        else -> null
                    }
                } ?: """不修改"""

        /** 不修改 */
        @Composable
        fun `PolicyNotModify`(vararg args: Any?) =
            FYTxtConfig.observe { `PolicyNotModify`.fmt(args) }

        /** 强制启用 */
        val `PolicyForceEnable`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Force Enable"""
                        RootMLangTags.ZH -> """强制启用"""
                        else -> null
                    }
                } ?: """强制启用"""

        /** 强制启用 */
        @Composable
        fun `PolicyForceEnable`(vararg args: Any?) =
            FYTxtConfig.observe { `PolicyForceEnable`.fmt(args) }

        /** 使用内置 */
        val `PolicyUseBuiltin`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use Built-in"""
                        RootMLangTags.ZH -> """使用内置"""
                        else -> null
                    }
                } ?: """使用内置"""

        /** 使用内置 */
        @Composable
        fun `PolicyUseBuiltin`(vararg args: Any?) =
            FYTxtConfig.observe { `PolicyUseBuiltin`.fmt(args) }

        /** 优先 HTTP/3 */
        val `PreferH3`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Prefer HTTP/3"""
                        RootMLangTags.ZH -> """优先 HTTP/3"""
                        else -> null
                    }
                } ?: """优先 HTTP/3"""

        /** 优先 HTTP/3 */
        @Composable fun `PreferH3`(vararg args: Any?) = FYTxtConfig.observe { `PreferH3`.fmt(args) }

        /** 监听地址 */
        val `Listen`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Listen Address"""
                        RootMLangTags.ZH -> """监听地址"""
                        else -> null
                    }
                } ?: """监听地址"""

        /** 监听地址 */
        @Composable fun `Listen`(vararg args: Any?) = FYTxtConfig.observe { `Listen`.fmt(args) }

        /** 例如：0.0.0.0:53 */
        val `ListenHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: 0.0.0.0:53"""
                        RootMLangTags.ZH -> """例如：0.0.0.0:53"""
                        else -> null
                    }
                } ?: """例如：0.0.0.0:53"""

        /** 例如：0.0.0.0:53 */
        @Composable
        fun `ListenHint`(vararg args: Any?) = FYTxtConfig.observe { `ListenHint`.fmt(args) }

        /** DNS IPv6 */
        val `Ipv6`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS IPv6"""
                        RootMLangTags.ZH -> """DNS IPv6"""
                        else -> null
                    }
                } ?: """DNS IPv6"""

        /** DNS IPv6 */
        @Composable fun `Ipv6`(vararg args: Any?) = FYTxtConfig.observe { `Ipv6`.fmt(args) }

        /** 使用 hosts */
        val `UseHosts`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use Hosts"""
                        RootMLangTags.ZH -> """使用 hosts"""
                        else -> null
                    }
                } ?: """使用 hosts"""

        /** 使用 hosts */
        @Composable fun `UseHosts`(vararg args: Any?) = FYTxtConfig.observe { `UseHosts`.fmt(args) }

        /** 追加系统 DNS */
        val `AppendSystem`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Append System DNS"""
                        RootMLangTags.ZH -> """追加系统 DNS"""
                        else -> null
                    }
                } ?: """追加系统 DNS"""

        /** 追加系统 DNS */
        @Composable
        fun `AppendSystem`(vararg args: Any?) = FYTxtConfig.observe { `AppendSystem`.fmt(args) }

        /** 增强模式 */
        val `EnhancedMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enhanced Mode"""
                        RootMLangTags.ZH -> """增强模式"""
                        else -> null
                    }
                } ?: """增强模式"""

        /** 增强模式 */
        @Composable
        fun `EnhancedMode`(vararg args: Any?) = FYTxtConfig.observe { `EnhancedMode`.fmt(args) }

        /** 不修改 */
        val `EnhancedNotModify`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Don't Modify"""
                        RootMLangTags.ZH -> """不修改"""
                        else -> null
                    }
                } ?: """不修改"""

        /** 不修改 */
        @Composable
        fun `EnhancedNotModify`(vararg args: Any?) =
            FYTxtConfig.observe { `EnhancedNotModify`.fmt(args) }

        /** 禁用 */
        val `EnhancedDisable`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Disable"""
                        RootMLangTags.ZH -> """禁用"""
                        else -> null
                    }
                } ?: """禁用"""

        /** 禁用 */
        @Composable
        fun `EnhancedDisable`(vararg args: Any?) =
            FYTxtConfig.observe { `EnhancedDisable`.fmt(args) }

        /** FakeIP */
        val `EnhancedFakeip`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP"""
                        RootMLangTags.ZH -> """FakeIP"""
                        else -> null
                    }
                } ?: """FakeIP"""

        /** FakeIP */
        @Composable
        fun `EnhancedFakeip`(vararg args: Any?) = FYTxtConfig.observe { `EnhancedFakeip`.fmt(args) }

        /** Mapping */
        val `EnhancedMapping`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Mapping"""
                        RootMLangTags.ZH -> """Mapping"""
                        else -> null
                    }
                } ?: """Mapping"""

        /** Mapping */
        @Composable
        fun `EnhancedMapping`(vararg args: Any?) =
            FYTxtConfig.observe { `EnhancedMapping`.fmt(args) }

        /** Direct 遵循策略 */
        val `DirectFollowPolicy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Direct Follow Policy"""
                        RootMLangTags.ZH -> """Direct 遵循策略"""
                        else -> null
                    }
                } ?: """Direct 遵循策略"""

        /** Direct 遵循策略 */
        @Composable
        fun `DirectFollowPolicy`(vararg args: Any?) =
            FYTxtConfig.observe { `DirectFollowPolicy`.fmt(args) }

        /** IPv6 超时 */
        val `Ipv6Timeout`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """IPv6 Timeout"""
                        RootMLangTags.ZH -> """IPv6 超时"""
                        else -> null
                    }
                } ?: """IPv6 超时"""

        /** IPv6 超时 */
        @Composable
        fun `Ipv6Timeout`(vararg args: Any?) = FYTxtConfig.observe { `Ipv6Timeout`.fmt(args) }

        /** DNS 服务器 */
        val `Servers`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS Servers"""
                        RootMLangTags.ZH -> """DNS 服务器"""
                        else -> null
                    }
                } ?: """DNS 服务器"""

        /** DNS 服务器 */
        @Composable fun `Servers`(vararg args: Any?) = FYTxtConfig.observe { `Servers`.fmt(args) }

        /** 例如：8.8.8.8, tls://dns.google */
        val `ServersHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: 8.8.8.8, tls://dns.google"""
                        RootMLangTags.ZH -> """例如：8.8.8.8, tls://dns.google"""
                        else -> null
                    }
                } ?: """例如：8.8.8.8, tls://dns.google"""

        /** 例如：8.8.8.8, tls://dns.google */
        @Composable
        fun `ServersHint`(vararg args: Any?) = FYTxtConfig.observe { `ServersHint`.fmt(args) }

        /** 备用 DNS */
        val `Fallback`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fallback DNS"""
                        RootMLangTags.ZH -> """备用 DNS"""
                        else -> null
                    }
                } ?: """备用 DNS"""

        /** 备用 DNS */
        @Composable fun `Fallback`(vararg args: Any?) = FYTxtConfig.observe { `Fallback`.fmt(args) }

        /** 例如：1.1.1.1 */
        val `FallbackHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: 1.1.1.1"""
                        RootMLangTags.ZH -> """例如：1.1.1.1"""
                        else -> null
                    }
                } ?: """例如：1.1.1.1"""

        /** 例如：1.1.1.1 */
        @Composable
        fun `FallbackHint`(vararg args: Any?) = FYTxtConfig.observe { `FallbackHint`.fmt(args) }

        /** 默认 DNS */
        val `Default`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Default DNS"""
                        RootMLangTags.ZH -> """默认 DNS"""
                        else -> null
                    }
                } ?: """默认 DNS"""

        /** 默认 DNS */
        @Composable fun `Default`(vararg args: Any?) = FYTxtConfig.observe { `Default`.fmt(args) }

        /** 用于解析 DNS 服务器域名 */
        val `DefaultHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """For resolving DNS server domains"""
                        RootMLangTags.ZH -> """用于解析 DNS 服务器域名"""
                        else -> null
                    }
                } ?: """用于解析 DNS 服务器域名"""

        /** 用于解析 DNS 服务器域名 */
        @Composable
        fun `DefaultHint`(vararg args: Any?) = FYTxtConfig.observe { `DefaultHint`.fmt(args) }

        /** FakeIP 过滤 */
        val `FakeipFilter`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP Filter"""
                        RootMLangTags.ZH -> """FakeIP 过滤"""
                        else -> null
                    }
                } ?: """FakeIP 过滤"""

        /** FakeIP 过滤 */
        @Composable
        fun `FakeipFilter`(vararg args: Any?) = FYTxtConfig.observe { `FakeipFilter`.fmt(args) }

        /** 例如：+.lan, localhost */
        val `FakeipFilterHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: +.lan, localhost"""
                        RootMLangTags.ZH -> """例如：+.lan, localhost"""
                        else -> null
                    }
                } ?: """例如：+.lan, localhost"""

        /** 例如：+.lan, localhost */
        @Composable
        fun `FakeipFilterHint`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeipFilterHint`.fmt(args) }

        /** FakeIP 过滤模式 */
        val `FakeipFilterMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP Filter Mode"""
                        RootMLangTags.ZH -> """FakeIP 过滤模式"""
                        else -> null
                    }
                } ?: """FakeIP 过滤模式"""

        /** FakeIP 过滤模式 */
        @Composable
        fun `FakeipFilterMode`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeipFilterMode`.fmt(args) }

        /** 黑名单 */
        val `FakeipBlacklist`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Blacklist"""
                        RootMLangTags.ZH -> """黑名单"""
                        else -> null
                    }
                } ?: """黑名单"""

        /** 黑名单 */
        @Composable
        fun `FakeipBlacklist`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeipBlacklist`.fmt(args) }

        /** 白名单 */
        val `FakeipWhitelist`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Whitelist"""
                        RootMLangTags.ZH -> """白名单"""
                        else -> null
                    }
                } ?: """白名单"""

        /** 白名单 */
        @Composable
        fun `FakeipWhitelist`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeipWhitelist`.fmt(args) }

        /** GeoIP 回退 */
        val `FallbackGeoip`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GeoIP Fallback"""
                        RootMLangTags.ZH -> """GeoIP 回退"""
                        else -> null
                    }
                } ?: """GeoIP 回退"""

        /** GeoIP 回退 */
        @Composable
        fun `FallbackGeoip`(vararg args: Any?) = FYTxtConfig.observe { `FallbackGeoip`.fmt(args) }

        /** GeoIP 代码 */
        val `FallbackGeoipCode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GeoIP Code"""
                        RootMLangTags.ZH -> """GeoIP 代码"""
                        else -> null
                    }
                } ?: """GeoIP 代码"""

        /** GeoIP 代码 */
        @Composable
        fun `FallbackGeoipCode`(vararg args: Any?) =
            FYTxtConfig.observe { `FallbackGeoipCode`.fmt(args) }

        /** 例如：CN */
        val `FallbackGeoipCodeHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: CN"""
                        RootMLangTags.ZH -> """例如：CN"""
                        else -> null
                    }
                } ?: """例如：CN"""

        /** 例如：CN */
        @Composable
        fun `FallbackGeoipCodeHint`(vararg args: Any?) =
            FYTxtConfig.observe { `FallbackGeoipCodeHint`.fmt(args) }

        /** 域名回退 */
        val `FallbackDomain`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Domain Fallback"""
                        RootMLangTags.ZH -> """域名回退"""
                        else -> null
                    }
                } ?: """域名回退"""

        /** 域名回退 */
        @Composable
        fun `FallbackDomain`(vararg args: Any?) = FYTxtConfig.observe { `FallbackDomain`.fmt(args) }

        /** 例如：+.google.com */
        val `FallbackDomainHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: +.google.com"""
                        RootMLangTags.ZH -> """例如：+.google.com"""
                        else -> null
                    }
                } ?: """例如：+.google.com"""

        /** 例如：+.google.com */
        @Composable
        fun `FallbackDomainHint`(vararg args: Any?) =
            FYTxtConfig.observe { `FallbackDomainHint`.fmt(args) }

        /** IP CIDR 回退 */
        val `FallbackIpcidr`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """IP CIDR Fallback"""
                        RootMLangTags.ZH -> """IP CIDR 回退"""
                        else -> null
                    }
                } ?: """IP CIDR 回退"""

        /** IP CIDR 回退 */
        @Composable
        fun `FallbackIpcidr`(vararg args: Any?) = FYTxtConfig.observe { `FallbackIpcidr`.fmt(args) }

        /** 例如：240.0.0.0/4 */
        val `FallbackIpcidrHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: 240.0.0.0/4"""
                        RootMLangTags.ZH -> """例如：240.0.0.0/4"""
                        else -> null
                    }
                } ?: """例如：240.0.0.0/4"""

        /** 例如：240.0.0.0/4 */
        @Composable
        fun `FallbackIpcidrHint`(vararg args: Any?) =
            FYTxtConfig.observe { `FallbackIpcidrHint`.fmt(args) }

        /** Geosite 回退 */
        val `FallbackGeosite`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Geosite Fallback"""
                        RootMLangTags.ZH -> """Geosite 回退"""
                        else -> null
                    }
                } ?: """Geosite 回退"""

        /** Geosite 回退 */
        @Composable
        fun `FallbackGeosite`(vararg args: Any?) =
            FYTxtConfig.observe { `FallbackGeosite`.fmt(args) }

        /** 例如：gfw */
        val `FallbackGeositeHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """e.g.: gfw"""
                        RootMLangTags.ZH -> """例如：gfw"""
                        else -> null
                    }
                } ?: """例如：gfw"""

        /** 例如：gfw */
        @Composable
        fun `FallbackGeositeHint`(vararg args: Any?) =
            FYTxtConfig.observe { `FallbackGeositeHint`.fmt(args) }

        /** DNS 策略 */
        val `NameserverPolicy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Nameserver Policy"""
                        RootMLangTags.ZH -> """DNS 策略"""
                        else -> null
                    }
                } ?: """DNS 策略"""

        /** DNS 策略 */
        @Composable
        fun `NameserverPolicy`(vararg args: Any?) =
            FYTxtConfig.observe { `NameserverPolicy`.fmt(args) }

        /** 域名匹配规则 */
        val `NameserverPolicyKey`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Domain match rule"""
                        RootMLangTags.ZH -> """域名匹配规则"""
                        else -> null
                    }
                } ?: """域名匹配规则"""

        /** 域名匹配规则 */
        @Composable
        fun `NameserverPolicyKey`(vararg args: Any?) =
            FYTxtConfig.observe { `NameserverPolicyKey`.fmt(args) }

        /** DNS 服务器 */
        val `NameserverPolicyValue`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS server"""
                        RootMLangTags.ZH -> """DNS 服务器"""
                        else -> null
                    }
                } ?: """DNS 服务器"""

        /** DNS 服务器 */
        @Composable
        fun `NameserverPolicyValue`(vararg args: Any?) =
            FYTxtConfig.observe { `NameserverPolicyValue`.fmt(args) }

        /** Fake-IP IPv6 网段 */
        val `FakeipRange6`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fake-IP IPv6 Range"""
                        RootMLangTags.ZH -> """Fake-IP IPv6 网段"""
                        else -> null
                    }
                } ?: """Fake-IP IPv6 网段"""

        /** Fake-IP IPv6 网段 */
        @Composable
        fun `FakeipRange6`(vararg args: Any?) = FYTxtConfig.observe { `FakeipRange6`.fmt(args) }

        /** Fake-IP TTL */
        val `FakeipTtl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Fake-IP TTL"""
                        RootMLangTags.ZH -> """Fake-IP TTL"""
                        else -> null
                    }
                } ?: """Fake-IP TTL"""

        /** Fake-IP TTL */
        @Composable
        fun `FakeipTtl`(vararg args: Any?) = FYTxtConfig.observe { `FakeipTtl`.fmt(args) }

        /** 代理服务器 DNS */
        val `ProxyServerNameserver`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Server Nameserver"""
                        RootMLangTags.ZH -> """代理服务器 DNS"""
                        else -> null
                    }
                } ?: """代理服务器 DNS"""

        /** 代理服务器 DNS */
        @Composable
        fun `ProxyServerNameserver`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyServerNameserver`.fmt(args) }

        /** 直连 DNS */
        val `DirectNameserver`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Direct Nameserver"""
                        RootMLangTags.ZH -> """直连 DNS"""
                        else -> null
                    }
                } ?: """直连 DNS"""

        /** 直连 DNS */
        @Composable
        fun `DirectNameserver`(vararg args: Any?) =
            FYTxtConfig.observe { `DirectNameserver`.fmt(args) }

        /** 代理服务器 DNS 策略 */
        val `ProxyServerNameserverPolicy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Server Nameserver Policy"""
                        RootMLangTags.ZH -> """代理服务器 DNS 策略"""
                        else -> null
                    }
                } ?: """代理服务器 DNS 策略"""

        /** 代理服务器 DNS 策略 */
        @Composable
        fun `ProxyServerNameserverPolicy`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyServerNameserverPolicy`.fmt(args) }

        /** 域名 / RuleSet */
        val `ProxyServerNameserverPolicyKey`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Domain / RuleSet"""
                        RootMLangTags.ZH -> """域名 / RuleSet"""
                        else -> null
                    }
                } ?: """域名 / RuleSet"""

        /** 域名 / RuleSet */
        @Composable
        fun `ProxyServerNameserverPolicyKey`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyServerNameserverPolicyKey`.fmt(args) }

        /** DNS 服务器 */
        val `ProxyServerNameserverPolicyValue`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS server"""
                        RootMLangTags.ZH -> """DNS 服务器"""
                        else -> null
                    }
                } ?: """DNS 服务器"""

        /** DNS 服务器 */
        @Composable
        fun `ProxyServerNameserverPolicyValue`(vararg args: Any?) =
            FYTxtConfig.observe { `ProxyServerNameserverPolicyValue`.fmt(args) }

        /** Hosts */
        val `Hosts`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Hosts"""
                        RootMLangTags.ZH -> """Hosts"""
                        else -> null
                    }
                } ?: """Hosts"""

        /** Hosts */
        @Composable fun `Hosts`(vararg args: Any?) = FYTxtConfig.observe { `Hosts`.fmt(args) }

        /** 域名 */
        val `HostsKey`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Domain"""
                        RootMLangTags.ZH -> """域名"""
                        else -> null
                    }
                } ?: """域名"""

        /** 域名 */
        @Composable fun `HostsKey`(vararg args: Any?) = FYTxtConfig.observe { `HostsKey`.fmt(args) }

        /** IP */
        val `HostsValue`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """IP"""
                        RootMLangTags.ZH -> """IP"""
                        else -> null
                    }
                } ?: """IP"""

        /** IP */
        @Composable
        fun `HostsValue`(vararg args: Any?) = FYTxtConfig.observe { `HostsValue`.fmt(args) }
    }

    object `General` {
        init {
            RootMLangGroups
        }

        /** HTTP 端口 */
        val `HttpPort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """HTTP Port"""
                        RootMLangTags.ZH -> """HTTP 端口"""
                        else -> null
                    }
                } ?: """HTTP 端口"""

        /** HTTP 端口 */
        @Composable fun `HttpPort`(vararg args: Any?) = FYTxtConfig.observe { `HttpPort`.fmt(args) }

        /** TLS 端口 */
        val `TlsPort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """TLS Port"""
                        RootMLangTags.ZH -> """TLS 端口"""
                        else -> null
                    }
                } ?: """TLS 端口"""

        /** TLS 端口 */
        @Composable fun `TlsPort`(vararg args: Any?) = FYTxtConfig.observe { `TlsPort`.fmt(args) }

        /** QUIC 端口 */
        val `QuicPort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """QUIC Port"""
                        RootMLangTags.ZH -> """QUIC 端口"""
                        else -> null
                    }
                } ?: """QUIC 端口"""

        /** QUIC 端口 */
        @Composable fun `QuicPort`(vararg args: Any?) = FYTxtConfig.observe { `QuicPort`.fmt(args) }

        /** SOCKS 端口 */
        val `SocksPort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """SOCKS Port"""
                        RootMLangTags.ZH -> """SOCKS 端口"""
                        else -> null
                    }
                } ?: """SOCKS 端口"""

        /** SOCKS 端口 */
        @Composable
        fun `SocksPort`(vararg args: Any?) = FYTxtConfig.observe { `SocksPort`.fmt(args) }

        /** Mixed 端口 */
        val `MixedPort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Mixed Port"""
                        RootMLangTags.ZH -> """Mixed 端口"""
                        else -> null
                    }
                } ?: """Mixed 端口"""

        /** Mixed 端口 */
        @Composable
        fun `MixedPort`(vararg args: Any?) = FYTxtConfig.observe { `MixedPort`.fmt(args) }

        /** Redirect 端口 */
        val `RedirectPort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Redirect Port"""
                        RootMLangTags.ZH -> """Redirect 端口"""
                        else -> null
                    }
                } ?: """Redirect 端口"""

        /** Redirect 端口 */
        @Composable
        fun `RedirectPort`(vararg args: Any?) = FYTxtConfig.observe { `RedirectPort`.fmt(args) }

        /** TProxy 端口 */
        val `TproxyPort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """TProxy Port"""
                        RootMLangTags.ZH -> """TProxy 端口"""
                        else -> null
                    }
                } ?: """TProxy 端口"""

        /** TProxy 端口 */
        @Composable
        fun `TproxyPort`(vararg args: Any?) = FYTxtConfig.observe { `TproxyPort`.fmt(args) }

        /** 允许局域网 */
        val `AllowLan`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow LAN"""
                        RootMLangTags.ZH -> """允许局域网"""
                        else -> null
                    }
                } ?: """允许局域网"""

        /** 允许局域网 */
        @Composable fun `AllowLan`(vararg args: Any?) = FYTxtConfig.observe { `AllowLan`.fmt(args) }

        /** IPv6 */
        val `Ipv6`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """IPv6"""
                        RootMLangTags.ZH -> """IPv6"""
                        else -> null
                    }
                } ?: """IPv6"""

        /** IPv6 */
        @Composable fun `Ipv6`(vararg args: Any?) = FYTxtConfig.observe { `Ipv6`.fmt(args) }

        /** 代理模式 */
        val `ProxyMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Mode"""
                        RootMLangTags.ZH -> """代理模式"""
                        else -> null
                    }
                } ?: """代理模式"""

        /** 代理模式 */
        @Composable
        fun `ProxyMode`(vararg args: Any?) = FYTxtConfig.observe { `ProxyMode`.fmt(args) }

        /** 日志等级 */
        val `LogLevel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Log Level"""
                        RootMLangTags.ZH -> """日志等级"""
                        else -> null
                    }
                } ?: """日志等级"""

        /** 日志等级 */
        @Composable fun `LogLevel`(vararg args: Any?) = FYTxtConfig.observe { `LogLevel`.fmt(args) }
    }

    object `Label` {
        init {
            RootMLangGroups
        }

        /** 缓存算法 */
        val `CacheAlgorithm`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cache Algorithm"""
                        RootMLangTags.ZH -> """缓存算法"""
                        else -> null
                    }
                } ?: """缓存算法"""

        /** 缓存算法 */
        @Composable
        fun `CacheAlgorithm`(vararg args: Any?) = FYTxtConfig.observe { `CacheAlgorithm`.fmt(args) }

        /** 启用 */
        val `Enable`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enable"""
                        RootMLangTags.ZH -> """启用"""
                        else -> null
                    }
                } ?: """启用"""

        /** 启用 */
        @Composable fun `Enable`(vararg args: Any?) = FYTxtConfig.observe { `Enable`.fmt(args) }

        /** FakeIP 地址段 */
        val `FakeIpRange`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP Range"""
                        RootMLangTags.ZH -> """FakeIP 地址段"""
                        else -> null
                    }
                } ?: """FakeIP 地址段"""

        /** FakeIP 地址段 */
        @Composable
        fun `FakeIpRange`(vararg args: Any?) = FYTxtConfig.observe { `FakeIpRange`.fmt(args) }

        /** 强制 DNS 映射 */
        val `ForceDnsMapping`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Force DNS Mapping"""
                        RootMLangTags.ZH -> """强制 DNS 映射"""
                        else -> null
                    }
                } ?: """强制 DNS 映射"""

        /** 强制 DNS 映射 */
        @Composable
        fun `ForceDnsMapping`(vararg args: Any?) =
            FYTxtConfig.observe { `ForceDnsMapping`.fmt(args) }

        /** 强制嗅探域名 */
        val `ForceDomain`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Force Domain"""
                        RootMLangTags.ZH -> """强制嗅探域名"""
                        else -> null
                    }
                } ?: """强制嗅探域名"""

        /** 强制嗅探域名 */
        @Composable
        fun `ForceDomain`(vararg args: Any?) = FYTxtConfig.observe { `ForceDomain`.fmt(args) }

        /** HTTP 覆写 */
        val `HttpOverride`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """HTTP Override"""
                        RootMLangTags.ZH -> """HTTP 覆写"""
                        else -> null
                    }
                } ?: """HTTP 覆写"""

        /** HTTP 覆写 */
        @Composable
        fun `HttpOverride`(vararg args: Any?) = FYTxtConfig.observe { `HttpOverride`.fmt(args) }

        /** Keep Alive 空闲阈值 */
        val `KeepAliveIdle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Keep Alive Idle"""
                        RootMLangTags.ZH -> """Keep Alive 空闲阈值"""
                        else -> null
                    }
                } ?: """Keep Alive 空闲阈值"""

        /** Keep Alive 空闲阈值 */
        @Composable
        fun `KeepAliveIdle`(vararg args: Any?) = FYTxtConfig.observe { `KeepAliveIdle`.fmt(args) }

        /** Keep Alive 间隔 */
        val `KeepAliveInterval`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Keep Alive Interval"""
                        RootMLangTags.ZH -> """Keep Alive 间隔"""
                        else -> null
                    }
                } ?: """Keep Alive 间隔"""

        /** Keep Alive 间隔 */
        @Composable
        fun `KeepAliveInterval`(vararg args: Any?) =
            FYTxtConfig.observe { `KeepAliveInterval`.fmt(args) }

        /** 覆写目标地址 */
        val `OverrideDestination`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override Destination"""
                        RootMLangTags.ZH -> """覆写目标地址"""
                        else -> null
                    }
                } ?: """覆写目标地址"""

        /** 覆写目标地址 */
        @Composable
        fun `OverrideDestination`(vararg args: Any?) =
            FYTxtConfig.observe { `OverrideDestination`.fmt(args) }

        /** 解析纯 IP */
        val `ParsePureIp`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Parse Pure IP"""
                        RootMLangTags.ZH -> """解析纯 IP"""
                        else -> null
                    }
                } ?: """解析纯 IP"""

        /** 解析纯 IP */
        @Composable
        fun `ParsePureIp`(vararg args: Any?) = FYTxtConfig.observe { `ParsePureIp`.fmt(args) }

        /** QUIC 覆写 */
        val `QuicOverride`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """QUIC Override"""
                        RootMLangTags.ZH -> """QUIC 覆写"""
                        else -> null
                    }
                } ?: """QUIC 覆写"""

        /** QUIC 覆写 */
        @Composable
        fun `QuicOverride`(vararg args: Any?) = FYTxtConfig.observe { `QuicOverride`.fmt(args) }

        /** 遵循路由规则 */
        val `RespectRules`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Respect Rules"""
                        RootMLangTags.ZH -> """遵循路由规则"""
                        else -> null
                    }
                } ?: """遵循路由规则"""

        /** 遵循路由规则 */
        @Composable
        fun `RespectRules`(vararg args: Any?) = FYTxtConfig.observe { `RespectRules`.fmt(args) }

        /** 覆盖规则 */
        val `RulesReplace`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override Rules"""
                        RootMLangTags.ZH -> """覆盖规则"""
                        else -> null
                    }
                } ?: """覆盖规则"""

        /** 覆盖规则 */
        @Composable
        fun `RulesReplace`(vararg args: Any?) = FYTxtConfig.observe { `RulesReplace`.fmt(args) }

        /** 跳过嗅探域名 */
        val `SkipDomain`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Skip Domain"""
                        RootMLangTags.ZH -> """跳过嗅探域名"""
                        else -> null
                    }
                } ?: """跳过嗅探域名"""

        /** 跳过嗅探域名 */
        @Composable
        fun `SkipDomain`(vararg args: Any?) = FYTxtConfig.observe { `SkipDomain`.fmt(args) }

        /** TLS 覆写 */
        val `TlsOverride`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """TLS Override"""
                        RootMLangTags.ZH -> """TLS 覆写"""
                        else -> null
                    }
                } ?: """TLS 覆写"""

        /** TLS 覆写 */
        @Composable
        fun `TlsOverride`(vararg args: Any?) = FYTxtConfig.observe { `TlsOverride`.fmt(args) }

        /** 使用系统 Hosts */
        val `UseSystemHosts`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use System Hosts"""
                        RootMLangTags.ZH -> """使用系统 Hosts"""
                        else -> null
                    }
                } ?: """使用系统 Hosts"""

        /** 使用系统 Hosts */
        @Composable
        fun `UseSystemHosts`(vararg args: Any?) = FYTxtConfig.observe { `UseSystemHosts`.fmt(args) }
    }
}
