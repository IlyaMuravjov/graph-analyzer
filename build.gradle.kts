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

val `junit-jupiter-version`: String by project
val `testfx-version`: String by project
val `monocle-version`: String by project
val `mockk-version`: String by project

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

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$`junit-jupiter-version`")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$`junit-jupiter-version`")
    testImplementation("org.testfx:testfx-core:$`testfx-version`")
    testImplementation("org.testfx:testfx-junit5:$`testfx-version`")
    testImplementation("org.testfx:openjfx-monocle:$`monocle-version`")
    testImplementation("io.mockk:mockk:$`mockk-version`")
}

application {
    mainClass.set("spbu_coding.graph_analyzer.GraphAnalyzerAppKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    if (project.hasProperty("headless") && project.property("headless").toString().toBoolean()) {
        systemProperty("testfx.robot", "glass")
        systemProperty("testfx.headless", "true")
        systemProperty("prism.order", "sw")
        systemProperty("prism.text", "t2k")
    }
}
