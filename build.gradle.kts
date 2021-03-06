import com.techshroom.inciseblue.commonLib
import net.minecrell.gradle.licenser.LicenseExtension

plugins {
    id("com.techshroom.incise-blue") version "0.3.14"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("net.researchgate.release") version "2.8.0" apply false
}

subprojects {
    version = rootProject.version
    apply(plugin = "com.techshroom.incise-blue")
    apply(plugin = "java-library")

    inciseBlue {
        util {
            javaVersion = JavaVersion.VERSION_1_8
        }
        ide()
        license()
        lwjgl {
            lwjglVersion = "3.1.3"
        }
    }

    configure<LicenseExtension> {
        exclude("**/Sync.java")
        exclude("**/Color.java")
    }

    dependencies {
        "compileOnly"("com.techshroom", "jsr305-plus", "0.0.1")

        commonLib("com.google.auto.service", "auto-service", "1.0-rc5") {
            "compileOnly"(lib())
            "annotationProcessor"(lib())
        }

        commonLib("com.google.auto.value", "auto-value", "1.6.5") {
            "compileOnly"(lib("annotations"))
            "annotationProcessor"(lib())
        }

        "testImplementation"("junit", "junit", "4.12")
        "testImplementation"("ch.qos.logback", "logback-classic", "1.2.3")
        "testImplementation"("ch.qos.logback", "logback-core", "1.2.3")
    }
}

listOf("api", "ap", "implementation", "bale-out").map { project(":$it") }.forEach {
    it.apply(plugin = "net.researchgate.release")
}
listOf("api", "ap", "implementation").map { project(":$it") }.forEach {
    it.inciseBlue.maven {
        projectDescription = "UnplannedDescent"
        coords("TechShroom", "UnplannedDescent")
    }
}

project(":api") {
    inciseBlue.lwjgl {
        addDependency("")
        addDependency("stb")
    }
    dependencies {
        "api"("org.slf4j", "slf4j-api", "1.7.25")
        "api"("com.flowpowered", "flow-math", "1.0.3")
        "api"("com.google.guava", "guava", "23.0")
        "implementation"("net.java.dev.jna", "jna", "4.5.0")
        "implementation"("com.github.luben", "zstd-jni", "1.3.2-2")
        commonLib("org.eclipse.collections", "eclipse-collections", "9.0.0") {
            "api"(lib("api"))
            "implementation"(lib())
        }
    }
}

project(":ap") {
    dependencies {
        "implementation"(project(":api"))
        "implementation"("com.google.auto", "auto-common", "0.8")
        "implementation"("com.squareup", "javapoet", "1.9.0")

        "testImplementation"("com.google.testing.compile", "compile-testing", "0.12")
    }
}

project(":implementation") {
    inciseBlue.lwjgl {
        addDependency("")
        addDependency("opengl")
        addDependency("openal")
        addDependency("glfw")
        addDependency("nanovg")
        addDependency("stb")

    }

    dependencies {
        "implementation"(project(":api"))
        "implementation"("com.squareup", "javapoet", "1.9.0")
    }
}
project(":examples") {
    dependencies {
        "implementation"(project(":api"))
        "annotationProcessor"(project(":ap"))
        "compileOnly"(project(":ap"))
        "runtime"(project(":implementation"))
        "implementation"("ch.qos.logback", "logback-classic", "1.2.3")
        "implementation"("ch.qos.logback", "logback-core", "1.2.3")
    }
    apply(plugin = "com.github.johnrengelman.shadow")
    tasks.named<Jar>("jar") {
        manifest {
            attributes("Main-Class" to "com.techshroom.unplanned.examples.ExamplePicker")
        }
    }
}
