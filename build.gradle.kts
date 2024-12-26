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
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("org.xerial:sqlite-jdbc:3.44.0.0");
    implementation("org.json:json:20210307");
    implementation("com.j256.ormlite:ormlite-core:5.1");
    implementation("com.j256.ormlite:ormlite-jdbc:5.1");
}

tasks.test {
    useJUnitPlatform()
}