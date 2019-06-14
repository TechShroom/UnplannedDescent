import com.techshroom.inciseblue.InciseBlueExtension

plugins {
    id("com.github.johnrengelman.shadow")
    id("edu.sc.seis.launch4j")
    id("application")
    id("com.techshroom.release-files")
}

application.mainClassName = "com.techshroom.unplanned.baleout.BaleOut"

configure<InciseBlueExtension> {
    ide.configureEclipse {
        addJavaFx = true
    }
}

dependencies {
    "implementation"(project(":api"))
    "implementation"("net.sf.jopt-simple", "jopt-simple", "5.0.4")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(mapOf(
                "Main-Class" to application.mainClassName,
                "Implementation-Version" to project.version as String
        ))
    }
}
