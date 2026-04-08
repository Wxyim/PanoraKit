val checkModernizationBaseline =
    tasks.register<ModernizationBaselineTask>("checkModernizationBaseline") {
        group = "verification"
        description =
            "Validates modernization baseline guards for lifecycle-aware Compose and thin Application startup orchestration."

        sourceRoots.from(
            layout.projectDirectory.dir("app/src"),
            layout.projectDirectory.dir("modules/feature"),
        )
        moduleBuildFiles.from(
            layout.projectDirectory.file("modules/feature/proxy/build.gradle.kts"),
            layout.projectDirectory.file("modules/feature/override/build.gradle.kts"),
        )
        requiredDependencyTokens.putAll(
            mapOf(
                "modules/feature/proxy/build.gradle.kts" to "libs.lifecycle.runtime.compose",
                "modules/feature/override/build.gradle.kts" to "libs.lifecycle.runtime.compose",
            )
        )
        repoRootDir.set(layout.projectDirectory)
        reportFile.set(layout.buildDirectory.file("reports/modernization-baseline/report.txt"))
    }

tasks.named("check") { dependsOn(checkModernizationBaseline) }