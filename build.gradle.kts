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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("org.hibernate:hibernate-validator:8.0.0.Final")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.register<GradleBuild>("run") {
    description = "Runs the application. Note that a PostgreSQL database is required for the application to work. See README.md for instructions."
    group = "execution"

    tasks = listOf("clean", "assemble")

    doLast {
        exec {
            val projectDirectory = layout.projectDirectory
            val buildDirectory = projectDirectory.file("build/libs").asFile

            val collection = projectDirectory.files({ buildDirectory.listFiles() })
            val path = collection.find { file -> file.name.endsWith(".jar") }
                    ?: throw GradleException("Could not find .jar file to run.")

            commandLine("java", "-jar", path)
        }
    }
}

tasks.register<GradleBuild>("dockerRun") {
    description = "Runs the application and database in Docker containers."
    group = "execution"

    tasks = listOf("clean", "assemble")

    doLast {
        exec {
            commandLine("systemctl", "--user", "start", "docker-desktop")
            commandLine("docker", "compose", "up", "--build")
        }
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"

    mustRunAfter("test")

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    outputs.upToDateWhen { false }
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

