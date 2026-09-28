pluginManagement {
    plugins {
        kotlin("plugin.spring") version "2.3.21"
    }
}
rootProject.name = "emerion-dashboard"

include("domain", "application", "infrastructure", "app")

include("adapter")
include("adapter")