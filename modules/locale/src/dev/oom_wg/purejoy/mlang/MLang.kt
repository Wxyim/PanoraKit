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

import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtGroup
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtTag

object `MLang` {
    init {
        `MLangGroups`
    }

    enum class `MLangGroups` : FYTxtGroup {
        `lang` {
            override val stats = mapOf(`MLangTags`.EN to 1.0, `MLangTags`.ZH to 1.0)
        };

        companion object {
            init {
                FYTxtConfig.init(`lang`, `MLangTags`.entries)
            }
        }
    }

    enum class `MLangTags` : FYTxtTag {
        EN {
            override val pattern = null
        },
        ZH {
            override val pattern = null
        },
    }

    object `About` {
        val `Title`
            get() = MLangAbout.`Title`

        val `Copyright`
            get() = MLangAbout.`Copyright`

        val `App`
            get() = MLangAbout.`App`

        val `Section`
            get() = MLangAbout.`Section`

        val `Link`
            get() = MLangAbout.`Link`

        val `License`
            get() = MLangAbout.`License`
    }

    object `AccessControl` {
        val `Title`
            get() = MLangAccessControl.`Title`

        val `Search`
            get() = MLangAccessControl.`Search`

        val `AppList`
            get() = MLangAccessControl.`AppList`

        val `Settings`
            get() = MLangAccessControl.`Settings`

        val `SortMode`
            get() = MLangAccessControl.`SortMode`

        val `Button`
            get() = MLangAccessControl.`Button`
    }

    object `AppSettings` {
        val `Title`
            get() = MLangAppSettings.`Title`

        val `Section`
            get() = MLangAppSettings.`Section`

        val `Behavior`
            get() = MLangAppSettings.`Behavior`

        val `Interface`
            get() = MLangAppSettings.`Interface`

        val `ServiceSection`
            get() = MLangAppSettings.`ServiceSection`

        val `Network`
            get() = MLangAppSettings.`Network`

        val `Cleanup`
            get() = MLangAppSettings.`Cleanup`

        val `WarningDialog`
            get() = MLangAppSettings.`WarningDialog`

        val `EditDialog`
            get() = MLangAppSettings.`EditDialog`

        val `Button`
            get() = MLangAppSettings.`Button`
    }

    object `Component` {
        val `ProfileCard`
            get() = MLangComponent.`ProfileCard`

        val `WebView`
            get() = MLangComponent.`WebView`

        val `Selector`
            get() = MLangComponent.`Selector`

        val `Navigation`
            get() = MLangComponent.`Navigation`

        val `Message`
            get() = MLangComponent.`Message`

        val `Button`
            get() = MLangComponent.`Button`

        val `Loading`
            get() = MLangComponent.`Loading`

        object `Update` {
            val `Title`
                get() = MLangComponent.`Update`.`Title`

            val `Message`
                get() = MLangComponent.`Update`.`Message`
        }

        val `ConfigInput`
            get() = MLangComponent.`ConfigInput`

        val `Accessibility`
            get() = MLangComponent.`Accessibility`

        val `BottomBar`
            get() = MLangComponent.`BottomBar`

        object `Editor` {
            val `CountItems`
                get() = MLangComponent.`Editor`.`CountItems`

            val `Action`
                get() = MLangComponent.`Editor`.`Action`

            val `Dialog`
                get() = MLangComponent.`Editor`.`Dialog`

            val `Empty`
                get() = MLangComponent.`Editor`.`Empty`

            val `Error`
                get() = MLangComponent.`Editor`.`Error`

            val `Rule`
                get() = MLangComponent.`Editor`.`Rule`
        }
    }

    object `Connection` {
        val `Title`
            get() = MLangConnection.`Title`

        val `Summary`
            get() = MLangConnection.`Summary`

        val `Search`
            get() = MLangConnection.`Search`

        val `SearchHint`
            get() = MLangConnection.`SearchHint`

        val `SortBy`
            get() = MLangConnection.`SortBy`

        val `Loading`
            get() = MLangConnection.`Loading`

