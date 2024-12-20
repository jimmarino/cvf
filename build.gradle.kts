/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.eclipse.dataspacetck.gradle.tasks.GenerateTestPlanTask

plugins {
    `java-library`
    `maven-publish`
    signing
    checkstyle
    jacoco
    `jacoco-report-aggregation`
    alias(libs.plugins.docker)
    alias(libs.plugins.nexuspublishing)
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }

    apply(plugin = "java-library")
    apply(plugin = "checkstyle")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")


    tasks.test {
        useJUnitPlatform()
        systemProperty("dataspacetck.launcher", "org.eclipse.dataspacetck.dsp.system.DspSystemLauncher")
    }

    tasks.jar {
        metaInf {
            from("${rootProject.projectDir.path}/LICENSE")
            from("${rootProject.projectDir.path}/DEPENDENCIES")
            from("${rootProject.projectDir.path}/NOTICE.md")
        }
    }


    dependencies {
        implementation(rootProject.libs.json.api)
        implementation(rootProject.libs.jackson.databind)
        implementation(rootProject.libs.jackson.jsonp)
        implementation(rootProject.libs.titanium)
        implementation(rootProject.libs.okhttp)
        implementation(rootProject.libs.junit.jupiter)
        implementation(rootProject.libs.junit.platform.engine)
        implementation(rootProject.libs.mockito.core)
        implementation(rootProject.libs.awaitility)
        testImplementation(rootProject.libs.assertj)
    }

    if (!project.hasProperty("skip.signing")) {
        apply(plugin = "signing")
        publishing {
            signing {
                useGpgCmd()
                sign(publishing.publications)
            }
        }
    }


}

subprojects {

    afterEvaluate {

        // register the annotation processor task to all modules that declare a dependency onto the annotation processor,
        // except the annotation processor module itself
        val config = project.configurations.findByName("annotationProcessor")
        if (config != null && config.dependencies.any { it.name == "tools" }) {
            tasks.register<GenerateTestPlanTask>("genTestPlan") {
                val force : String = project.properties.getOrDefault("cvf.conversion.force", "true") as String
                forceConversion = force.toBoolean()
                imageFormat = project.properties.getOrDefault("cvf.conversion.format", "svg") as String // or "png"
                // uncomment to configure the output directory
                //outputDirectory = "/path/to/directory"
            }
        }

        // the "dockerize" task is added to all projects that contain a src/main/docker/Dockerfile
        if (file("${project.projectDir}/src/main/docker/Dockerfile").exists()) {

            // this task copies some legal docs into the build folder, so we can easily copy them into the docker images
            val copyLegalDocs = tasks.create("copyLegalDocs", Copy::class) {

                into("${project.layout.buildDirectory.asFile.get()}")
                into("legal") {
                    from("${project.rootProject.projectDir}/SECURITY.md")
                    from("${project.rootProject.projectDir}/NOTICE.md")
                    from("${project.rootProject.projectDir}/DEPENDENCIES")
                    from("${project.rootProject.projectDir}/LICENSE")
                    from("${projectDir}/notice.md")

                }
            }

            //actually apply the plugin to the (sub-)project
            apply(plugin = "com.bmuschko.docker-remote-api")
            // configure the "dockerize" task
            val dockerTask: DockerBuildImage = tasks.create("dockerize", DockerBuildImage::class) {
                val dockerContextDir = project.projectDir
                dockerFile.set(file("$dockerContextDir/src/main/docker/Dockerfile"))
                images.add("${project.name}:${project.version}")
                images.add("${project.name}:latest")
                // specify platform with the -Dplatform flag:
                if (System.getProperty("platform") != null) {
                    platform.set(System.getProperty("platform"))
                }
                buildArgs.put("JAR", "build/libs/${project.name}.jar")
                buildArgs.put("ADDITIONAL_FILES", "build/legal/*")
                inputDir.set(file(dockerContextDir))
            }
            // make sure "dockerize" always runs after "copyLegalDocs"
            dockerTask.dependsOn(copyLegalDocs)
        }

        publishing {
            publications.forEach { i ->
                val mp = (i as MavenPublication)
                mp.pom {
                    name.set(project.name)
                    description.set("Compliance Verification Toolkit")
                    url.set("https://projects.eclipse.org/projects/technology.dataspacetck")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                        developers {
                            developer {
                                id.set("JimMarino")
                                name.set("Jim Marino")
                                email.set("jmarino@metaformsystems.com")
                            }
                            developer {
                                id.set("PaulLatzelsperger")
                                name.set("Paul Latzelsperger")
                                email.set("paul.latzelsperger@beardyinc.com")
                            }
                            developer {
                                id.set("EnricoRisa")
                                name.set("Enrico Risa")
                                email.set("enrico.risa@gmail.com")
                            }
                        }
                        scm {
                            connection.set("scm:git:git@github.com:eclipse-dataspacetck/cvf.git")
                            url.set("https://github.com/eclipse-dataspacetck/cvf.git")
                        }
                    }
                }
            }
        }

    }

    publishing {
        publications {
            create<MavenPublication>(project.name) {
                artifactId = project.name
                from(components["java"])
            }
        }
    }
}


// needed for running the dash tool
tasks.register("allDependencies", DependencyReportTask::class)

// disallow any errors
checkstyle {
    maxErrors = 0
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/"))
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("OSSRH_USERNAME") ?: return@sonatype)
            password.set(System.getenv("OSSRH_PASSWORD") ?: return@sonatype)
        }
    }
}


