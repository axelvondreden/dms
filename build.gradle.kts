import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.10" apply true
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.10" apply true
    id("com.vaadin") version "0.14.3.7"
    application
}

ext {
    set("vaadinVersion", "14.3.7")
}

noArg {
    annotation("javax.persistence.Entity")
}

group = "com.dude.dms"
version = "0.2.5"

val karibudslVersion = "1.0.3"
val vaadinVersion = "14.4.2"

defaultTasks("clean", "build")

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
    maven { setUrl("https://dl.bintray.com/hotkeytlt/maven") }
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:$vaadinVersion")
        mavenBom("dev.forkhandles:forkhandles-bom:1.2.0.0")
    }
}

dependencies {
    implementation("com.github.mvysny.karibudsl:karibu-dsl:$karibudslVersion")

    implementation("com.vaadin:vaadin-spring-boot-starter") {
        // Webjars are only needed when running in Vaadin 13 compatibility mode
        arrayOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
                "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
                "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
                .forEach { exclude(it) }
    }

    implementation("commons-net:commons-net:3.7")
    implementation("commons-logging:commons-logging:1.2")
    implementation("com.h2database:h2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("com.vaadin:vaadin-spring")

    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude("org.apache.tomcat")
    }
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("javax.xml.bind:jaxb-api")
    implementation("org.reactivestreams:reactive-streams")
    implementation("javax.persistence:javax.persistence-api")
    implementation("org.hibernate:hibernate-core:5.4.10.Final")
    implementation("org.apache.pdfbox:pdfbox-tools:2.0.21")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    implementation("org.bytedeco:tesseract-platform:4.1.1-1.5.4")
    implementation("org.languagetool:languagetool-core:5.1")
    implementation("org.languagetool:language-de:5.1")
    implementation("org.languagetool:language-en:5.1")
    implementation("com.atlascopco:hunspell-bridj:1.0.4")
    implementation("com.github.vatbub:mslinks:1.0.5")
    implementation("dev.forkhandles:parser4k")

    implementation("com.github.appreciated:app-layout-addon:4.0.0")
    implementation("com.github.appreciated:apexcharts:2.0.0.beta10")
    implementation("com.github.appreciated:card:2.0.0")
    implementation("com.github.appreciated:color-picker-field-flow:2.0.0.beta6")
    implementation("dev.mett.vaadin:tooltip:1.6.0")
    implementation("org.vaadin.artur:spring-data-provider:2.1.0")
    implementation("org.vaadin.gatanaso:multiselect-combo-box-flow:2.5.0")
    implementation("org.vaadin.olli:file-download-wrapper:3.0.1")
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

tasks.bootDistZip {
    archiveFileName.set("dms")
    archiveBaseName.set("dms")
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
    mainClassName = "com.dude.dms.ApplicationKt"
}

vaadin {
    if (gradle.startParameter.taskNames.contains("bootDistZip")) {
        productionMode = true
    }
    pnpmEnable = true
}