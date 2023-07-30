import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("kapt")

    id("org.jlleitschuh.gradle.ktlint")
}

group = "com.yoon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotestVersion: String by rootProject
val mockkVersion: String by rootProject
val kotestExtensionsSpringVersion: String by rootProject
val mockitoVersion: String by rootProject
val kotlinCoroutinesVersion: String by rootProject
val mapstructVersion: String by rootProject
val jacksonVersion: String by rootProject

dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  implementation("org.mapstruct:mapstruct:$mapstructVersion")
  implementation("org.valid4j:valid4j:0.5.0")
  kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
  kaptTest("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}
