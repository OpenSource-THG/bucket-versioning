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
package com.thehutgroup.versioning;

import com.thehutgroup.versioning.storage.StorageProvider;
import com.thehutgroup.versioning.storage.VersionStorage;
import com.thehutgroup.versioning.version.Incrementer;
import com.thehutgroup.versioning.version.SimpleIncrementer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class BucketVersioning implements Plugin<Project> {

    public static final String EXTENSION_ROOT = "versioning";
    private final Incrementer incrementer = new SimpleIncrementer();

    @Override
    public void apply(Project project) {
        final VersioningProperties versioningExtension = project.getExtensions().create(EXTENSION_ROOT, VersioningProperties.class);

        project.afterEvaluate(p -> {

            final VersionStorage provider = getVersionStorageProvider(versioningExtension);

            project.setVersion(incrementVersion(project, provider));
            project.subprojects(subproject -> {
                subproject.setVersion(incrementVersion(subproject, provider));
            });

        });

    }

    private VersionStorage getVersionStorageProvider(VersioningProperties versioningExtension) {
        VersionStorage provider;
        try {
            final Class clazz = StorageProvider.valueOf(versioningExtension.getStorageProvider()).getClazz();
            provider = (VersionStorage) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return provider;
    }

    private String incrementVersion(Project project, VersionStorage versionStorage) {
        final String latestVersion = versionStorage.latestVersion(project);
        return incrementer.increment(latestVersion, Incrementer.Strategy.RELEASE);
    }
}
