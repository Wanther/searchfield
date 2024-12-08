plugins {
    `java-library`
    antlr
}

group = "org.kotlina"
version = "1.0-SNAPSHOT"

tasks.generateGrammarSource {
    outputDirectory = layout.buildDirectory.dir("generated-src/antlr/main/org/kotlina/searchfield").get().asFile
    arguments = arguments + listOf(
        "-package", "org.kotlina.searchfield",
        "-no-listener"
    )
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    antlr("org.antlr:antlr4:4.13.2")
}