# mavenproxy

Simple caching Maven proxy to add dependencies to version control

# Getting started

Properties file is optional. See defaults below.

```
java -jar mavenproxy.jar mavenproxy.properties
```

Add to all your `settings.gradle[.kts]`

```
pluginManagement {
    repositories {
        maven(url = "http://localhost:3000/") {
            isAllowInsecureProtocol = true
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "http://localhost:3000/") {
            isAllowInsecureProtocol = true
        }
    }
}
```

Make sure to cleared your Maven/Gradle cache, otherwise you might not get all dependencies.

# default.properties

```
proxy.threads=8
proxy.location=.cache
proxy.port=3000
proxy.offline=false
remote.repos=[https://repo1.maven.org/maven2, https://repo.maven.apache.org/maven2, https://plugins.gradle.org/m2]
remote.exclude.extensions=[.asc, .sha1, .sha512, .sha256, .md5, -release.zip, -site.xml]
remote.exclude.classifiers=[-javadoc., -tests., -tests., -test-sources., -groovydoc.]
remote.threads=8
```

# TODO

- second cache level in user home for faster project updates 