import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.asciidoctor.jvm.convert")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "${property("projectGroup")}"
version = "${property("applicationVersion")}"
java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.security:spring-security-crypto")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // Query Builder
    implementation("com.querydsl:querydsl-jpa:${property("queryDslVersion")}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${property("queryDslVersion")}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Cloud Infra
    implementation("io.awspring.cloud:spring-cloud-aws-starter:${property("awspringVersion")}")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3:${property("awspringVersion")}")

    // Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${property("jwtTokenVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jwtTokenVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jwtTokenVersion")}")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("swaggerVersion")}")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Guava
    implementation("com.google.guava:guava:${property("guavaVersion")}-jre")

    // Slack API
    implementation("com.slack.api:slack-api-client:${property("slackApiVersion")}")

    // p6spy
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${property("p6spyVersion")}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Kotest & Mockk
    testImplementation("io.kotest:kotest-runner-junit5:${property("kotestVersion")}")
    testImplementation("io.kotest:kotest-assertions-core:${property("kotestVersion")}")
    testImplementation("io.kotest:kotest-extensions-spring:${property("kotestVersion")}")
    testImplementation("io.mockk:mockk:${property("mockkVersion")}")

    // Spring REST Docs (With MockMvc)
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // RestAssured (E2E Test)
    testImplementation("io.rest-assured:rest-assured:${property("restAssuredVersion")}")

    // TestContainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")

    // TestContainers + RDB(MySQL)
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.flywaydb.flyway-test-extensions:flyway-spring-test:${property("flywayTestExtensionVersion")}")

    // TestContainers + LocalStack
    testImplementation("org.testcontainers:localstack:${property("localStackVersion")}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "${project.property("javaVersion")}"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// build REST Docs
val asciidoctorExt: Configuration by configurations.creating
dependencies {
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

val snippetsDir: File by extra { file("build/generated-snippets") }
tasks {
    test {
        outputs.dir(snippetsDir)
    }

    asciidoctor {
        configurations(asciidoctorExt.name)
        dependsOn(test)

        doFirst {
            delete(file("src/main/resources/static/docs"))
        }

        inputs.dir(snippetsDir)

        doLast {
            copy {
                from("build/docs/asciidoc")
                into("src/main/resources/static/docs")
            }
        }
    }

    build {
        dependsOn(asciidoctor)
    }
}

// Copy Submodule
tasks.register<Copy>("copySecret") {
    from("./koddy-secret")
    include("application*.yml")
    into("./src/main/resources")
}

tasks.named("processResources") {
    dependsOn("copySecret")
}

tasks.named<JavaCompile>("compileJava") {
    inputs.files(tasks.named("processResources"))
}

// jar & bootJar
tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<BootJar>("bootJar") {
    archiveBaseName = "Koddy"
    archiveFileName = "Koddy.jar"
}
