plugins {
    id("java")
}

group = "at.ac.fhcampuswien.richAF"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.24.1")
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.17.0")
}

tasks.test {
    useJUnitPlatform()
}