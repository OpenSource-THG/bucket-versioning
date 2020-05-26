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
package com.thehutgroup.versioning.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.thehutgroup.versioning.metafile.MetadataStreamReader;
import com.thehutgroup.versioning.VersioningProperties;
import org.gradle.api.Project;

public class S3Bucket implements VersionStorage {

    final AmazonS3 s3client;

    public S3Bucket() {
         s3client = AmazonS3ClientBuilder.defaultClient();
    }

    @Override
    public String latestVersion(Project project) {
        try {
            final VersioningProperties versioningProperties = project.getRootProject().getExtensions().getByType(VersioningProperties.class);

            String group = (String) project.getRootProject().getGroup();
            final String groupPath = group.replaceAll("\\.", "/");

            if (s3client.doesBucketExistV2(versioningProperties.getS3BucketName())) {
                final String keyPath = String.format("%s/%s/maven-metadata.xml", groupPath, project.getName());

                final S3Object object = s3client.getObject(versioningProperties.getS3BucketName(), keyPath);

                final S3ObjectInputStream objectContent = object.getObjectContent();
                return new MetadataStreamReader().read(objectContent).getVersioning().getRelease();
            }
        } catch (Exception e) {
        }
        return "0.0.0";
    }

}
