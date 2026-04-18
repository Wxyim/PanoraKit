/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import javax.xml.parsers.DocumentBuilderFactory
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.aboutlibraries.android) apply false
    alias(libs.plugins.spotless)
}

apply(from = "gradle/ui-contract-validation.gradle.kts")

apply(from = "gradle/modernization-baseline.gradle.kts")

extensions.configure(SpotlessExtension::class.java) {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "**/generated/**")
        ktfmt("0.52").kotlinlangStyle()
    }

    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**")
        ktfmt("0.52").kotlinlangStyle()
    }
}

data class GithubOssDependencyRule(val reason: String, val markers: List<String>)

data class GithubOssDependencyFinding(
    val file: File,
    val lineNumber: Int,
    val lineText: String,
    val reason: String,
)

@DisableCachingByDefault(
    because = "Scans repository build metadata for GitHub OSS policy violations."
)
abstract class CheckGithubOssLicensePolicyTask : DefaultTask() {
    @get:Internal abstract val repositoryRoot: DirectoryProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val policyInputs: ConfigurableFileCollection

    @TaskAction
    fun runCheck() {
        val rules =
            listOf(
                GithubOssDependencyRule(
                    reason =
                        "F2DLPRL-covered FYTxt/FVV components must not be shipped in GitHub OSS release binaries.",
                    markers = listOf("dev.oom-wg.purejoy.fyl.fytxt", "libs.fytxt.common.android"),
                ),
                GithubOssDependencyRule(
                    reason =
                        "Google ML Kit / Play Services terms are blocked for the GitHub OSS release profile.",
                    markers =
                        listOf(
                            "libs.mlkit.barcode.scanning",
                            "com.google.mlkit:",
                            "play-services-mlkit-barcode-scanning",
                        ),
                ),
            )

        val repoRootDir = repositoryRoot.asFile.get()
        val findings = mutableListOf<GithubOssDependencyFinding>()

        policyInputs.files
            .sortedBy { it.relativeTo(repoRootDir).invariantSeparatorsPath }
            .forEach { file ->
                file.readLines().forEachIndexed { index, rawLine ->
                    val line = rawLine.trim()
                    if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) {
                        return@forEachIndexed
                    }

                    rules.forEach { rule ->
                        if (rule.markers.any(line::contains)) {
                            findings +=
                                GithubOssDependencyFinding(
                                    file = file,
                                    lineNumber = index + 1,
                                    lineText = line,
                                    reason = rule.reason,
                                )
                        }
                    }
                }
            }

        if (findings.isNotEmpty()) {
            val formattedFindings =
                findings.joinToString("\n") { finding ->
                    val relativePath = finding.file.relativeTo(repoRootDir).invariantSeparatorsPath
                    "- $relativePath:${finding.lineNumber}: ${finding.lineText}\n  ${finding.reason}"
                }

            throw GradleException(
                buildString {
                    appendLine("GitHub OSS release policy check failed.")
                    appendLine("Blocked dependencies are still wired into the public release path:")
                    appendLine(formattedFindings)
                    appendLine()
                    appendLine(
                        "Remove or isolate these dependencies before publishing GitHub release binaries."
                    )
                    appendLine("Policy reference: docs/LICENSING.md")
                }
            )
        }
    }
}

@DisableCachingByDefault(because = "Small XML baseline budget check; caching is unnecessary.")
abstract class CheckLintBaselineBudgetTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val baselineFile: RegularFileProperty

    @get:Input abstract val maxIssues: Property<Int>

    @TaskAction
    fun runCheck() {
        val baseline = baselineFile.asFile.get()
        val budget = maxIssues.get()

        if (!baseline.exists()) {
            logger.lifecycle("Lint baseline budget: 0/$budget issues (baseline file not found).")
            return
        }

        val factory = DocumentBuilderFactory.newInstance()
        val document = factory.newDocumentBuilder().parse(baseline)
        val issues = document.getElementsByTagName("issue")
        val issueCount = issues.length

        if (issueCount > budget) {
            val countsById =
                buildMap<String, Int> {
                        repeat(issueCount) { index ->
                            val issue = issues.item(index)
                            val id = issue.attributes?.getNamedItem("id")?.nodeValue ?: "unknown"
                            put(id, (get(id) ?: 0) + 1)
                        }
                    }
                    .entries
                    .sortedWith(
                        compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key }
                    )
                    .joinToString(separator = "\n") { (id, count) -> "- $id: $count" }

            throw GradleException(
                buildString {
                    appendLine("Lint baseline budget exceeded.")
                    appendLine("Current baseline issues: $issueCount")
                    appendLine("Allowed baseline issues: $budget")
                    appendLine()
                    appendLine("Reduce app/lint-baseline.xml or explicitly lower/raise")
                    appendLine("lint.baseline.maxIssues with review.")
                    if (countsById.isNotBlank()) {
                        appendLine()
                        appendLine("Current issue counts:")
                        appendLine(countsById)
                    }
                }
            )
        }

        logger.lifecycle("Lint baseline budget: $issueCount/$budget issues.")
    }
}