        val `Empty`
            get() = MLangConnection.`Empty`

        val `NoResults`
            get() = MLangConnection.`NoResults`

        val `Tab`
            get() = MLangConnection.`Tab`

        val `Sort`
            get() = MLangConnection.`Sort`

        val `RelativeTime`
            get() = MLangConnection.`RelativeTime`

        val `Detail`
            get() = MLangConnection.`Detail`
    }

    object `Home` {
        val `Title`
            get() = MLangHome.`Title`

        val `Message`
            get() = MLangHome.`Message`

        val `Control`
            get() = MLangHome.`Control`

        val `Profile`
            get() = MLangHome.`Profile`

        val `NodeInfo`
            get() = MLangHome.`NodeInfo`

        val `IpInfo`
            get() = MLangHome.`IpInfo`

        val `Status`
            get() = MLangHome.`Status`
    }

    object `Log` {
        val `Title`
            get() = MLangLog.`Title`

        val `Action`
            get() = MLangLog.`Action`

        val `Empty`
            get() = MLangLog.`Empty`

        val `Detail`
            get() = MLangLog.`Detail`

        val `History`
            get() = MLangLog.`History`

        val `Startup`
            get() = MLangLog.`Startup`
    }

    object `MetaFeature` {
        val `Title`
            get() = MLangMetaFeature.`Title`

        val `RecentRequests`
            get() = MLangMetaFeature.`RecentRequests`

        val `RuntimeConfig`
            get() = MLangMetaFeature.`RuntimeConfig`

        val `GeoX`
            get() = MLangMetaFeature.`GeoX`

        val `Download`
            get() = MLangMetaFeature.`Download`

        val `Dashboard`
            get() = MLangMetaFeature.`Dashboard`
    }

    object `NetworkSettings` {
        val `Title`
            get() = MLangNetworkSettings.`Title`

        val `Section`
            get() = MLangNetworkSettings.`Section`

        val `VpnService`
            get() = MLangNetworkSettings.`VpnService`

        val `HttpMode`
            get() = MLangNetworkSettings.`HttpMode`

        val `VpnOptions`
            get() = MLangNetworkSettings.`VpnOptions`

        val `ProxyOptions`
            get() = MLangNetworkSettings.`ProxyOptions`

        val `RootTun`
            get() = MLangNetworkSettings.`RootTun`

        val `Error`
            get() = MLangNetworkSettings.`Error`
    }

    object `Onboarding` {
        val `Navigation`
            get() = MLangOnboarding.`Navigation`

        object `Permission` {
            val `Title`
                get() = MLangOnboarding.`Permission`.`Title`

            val `Subtitle`
                get() = MLangOnboarding.`Permission`.`Subtitle`

            val `Common`
                get() = MLangOnboarding.`Permission`.`Common`

            val `Notification`
                get() = MLangOnboarding.`Permission`.`Notification`

            val `AppList`
                get() = MLangOnboarding.`Permission`.`AppList`
        }

        object `Privacy` {
            val `Title`
                get() = MLangOnboarding.`Privacy`.`Title`

            val `Subtitle`
                get() = MLangOnboarding.`Privacy`.`Subtitle`

            val `RichTextLead`
                get() = MLangOnboarding.`Privacy`.`RichTextLead`

            val `RichTextPrefix`
                get() = MLangOnboarding.`Privacy`.`RichTextPrefix`

            val `RichTextSuffix`
                get() = MLangOnboarding.`Privacy`.`RichTextSuffix`

            val `PolicyLink`
                get() = MLangOnboarding.`Privacy`.`PolicyLink`

            val `Privacy`
                get() = MLangOnboarding.`Privacy`.`Privacy`

            val `Accept`
                get() = MLangOnboarding.`Privacy`.`Accept`
        }

        val `Personalize`
            get() = MLangOnboarding.`Personalize`

        val `Finish`
            get() = MLangOnboarding.`Finish`

        object `Project` {
            val `Github`
                get() = MLangOnboarding.`Project`.`Github`
        }

