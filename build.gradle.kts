plugins {
    id("java")
}

group = "at.ac.fhcampuswien.richAF"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // TESTING
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // COMPTIME
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // RUNTIME
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("org.json:json:20240303")
    implementation("org.xerial:sqlite-jdbc:3.44.0.0");
    implementation("com.j256.ormlite:ormlite-core:5.1");
    implementation("com.j256.ormlite:ormlite-jdbc:5.1");
}

tasks.test {
    useJUnitPlatform()
}