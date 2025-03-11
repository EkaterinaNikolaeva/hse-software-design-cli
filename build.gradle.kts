plugins {
    id("java")
}

group = "cli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    test {
        java {
            srcDir("src/test/java")
        }
    }
}