        val `Sheet`
            get() = MLangOnboarding.`Sheet`
    }

    object `OpenSourceLicenses` {
        val `Title`
            get() = MLangOpenSourceLicenses.`Title`

        val `LicenseSheet`
            get() = MLangOpenSourceLicenses.`LicenseSheet`
    }

    object `Override` {
        val `Title`
            get() = MLangOverride.`Title`

        val `Action`
            get() = MLangOverride.`Action`

        val `Empty`
            get() = MLangOverride.`Empty`

        val `Status`
            get() = MLangOverride.`Status`

        val `Card`
            get() = MLangOverride.`Card`

        object `Import` {
            val `ReadError`
                get() = MLangOverride.`Import`.`ReadError`

            val `Success`
                get() = MLangOverride.`Import`.`Success`

            val `SuccessDefault`
                get() = MLangOverride.`Import`.`SuccessDefault`

            val `Failed`
                get() = MLangOverride.`Import`.`Failed`

            val `FileError`
                get() = MLangOverride.`Import`.`FileError`

            val `SurgeSuccess`
                get() = MLangOverride.`Import`.`SurgeSuccess`

            val `SurgeSuccessDefault`
                get() = MLangOverride.`Import`.`SurgeSuccessDefault`

            val `SurgeNoRules`
                get() = MLangOverride.`Import`.`SurgeNoRules`

            val `SurgeImportDescription`
                get() = MLangOverride.`Import`.`SurgeImportDescription`

            val `PluginNoRules`
                get() = MLangOverride.`Import`.`PluginNoRules`

            val `PluginImportDescription`
                get() = MLangOverride.`Import`.`PluginImportDescription`

            val `AutoDetectFailed`
                get() = MLangOverride.`Import`.`AutoDetectFailed`

            val `UrlLabel`
                get() = MLangOverride.`Import`.`UrlLabel`

            val `UrlDownloading`
                get() = MLangOverride.`Import`.`UrlDownloading`

            val `UrlInvalidScheme`
                get() = MLangOverride.`Import`.`UrlInvalidScheme`

            val `UrlHttpsRequired`
                get() = MLangOverride.`Import`.`UrlHttpsRequired`

            val `UrlHttpError`
                get() = MLangOverride.`Import`.`UrlHttpError`

            val `UrlContentTooLarge`
                get() = MLangOverride.`Import`.`UrlContentTooLarge`

            val `UrlRedirectInvalid`
                get() = MLangOverride.`Import`.`UrlRedirectInvalid`

            val `UrlTooManyRedirects`
                get() = MLangOverride.`Import`.`UrlTooManyRedirects`

            val `UrlSheet`
                get() = MLangOverride.`Import`.`UrlSheet`
        }

        val `Export`
            get() = MLangOverride.`Export`

        object `Dialog` {
            val `Create`
                get() = MLangOverride.`Dialog`.`Create`

            val `Delete`
                get() = MLangOverride.`Dialog`.`Delete`

            val `EditOptions`
                get() = MLangOverride.`Dialog`.`EditOptions`

            val `Button`
                get() = MLangOverride.`Dialog`.`Button`
        }

        object `Edit` {
            val `TitleNew`
                get() = MLangOverride.`Edit`.`TitleNew`

            val `JsonEditHint`
                get() = MLangOverride.`Edit`.`JsonEditHint`

            val `StructuredObjectListHint`
                get() = MLangOverride.`Edit`.`StructuredObjectListHint`

            val `StructuredProviderDictHint`
                get() = MLangOverride.`Edit`.`StructuredProviderDictHint`

            val `SubRuleGroupHint`
                get() = MLangOverride.`Edit`.`SubRuleGroupHint`

            val `JsonKeyValueHint`
                get() = MLangOverride.`Edit`.`JsonKeyValueHint`

            val `OneProviderPerLineHint`
                get() = MLangOverride.`Edit`.`OneProviderPerLineHint`

            val `TitleEdit`
                get() = MLangOverride.`Edit`.`TitleEdit`

