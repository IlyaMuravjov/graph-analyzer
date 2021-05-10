import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

repositories {
    jcenter()
}

val `tornadofx-version`: String by project
val `controlsfx-version`: String by project
val `exposed-version`: String by project
val `sqlite-jdbc-version`: String by project
val `slf4j-version`: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("no.tornado:tornadofx:$`tornadofx-version`")
    implementation("org.controlsfx:controlsfx:$`controlsfx-version`")
    implementation("org.jetbrains.exposed:exposed-core:$`exposed-version`")
    implementation("org.jetbrains.exposed:exposed-dao:$`exposed-version`")
    implementation("org.jetbrains.exposed:exposed-jdbc:$`exposed-version`")
    implementation("org.xerial:sqlite-jdbc:$`sqlite-jdbc-version`")
    implementation("org.slf4j:slf4j-api:$`slf4j-version`")
    implementation("org.slf4j:slf4j-simple:$`slf4j-version`")
}

application {
    mainClass.set("spbu_coding.graph_analyzer.GraphAnalyzerAppKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
