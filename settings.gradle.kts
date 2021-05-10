rootProject.name = "graph-analyzer"

pluginManagement {
    val `kotlin-version`: String by settings

    plugins {
        kotlin("jvm") version `kotlin-version`
    }
}