            val `PresetApplied`
                get() = MLangOverride.`Edit`.`PresetApplied`

            val `EmptyName`
                get() = MLangOverride.`Edit`.`EmptyName`

            val `Button`
                get() = MLangOverride.`Edit`.`Button`
        }

        object `Section` {
            val `General`
                get() = MLangOverride.`Section`.`General`

            val `Dns`
                get() = MLangOverride.`Section`.`Dns`

            val `Sniffer`
                get() = MLangOverride.`Section`.`Sniffer`

            val `Inbound`
                get() = MLangOverride.`Section`.`Inbound`

            val `Tun`
                get() = MLangOverride.`Section`.`Tun`

            val `Rules`
                get() = MLangOverride.`Section`.`Rules`

            val `Proxies`
                get() = MLangOverride.`Section`.`Proxies`

            val `ProxyProviders`
                get() = MLangOverride.`Section`.`ProxyProviders`

            val `ProxyGroups`
                get() = MLangOverride.`Section`.`ProxyGroups`

            val `RuleProviders`
                get() = MLangOverride.`Section`.`RuleProviders`

            val `SubRules`
                get() = MLangOverride.`Section`.`SubRules`
        }

        val `Modifier`
            get() = MLangOverride.`Modifier`

        object `Structured` {
            val `Proxies`
                get() = MLangOverride.`Structured`.`Proxies`

            val `ProxyGroups`
                get() = MLangOverride.`Structured`.`ProxyGroups`

            val `RuleProviders`
                get() = MLangOverride.`Structured`.`RuleProviders`

            val `ProxyProviders`
                get() = MLangOverride.`Structured`.`ProxyProviders`

            val `SubRules`
                get() = MLangOverride.`Structured`.`SubRules`
        }

        object `Editor` {
            val `New`
                get() = MLangOverride.`Editor`.`New`

            val `AddNamedItem`
                get() = MLangOverride.`Editor`.`AddNamedItem`

            val `NewRule`
                get() = MLangOverride.`Editor`.`NewRule`

            val `EditRule`
                get() = MLangOverride.`Editor`.`EditRule`

            val `NewSubRuleGroup`
                get() = MLangOverride.`Editor`.`NewSubRuleGroup`

            val `UnnamedSubRuleGroup`
                get() = MLangOverride.`Editor`.`UnnamedSubRuleGroup`

            val `EditSubRuleGroup`
                get() = MLangOverride.`Editor`.`EditSubRuleGroup`

            val `ClearSubRules`
                get() = MLangOverride.`Editor`.`ClearSubRules`

            val `Unnamed`
                get() = MLangOverride.`Editor`.`Unnamed`

            val `UnnamedRule`
                get() = MLangOverride.`Editor`.`UnnamedRule`

            val `Edit`
                get() = MLangOverride.`Editor`.`Edit`

            val `DragToSort`
                get() = MLangOverride.`Editor`.`DragToSort`

            val `CancelDelete`
                get() = MLangOverride.`Editor`.`CancelDelete`

            val `DeleteSelected`
                get() = MLangOverride.`Editor`.`DeleteSelected`

            val `DeleteSelectedNamedItem`
                get() = MLangOverride.`Editor`.`DeleteSelectedNamedItem`

            val `DeleteSelectedRules`
                get() = MLangOverride.`Editor`.`DeleteSelectedRules`

            val `ClearMode`
                get() = MLangOverride.`Editor`.`ClearMode`

            val `EnterDeleteMode`
                get() = MLangOverride.`Editor`.`EnterDeleteMode`

            val `EmptyString`
                get() = MLangOverride.`Editor`.`EmptyString`

            val `ArrayItems`
                get() = MLangOverride.`Editor`.`ArrayItems`

            val `ObjectFields`
                get() = MLangOverride.`Editor`.`ObjectFields`

            val `Clear`
                get() = MLangOverride.`Editor`.`Clear`

            val `Rules`
                get() = MLangOverride.`Editor`.`Rules`

            val `AddCustom`
                get() = MLangOverride.`Editor`.`AddCustom`

