plugins {
    `java-library`
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("org.antlr:antlr4:4.13.2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation(project(":search-field-core"))

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.0")
    testRuntimeOnly("com.h2database:h2:2.3.232")
}