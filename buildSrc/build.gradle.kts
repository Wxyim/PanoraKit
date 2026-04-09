plugins { `kotlin-dsl` }

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies { testImplementation(kotlin("test-junit5")) }

tasks.test { useJUnitPlatform() }
