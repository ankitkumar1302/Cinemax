/*
 * Copyright 2022 Maximillian Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    source = files(rootDir)
    config = files("$rootDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    parallel = true
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.kode.detekt.rules.compose)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    exclude("config/**")
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
