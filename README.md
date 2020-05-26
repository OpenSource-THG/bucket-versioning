# Bucket Versioning

## Intro

A Gradle plugin to generate a version based on a strategy against the currently published version to S3.  

Relies on using S3 as a maven repository.

## Configuration

Provide a `versioning` block in your gradle build file. Supported keys are below.

`storageProvider`

- S3
- None

An optional configuration.
 
A value of `S3` requires the `s3BucketName` config to be also set. It will use S3 to retrieve the maven-metadata.xml
and determine the current version from there.

A value of `None` will use the specified version from gradle otherwise defaulting to `0.0.0`

Omitting the value defaults to None

`s3BucketName`

Required when using a `storageProvider` of `S3`. Will attempt to retreive the maven-metadata.xml from this S3 bucket.

Omitting the value will set no default. If the storage provider is S3 it will cause a retrieval failure.
If the storage provider if any other value it has no effect.

## Examples

### Applying the plugin

**build.gradle**

*Please note the versioning block*

```gradle
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.thehutgroup', name: 'bucket-versioning', version: '[0.0.1,)'
    }
}

apply plugin: 'com.thehutgroup.BucketVersioning'


versioning {
    storageProvider = "S3"
    s3BucketName = "example-bucket"
}
```

**settings.gradle**

```gradle
pluginManagement {
    repositories {
        maven {
            url "s3://example-bucket/"
            authentication {
                awsIm(AwsImAuthentication)
            }
        }
    }
}
```

## Running the tests

To be able to execute the `BucketVersioningTest` there must me a S3 bucket that is accessible to the user running the tests.

Replace `example-bucket` in `settings.gradle` with the bucket that you have access to.

## FAQ

Q. **What happens if there are no remote versions published yet?**

A. By default in this scenario it will set the current version to `0.0.0` and then run through the incrementing strategy.
Since there is only one strategy available of incremental. The version the plugin will output is `0.0.1`.