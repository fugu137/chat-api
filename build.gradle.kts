plugins {
    java
    idea
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "zoidnet.dev"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

sourceSets {
    create("integrationTest") {
        java {
            compileClasspath += main.get().output + test.get().output
            runtimeClasspath += main.get().output + test.get().output
            setSrcDirs(listOf("src/integrationTest/java"))
        }
    }
}

idea {
    module {
        testSources.from(sourceSets["integrationTest"].java.srcDirs)
    }
}

configurations["integrationTestImplementation"].extendsFrom(configurations.testImplementation.get())
configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    outputs.upToDateWhen { false }
    mustRunAfter("test")
}

tasks.check {
    dependsOn(tasks.getByName("integrationTest"))
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging.showStandardStreams = false

    testLogging {
        events("passed", "skipped", "failed")

        afterSuite(
                KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
                    val summary = " Result: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
                    val duration = ", Duration: ${(result.endTime - result.startTime) / 1000}s"
                    val divider = "-".repeat(summary.length + duration.length + 1)

                    if (desc.parent == null) {
                        println()
                        println(divider)
                        println(summary + duration)
                        println(divider)
                    }
                })
        )
    }
}

