plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "at.ac.fhcampuswien.richAF"
version = "1.0-SNAPSHOT"


java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // COMPTIME
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // RUNTIME
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("org.xerial:sqlite-jdbc:3.45.0.0");
    implementation("org.json:json:20210307");
    implementation("com.j256.ormlite:ormlite-core:5.1");
    implementation("com.j256.ormlite:ormlite-jdbc:5.1");
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("at.ac.fhcampuswien.richAF.App")
}

tasks.test {
    useJUnitPlatform()
}