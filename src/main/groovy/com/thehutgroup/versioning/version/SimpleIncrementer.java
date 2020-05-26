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
package com.thehutgroup.versioning.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleIncrementer implements Incrementer {

    private static final Pattern VERSION_REGEX = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)(.*)");
    private static final String DEFAULT_VERSION = "0.1.0";

    @Override
    public String increment(String currentVersion, Strategy strategy) {
        final Matcher versionMatcher = VERSION_REGEX.matcher(currentVersion);
        while (versionMatcher.find()) {
            Integer major = Integer.parseInt(versionMatcher.group(1));
            Integer minor = Integer.parseInt(versionMatcher.group(2));
            Integer patch = Integer.parseInt(versionMatcher.group(3));

            patch += 1;
            if (patch >= 99) {
                patch = 0;
                minor += 1;
            }
            if (minor >= 99) {
                patch = 0;
                minor = 0;
                major += 1;
            }

            return String.format("%s.%s.%s", major, minor, patch);
        }
        return DEFAULT_VERSION;
    }
}
