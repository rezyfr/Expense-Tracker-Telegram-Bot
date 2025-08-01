plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Telegram Bot API
    implementation("com.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")

    // HTTP client for Notion API
    implementation("com.squareup.okhttp3:okhttp:3.8.0")
    implementation("com.squareup.okio:okio:3.5.0")

    // JSON parser
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Configuration
    implementation("com.sksamuel.hoplite:hoplite-core:2.8.0.RC3")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.8.0.RC3")

    // Date/Time utilities
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    implementation("io.ktor:ktor-server-core-jvm:2.3.4")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.4")
    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClass.set("com.expensetracker.MainKt")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.test {
    useJUnit()
} 
