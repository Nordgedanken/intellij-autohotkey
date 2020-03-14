plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    java
}

group = "de.nordgedanken"
version = "0.1.0"

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
}

val intellijPublishToken: String by project
tasks.publishPlugin {
    token(intellijPublishToken)
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.3.3"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      <h2>1.0-SNAPSHOT</h2>
        <h3>Added</h3>
        <ul>
            <li>Initial Release</li>
            <li>Added most basic features</li>
        </ul>""")
}