            val `ContentEmpty`
                get() = MLangOverride.`Editor`.`ContentEmpty`

            val `Confirm`
                get() = MLangOverride.`Editor`.`Confirm`

            val `OneItemPerLine`
                get() = MLangOverride.`Editor`.`OneItemPerLine`

            val `AddItem`
                get() = MLangOverride.`Editor`.`AddItem`

            val `DeleteLastItem`
                get() = MLangOverride.`Editor`.`DeleteLastItem`

            val `Copy`
                get() = MLangOverride.`Editor`.`Copy`

            val `Delete`
                get() = MLangOverride.`Editor`.`Delete`

            val `MoveUp`
                get() = MLangOverride.`Editor`.`MoveUp`

            val `MoveDown`
                get() = MLangOverride.`Editor`.`MoveDown`

            val `AddObject`
                get() = MLangOverride.`Editor`.`AddObject`

            val `SubRuleName`
                get() = MLangOverride.`Editor`.`SubRuleName`

            val `NoRules`
                get() = MLangOverride.`Editor`.`NoRules`

            val `RulesConfiguredInline`
                get() = MLangOverride.`Editor`.`RulesConfiguredInline`

            val `AddSubRuleGroup`
                get() = MLangOverride.`Editor`.`AddSubRuleGroup`

            val `EditSubRule`
                get() = MLangOverride.`Editor`.`EditSubRule`

            val `KeyName`
                get() = MLangOverride.`Editor`.`KeyName`

            val `List`
                get() = MLangOverride.`Editor`.`List`

            val `EditItem`
                get() = MLangOverride.`Editor`.`EditItem`

            val `ClearCurrentMode`
                get() = MLangOverride.`Editor`.`ClearCurrentMode`

            val `NewProxyNode`
                get() = MLangOverride.`Editor`.`NewProxyNode`

            val `NewProxyGroup`
                get() = MLangOverride.`Editor`.`NewProxyGroup`

            val `EditProxyNode`
                get() = MLangOverride.`Editor`.`EditProxyNode`

            val `EditProxyGroup`
                get() = MLangOverride.`Editor`.`EditProxyGroup`

            val `UnnamedProxyNode`
                get() = MLangOverride.`Editor`.`UnnamedProxyNode`

            val `UnnamedProxyGroup`
                get() = MLangOverride.`Editor`.`UnnamedProxyGroup`

            val `ProxyNode`
                get() = MLangOverride.`Editor`.`ProxyNode`

            val `ProxyGroup`
                get() = MLangOverride.`Editor`.`ProxyGroup`

            val `RuleEdit`
                get() = MLangOverride.`Editor`.`RuleEdit`

            val `SubRuleTarget`
                get() = MLangOverride.`Editor`.`SubRuleTarget`

            val `ProxyGroupTarget`
                get() = MLangOverride.`Editor`.`ProxyGroupTarget`

            val `RuleTypeEmpty`
                get() = MLangOverride.`Editor`.`RuleTypeEmpty`

            val `PayloadEmpty`
                get() = MLangOverride.`Editor`.`PayloadEmpty`

            val `TargetEmpty`
                get() = MLangOverride.`Editor`.`TargetEmpty`

            val `MatchResult`
                get() = MLangOverride.`Editor`.`MatchResult`

            val `SelectMatchResult`
                get() = MLangOverride.`Editor`.`SelectMatchResult`

            val `CustomMatchResult`
                get() = MLangOverride.`Editor`.`CustomMatchResult`

            val `SelectSubRuleTarget`
                get() = MLangOverride.`Editor`.`SelectSubRuleTarget`

            val `SelectProxyGroupTarget`
                get() = MLangOverride.`Editor`.`SelectProxyGroupTarget`

            val `CustomSubRuleTarget`
                get() = MLangOverride.`Editor`.`CustomSubRuleTarget`

            val `CustomProxyGroupTarget`
                get() = MLangOverride.`Editor`.`CustomProxyGroupTarget`

            val `SelectRuleProvider`
                get() = MLangOverride.`Editor`.`SelectRuleProvider`