tasks.register<CheckGithubOssLicensePolicyTask>("checkGithubOssLicensePolicy") {
    group = "verification"
    description =
        "Fails when the public GitHub OSS release path still references blocked dependencies."

    repositoryRoot.set(layout.projectDirectory)
    policyInputs.from(
        layout.projectDirectory.file("gradle/libs.versions.toml"),
        fileTree(layout.projectDirectory.asFile) {
            include("**/build.gradle.kts")
            exclude("build.gradle.kts", "**/build/**")
        },
    )
}

tasks.register<CheckLintBaselineBudgetTask>("checkLintBaselineBudget") {
    group = "verification"
    description = "Fails when app/lint-baseline.xml grows beyond the approved burn-down budget."

    baselineFile.set(layout.projectDirectory.file("app/lint-baseline.xml"))
    maxIssues.set(providers.gradleProperty("lint.baseline.maxIssues").map { it.toInt() })
}

tasks.named("check") { dependsOn("checkLintBaselineBudget") }

subprojects {
    val moduleOutputPath = path.removePrefix(":").replace(':', '/')
    layout.buildDirectory.set(rootProject.layout.buildDirectory.dir(moduleOutputPath))

    dependencyLocking { lockAllConfigurations() }

    val androidCompileSdk = providers.gradleProperty("android.compileSdk").get().toInt()
    val androidMinSdk = providers.gradleProperty("android.minSdk").get().toInt()
    val androidTargetSdk = providers.gradleProperty("android.targetSdk").get().toInt()
    val javaVer =
        providers.gradleProperty("android.jvm").orNull
            ?: providers.gradleProperty("project.jvm").orNull
            ?: "17"
    val ndkVersionValue = providers.gradleProperty("android.ndkVersion").orNull.orEmpty()

    pluginManager.withPlugin("com.android.library") {
        extensions.configure(LibraryExtension::class.java) {
            compileSdk = androidCompileSdk

            if (ndkVersionValue.isNotBlank()) {
                ndkVersion = ndkVersionValue
            }

            defaultConfig { minSdk = androidMinSdk }

            compileOptions {
                sourceCompatibility = JavaVersion.toVersion(javaVer)
                targetCompatibility = JavaVersion.toVersion(javaVer)
            }

            packaging {
                resources {
                    excludes +=
                        setOf(
                            "/META-INF/{AL2.0,LGPL2.1}",
                            "/META-INF/*.kotlin_module",
                            "DebugProbesKt.bin",
                        )
                }
                jniLibs { useLegacyPackaging = true }
            }
        }
    }

    pluginManager.withPlugin("com.android.application") {
        extensions.configure(ApplicationExtension::class.java) {
            compileSdk = androidCompileSdk

            if (ndkVersionValue.isNotBlank()) {
                ndkVersion = ndkVersionValue
            }

            defaultConfig {
                minSdk = androidMinSdk
                targetSdk = androidTargetSdk
            }

            compileOptions {
                sourceCompatibility = JavaVersion.toVersion(javaVer)
                targetCompatibility = JavaVersion.toVersion(javaVer)
            }
        }
    }

    tasks.withType(KotlinCompile::class.java).configureEach {
        val persistentDir =
            rootProject.findProperty("kotlin.project.persistent.dir")?.toString()?.trim().orEmpty()
        if (persistentDir.isNotEmpty()) {
            val sessionsDir = rootProject.file(persistentDir).resolve("sessions")
            doFirst { sessionsDir.mkdirs() }
        }
    }
}
