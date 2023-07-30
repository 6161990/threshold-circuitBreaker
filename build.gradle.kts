import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

plugins {
  kotlin("jvm") version "1.7.20"
  kotlin("kapt")
  kotlin("plugin.spring")

  id("org.springframework.boot")
  id("io.spring.dependency-management")
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
val jacksonVersion: String by rootProject
val springCloudVersion: String by rootProject

dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
  implementation(enforcedPlatform("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"))
  implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
  implementation("io.github.openfeign:feign-okhttp")
  implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.valid4j:valid4j:0.5.0")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-actuator")
  testImplementation("org.springframework.boot:spring-boot-starter-web")
  testRuntimeOnly("io.micrometer:micrometer-registry-prometheus")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("io.rest-assured:rest-assured")
  testImplementation("org.testcontainers:mockserver")
  testImplementation("org.mock-server:mockserver-netty:5.15.0")
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