            val `RuleBody`
                get() = MLangOverride.`Editor`.`RuleBody`

            val `RuleType`
                get() = MLangOverride.`Editor`.`RuleType`

            val `Payload`
                get() = MLangOverride.`Editor`.`Payload`

            val `AdditionalParams`
                get() = MLangOverride.`Editor`.`AdditionalParams`

            val `BasicConnection`
                get() = MLangOverride.`Editor`.`BasicConnection`

            val `NetworkAndRoute`
                get() = MLangOverride.`Editor`.`NetworkAndRoute`

            val `PortEmptyHint`
                get() = MLangOverride.`Editor`.`PortEmptyHint`

            val `TypeEmpty`
                get() = MLangOverride.`Editor`.`TypeEmpty`

            val `MemberSource`
                get() = MLangOverride.`Editor`.`MemberSource`

            val `HealthCheckAndFilter`
                get() = MLangOverride.`Editor`.`HealthCheckAndFilter`

            val `SelectProxyGroupMember`
                get() = MLangOverride.`Editor`.`SelectProxyGroupMember`

            val `CustomMember`
                get() = MLangOverride.`Editor`.`CustomMember`

            val `SaveProxyNode`
                get() = MLangOverride.`Editor`.`SaveProxyNode`

            val `SaveProxyGroup`
                get() = MLangOverride.`Editor`.`SaveProxyGroup`

            val `SaveRule`
                get() = MLangOverride.`Editor`.`SaveRule`

            val `RuleProviderInputHint`
                get() = MLangOverride.`Editor`.`RuleProviderInputHint`

            val `LogicalRuleHint`
                get() = MLangOverride.`Editor`.`LogicalRuleHint`

            val `OtherExtraParams`
                get() = MLangOverride.`Editor`.`OtherExtraParams`

            val `ExtraParamsHint`
                get() = MLangOverride.`Editor`.`ExtraParamsHint`

            val `Mode`
                get() = MLangOverride.`Editor`.`Mode`

            val `ClearDialog`
                get() = MLangOverride.`Editor`.`ClearDialog`
        }

        val `Draft`
            get() = MLangOverride.`Draft`

        val `Form`
            get() = MLangOverride.`Form`

        val `Rule`
            get() = MLangOverride.`Rule`

        val `Save`
            get() = MLangOverride.`Save`

        val `Dns`
            get() = MLangOverride.`Dns`

        val `General`
            get() = MLangOverride.`General`

        val `Label`
            get() = MLangOverride.`Label`
    }

    object `ProfilesPage` {
        val `Title`
            get() = MLangProfilesPage.`Title`

        val `Action`
            get() = MLangProfilesPage.`Action`

        val `Empty`
            get() = MLangProfilesPage.`Empty`

        val `Sheet`
            get() = MLangProfilesPage.`Sheet`

        val `Type`
            get() = MLangProfilesPage.`Type`

        val `Input`
            get() = MLangProfilesPage.`Input`

        val `QrScanner`
            get() = MLangProfilesPage.`QrScanner`

        val `Message`
            get() = MLangProfilesPage.`Message`

        val `Validation`
            get() = MLangProfilesPage.`Validation`

        val `Progress`
            get() = MLangProfilesPage.`Progress`

        val `Button`
            get() = MLangProfilesPage.`Button`

        val `DeleteDialog`
            get() = MLangProfilesPage.`DeleteDialog`

        val `EditDialog`
            get() = MLangProfilesPage.`EditDialog`

        object `LinkSettings` {
            val `Title`
                get() = MLangProfilesPage.`LinkSettings`.`Title`

            val `OpenMode`
                get() = MLangProfilesPage.`LinkSettings`.`OpenMode`

            val `OpenModeInApp`
                get() = MLangProfilesPage.`LinkSettings`.`OpenModeInApp`

            val `OpenModeExternal`
                get() = MLangProfilesPage.`LinkSettings`.`OpenModeExternal`

            val `DefaultLink`
                get() = MLangProfilesPage.`LinkSettings`.`DefaultLink`

