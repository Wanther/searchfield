tasks.register<Delete>("clean") {
    setDelete(rootProject.layout.buildDirectory)
}