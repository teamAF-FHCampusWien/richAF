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
    implementation("org.json:json:20171018")
}

tasks.test {
    useJUnitPlatform()
}