            val `DefaultLinkSummary`
                get() = MLangProfilesPage.`LinkSettings`.`DefaultLinkSummary`

            val `AddLink`
                get() = MLangProfilesPage.`LinkSettings`.`AddLink`

            val `EditLink`
                get() = MLangProfilesPage.`LinkSettings`.`EditLink`

            val `Name`
                get() = MLangProfilesPage.`LinkSettings`.`Name`

            val `Url`
                get() = MLangProfilesPage.`LinkSettings`.`Url`

            val `Close`
                get() = MLangProfilesPage.`LinkSettings`.`Close`

            val `Validation`
                get() = MLangProfilesPage.`LinkSettings`.`Validation`
        }

        val `ShareDialog`
            get() = MLangProfilesPage.`ShareDialog`

        val `Misc`
            get() = MLangProfilesPage.`Misc`

        val `SettingsDialog`
            get() = MLangProfilesPage.`SettingsDialog`
    }

    object `ProfilesVM` {
        val `Message`
            get() = MLangProfilesVM.`Message`

        val `Progress`
            get() = MLangProfilesVM.`Progress`

        val `Error`
            get() = MLangProfilesVM.`Error`
    }

    object `Providers` {
        val `Title`
            get() = MLangProviders.`Title`

        val `Action`
            get() = MLangProviders.`Action`

        val `Empty`
            get() = MLangProviders.`Empty`

        val `Type`
            get() = MLangProviders.`Type`

        val `Transport`
            get() = MLangProviders.`Transport`

        val `Summary`
            get() = MLangProviders.`Summary`

        val `Message`
            get() = MLangProviders.`Message`
    }

    object `Proxy` {
        val `Title`
            get() = MLangProxy.`Title`

        val `Mode`
            get() = MLangProxy.`Mode`

        val `Action`
            get() = MLangProxy.`Action`

        val `Empty`
            get() = MLangProxy.`Empty`

        val `Testing`
            get() = MLangProxy.`Testing`

        val `Selection`
            get() = MLangProxy.`Selection`

        val `SortMode`
            get() = MLangProxy.`SortMode`

        val `DisplayMode`
            get() = MLangProxy.`DisplayMode`

        val `GroupStyle`
            get() = MLangProxy.`GroupStyle`
    }

    object `Service` {
        val `Notification`
            get() = MLangService.`Notification`

        val `Tile`
            get() = MLangService.`Tile`

        val `AutoRestart`
            get() = MLangService.`AutoRestart`

        val `LogRecord`
            get() = MLangService.`LogRecord`
    }

    object `Settings` {
        val `Title`
            get() = MLangSettings.`Title`

        val `Section`
            get() = MLangSettings.`Section`

        val `UiSettings`
            get() = MLangSettings.`UiSettings`

        val `More`
            get() = MLangSettings.`More`

        val `Error`
            get() = MLangSettings.`Error`
    }

    object `TrafficStatistics` {
        val `Title`
            get() = MLangTrafficStatistics.`Title`

        val `EntrySummary`
            get() = MLangTrafficStatistics.`EntrySummary`

        val `OverviewTitle`
            get() = MLangTrafficStatistics.`OverviewTitle`

        val `Detail`
            get() = MLangTrafficStatistics.`Detail`

        val `TargetSites`
            get() = MLangTrafficStatistics.`TargetSites`

        val `RecentRequests`
            get() = MLangTrafficStatistics.`RecentRequests`

        val `Status`
            get() = MLangTrafficStatistics.`Status`

        val `RelativeTime`
            get() = MLangTrafficStatistics.`RelativeTime`

        val `TimeRange`
            get() = MLangTrafficStatistics.`TimeRange`

        val `Summary`
            get() = MLangTrafficStatistics.`Summary`

        val `Compare`
            get() = MLangTrafficStatistics.`Compare`

        val `Chart`
            get() = MLangTrafficStatistics.`Chart`
    }

    object `Util` {
        val `Error`
            get() = MLangUtil.`Error`
    }
}
