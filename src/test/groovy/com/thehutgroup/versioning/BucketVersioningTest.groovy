/*
 * Copyright 2020 THG
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thehutgroup.versioning

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class BucketVersioningTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    static final String[] gradleVersions = [
            '6.0',
            '6.0.1',
            '6.1',
            '6.1.1',
            '6.2',
            '6.2.1',
            '6.2.2',
            '6.3',
            '6.4',
            '6.4.1'
    ]

    File rootBuildFile
    File settingsFile

    def setup() {
        rootBuildFile = testProjectDir.newFile('build.gradle')
        settingsFile = testProjectDir.newFile('settings.gradle')
    }

    @Unroll
    def "has a version of 0.0.1 when using None from single root project with gradle version #gradleVersion"() {
        given:
        settingsFile <<
                """
                rootProject.name = 'bucket-versioning-plugin-test'
                """
        rootBuildFile <<
                """
                plugins {
                    id 'com.thehutgroup.BucketVersioning'
                }
                
                version = '0.1.0'
                group = 'com.thehutgroup.versioning'
                
                versioning {
                    storageProvider = "None"
                }
                """

        when:
        def result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withArguments('properties')
                .withPluginClasspath()
                .build()

        then:
        result.task(":properties").outcome == SUCCESS
        result.output.readLines().stream().any({property -> property.matches("^version:\\s+\\d+.\\d+.\\d+.*\$")})
        result.output.readLines().stream().any({property -> property.matches("^version:\\s+0\\.0\\.1\$")})

        where:
        gradleVersion << gradleVersions
    }

}