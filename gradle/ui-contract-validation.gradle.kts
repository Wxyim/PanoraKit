val checkUiContracts =
    tasks.register<UiContractValidationTask>("checkUiContracts") {
        group = "verification"
        description =
            "Validates page reachability, capability registry coverage, and UI-to-implementation correspondence."

        registryFile.set(layout.projectDirectory.file("config/ui-capability-registry.txt"))
        sourceRoots.from(
            layout.projectDirectory.dir("app/src"),
            layout.projectDirectory.dir("modules/feature"),
        )
        repoRootDir.set(layout.projectDirectory)
        reportFile.set(layout.buildDirectory.file("reports/ui-contracts/report.txt"))
    }

tasks.named("check") { dependsOn(checkUiContracts) }
