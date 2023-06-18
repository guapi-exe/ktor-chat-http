val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.1"
}

group = "com.guapi.chat"
version = "0.0.1"
application {
    mainClass.set("com.guapi.chat.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-json:$ktor_version")
    implementation("org.w3c:dom:2.3.0-jaxb-1.0.6")
    implementation("cn.zhxu:okhttps:4.0.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js:1.8.20-RC")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktor_version")
    implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.129-kotlin-1.4.20")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-serialization-jackson:2.3.1")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    implementation("org.w3c:dom:2.3.0-jaxb-1.0.6")
    implementation("net.bytebuddy:byte-buddy:1.14.2")
    implementation("junit:junit:4.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.7.0-Beta")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}