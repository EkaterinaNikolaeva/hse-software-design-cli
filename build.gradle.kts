plugins {
    id("java")
    application
}

group = "cli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jetbrains:annotations:24.0.1")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
    }
    test {
        java {
            srcDir("src/test/java")
        }
    }
}

application {
    mainClass.set("cli.Main")
}

tasks.getByName("run", JavaExec::class) {
    standardInput = System.`in`
}
