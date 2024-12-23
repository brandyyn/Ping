plugins {
    id("fpgradle-minecraft") version ("0.9.0")
}

group = "mega"

minecraft_fp {
//    java {
//        compatibility = legacy //Convention
//        version       = JavaVersion.VERSION_XYZ //Convention, determined by compatibility
//    }

    mod {
        modid = "megaping"
        name = "MEGA Ping"
        rootPkg = "$group.ping"
    }
    tokens {
        tokenClass = "Tags"
    }
    publish {
        changelog = "https://github.com/GTMEGA/Ping/releases/tag/$version"
        maven {
            repoUrl = "https://mvn.falsepattern.com/gtmega_releases/"
            repoName = "mega"
        }
    }
}

repositories {
    exclusive(mavenpattern(), "com.falsepattern")
}

dependencies {
    implementationSplit("com.falsepattern:falsepatternlib-mc1.7.10:1.5.5")
}