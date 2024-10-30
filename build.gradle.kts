plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "org.lasantha.kotlindemo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //JUnit Jupiter
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}
