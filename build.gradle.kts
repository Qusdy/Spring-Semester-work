import java.util.*

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
    id("org.openapi.generator") version "7.10.0"
    id("jacoco")
}

group = "ru.kpfu.itis.snetkov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val postgresVersion: String by project
val springSecurityVersion: String by project
val lombokVersion: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.25")
    implementation("org.postgresql:postgresql:${postgresVersion}")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("javax.mail:javax.mail-api:1.6.2")
//    implementation("org.springframework.security:spring-security-taglibs:${springSecurityVersion}")
    implementation("org.liquibase:liquibase-core:4.33.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
}

tasks.test {
    useJUnitPlatform()
}

val openApiSpec = "$projectDir/src/main/resources/api.yaml"
val openApiGeneratedDir: String = layout.buildDirectory.dir("generated").get().asFile.absolutePath

openApiGenerate {
    inputSpec.set(openApiSpec)
    outputDir.set(openApiGeneratedDir)
    generatorName.set("spring")
    modelPackage.set("ru.kpfu.itis.snetkov.api.generated.dto")
    apiPackage.set("ru.kpfu.itis.snetkov.api.generated.api")

    configOptions.set(
        mapOf(
            "useJakartaEe" to "true",
            "useSpringBoot3" to "true",
            "library" to "spring-boot",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "useBeanValidation" to "true",
            "useTags" to "true",
            "dateLibrary" to "java8",
            "openApiNullable" to "false",
            "documentationProvider" to "none",
            "useResponseEntity" to "true",
        )
    )
    additionalProperties.set(
        mapOf(
            "generateApiTests" to "false",
            "generateModelTests" to "false",
            "generateApiDocumentation" to "false",
            "generateModelDocumentation" to "false"
        )
    )
}

sourceSets {
    getByName("main") {
        java {
            srcDir(layout.buildDirectory.dir("generated/src/main/java"))
        }
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

val props = Properties()
props.load(file("src/main/resources/db/liquibase.properties").inputStream())

liquibase {
    activities.register("main") {
        arguments=mapOf(
            "changeLogFile" to props.get("change-log-file").toString(),
            "url" to props.get("url"),
            "username" to props.get("username"),
            "password" to props.get("password"),
            "driver" to props.get("driver-class-name")
        )
    }
}