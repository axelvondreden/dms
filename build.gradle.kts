import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.5.0" apply true
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.0" apply true
    id("com.vaadin") version "0.14.6.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    application
}

ext {
    set("vaadinVersion", "14.6.8")
}

noArg {
    annotation("javax.persistence.Entity")
}

group = "com.dude.dms"
version = "0.2.7"

val karibudslVersion = "1.0.6"
val vaadinVersion = "14.6.8"

defaultTasks("clean", "build")

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:$vaadinVersion")
        mavenBom("dev.forkhandles:forkhandles-bom:1.10.0.0")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-RC")

    implementation("com.h2database:h2")
    implementation("org.hibernate:hibernate-core:5.4.10.Final")
    implementation("javax.persistence:javax.persistence-api")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.vaadin:vaadin-spring")
    implementation("com.vaadin:vaadin-spring-boot-starter") {
        // Webjars are only needed when running in Vaadin 13 compatibility mode
        arrayOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
                "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
                "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
                .forEach { exclude(it) }
    }

    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude("org.apache.tomcat")
    }

    implementation("commons-net:commons-net:3.7")
    implementation("commons-logging:commons-logging:1.2")
    implementation("javax.xml.bind:jaxb-api")
    implementation("org.reactivestreams:reactive-streams")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("com.github.mvysny.karibudsl:karibu-dsl:$karibudslVersion")
    implementation("org.apache.pdfbox:pdfbox-tools:3.0.0-RC1")
    implementation("org.bytedeco:tesseract-platform:4.1.1-1.5.5")
    implementation("org.languagetool:languagetool-core:5.3")
    implementation("org.languagetool:language-de:5.3")
    implementation("org.languagetool:language-en:5.3")
    implementation("com.github.vatbub:mslinks:1.0.6.1")
    implementation("dev.forkhandles:parser4k")

    implementation("com.github.appreciated:app-layout-addon:4.0.0")
    implementation("com.github.appreciated:apexcharts:2.0.0.beta11")
    implementation("com.github.appreciated:card:2.0.0")
    implementation("com.github.appreciated:color-picker-field-flow:2.0.0.beta6")
    implementation("dev.mett.vaadin:tooltip:1.7.2")
    implementation("org.vaadin.artur:spring-data-provider:2.1.0")
    implementation("org.vaadin.olli:file-download-wrapper:3.0.1")
    implementation("com.hilerio:ace-widget:1.0.2")
    implementation("net.coobird:thumbnailator:0.4.14")
}


application {
    mainClass.set("com.dude.dms.ApplicationKt")
}

tasks.processResources {
    filesMatching("**/application.properties") {
        expand( project.properties )
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.bootRun {
    jvmArgs("-Xmx1g")
}

tasks.bootJar {
    archiveBaseName.set("dms")
}

tasks.bootDistZip {
    archiveFileName.set("dms.zip")
    archiveBaseName.set("dms")
}

tasks.bootStartScripts {
    applicationName = "dms"
}

val createConfig by tasks.registering {
    val config = file("$buildDir/config")
    outputs.dir(config)
    doLast {
        config.mkdirs()
        File("$rootDir/config/options.default.json").copyTo(File(config, "options.default.json"), overwrite = true)
        File("$rootDir/tessdata/").copyRecursively(File(config, "tessdata/"), overwrite = true)
    }
}

distributions {
    boot {
        contents {
            from(createConfig) {
                into("config")
            }
            distributionBaseName.set("dms")
        }
        distributionBaseName.set("dms")
    }
}

springBoot {
    mainClass.set("com.dude.dms.ApplicationKt")
}

vaadin {
    if (gradle.startParameter.taskNames.contains("bootDistZip")) {
        productionMode = true
    }
    pnpmEnable = true
}