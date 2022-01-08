package de.mheinzerling.mavenproxy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static de.mheinzerling.mavenproxy.Maven.GRADLE_PLUGINS;
import static de.mheinzerling.mavenproxy.Maven.REPO1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenIntegrationTest {

    public static final String JAVAPARSER_CORE_3_13_5 = "/com/github/javaparser/javaparser-core/3.13.5/";
    public static final String SEMVER_4_J_0_16_4 = "/com/github/gundy/semver4j/0.16.4/";
    public static final String JAVAFX_PLUGIN_0_0_10 = "/org/openjfx/javafx-plugin/0.0.10/";
    public static final String KOTLIN_REFLECT_1_5_31 = "/org/jetbrains/kotlin/kotlin-reflect/1.5.31/";
    public static final String LISTABLE_FUTURE_9999 = "/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/";
    public static final String KOTLINX_COROUTINES_1_5_0 = "/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.5.0/";

    public static final Path TEMP = Paths.get(".temp").toAbsolutePath();
    public static final Maven maven = new Maven(new Properties());

    @BeforeAll
    static void clearTemp() throws IOException {
        Utils.deleteDirectory(TEMP);
    }

    @Test
    void javaparser_core() throws IOException {
        MavenListing expected = new MavenListing(REPO1, "/com/github/javaparser/javaparser-core/3.13.5/");
        expected.getFiles().add("javaparser-core-3.13.5-sources.jar");
        expected.getFiles().add("javaparser-core-3.13.5.jar");
        expected.getFiles().add("javaparser-core-3.13.5.pom");
        expected.getExcludedByClassifier().add("javaparser-core-3.13.5-javadoc.jar");
        expected.getExcludedByExtension().addAll(Arrays.asList("javaparser-core-3.13.5-javadoc.jar.asc",
                "javaparser-core-3.13.5-javadoc.jar.md5", "javaparser-core-3.13.5-javadoc.jar.md5.asc",
                "javaparser-core-3.13.5-javadoc.jar.sha1", "javaparser-core-3.13.5-sources.jar.asc",
                "javaparser-core-3.13.5-sources.jar.md5", "javaparser-core-3.13.5-sources.jar.md5.asc",
                "javaparser-core-3.13.5-sources.jar.sha1", "javaparser-core-3.13.5.jar.asc",
                "javaparser-core-3.13.5.jar.md5", "javaparser-core-3.13.5.jar.md5.asc",
                "javaparser-core-3.13.5.jar.sha1", "javaparser-core-3.13.5.pom.asc",
                "javaparser-core-3.13.5.pom.md5", "javaparser-core-3.13.5.pom.md5.asc",
                "javaparser-core-3.13.5.pom.sha1"));

        assertEquals(expected, maven.index(JAVAPARSER_CORE_3_13_5));

        maven.loadAll(expected, TEMP);
    }

    @Test
    void semver() throws IOException {
        MavenListing expected = new MavenListing(REPO1, "/com/github/gundy/semver4j/0.16.4/");
        expected.getFiles().add("semver4j-0.16.4-nodeps-sources.jar");
        expected.getFiles().add("semver4j-0.16.4-nodeps.jar");
        expected.getFiles().add("semver4j-0.16.4-sources.jar");
        expected.getFiles().add("semver4j-0.16.4.jar");
        expected.getFiles().add("semver4j-0.16.4.pom");
        expected.getExcludedByClassifier().add("semver4j-0.16.4-javadoc.jar");
        expected.getExcludedByExtension().addAll(Arrays.asList("semver4j-0.16.4-javadoc.jar.asc",
                "semver4j-0.16.4-javadoc.jar.md5", "semver4j-0.16.4-javadoc.jar.sha1",
                "semver4j-0.16.4-nodeps-sources.jar.asc", "semver4j-0.16.4-nodeps-sources.jar.md5",
                "semver4j-0.16.4-nodeps-sources.jar.sha1", "semver4j-0.16.4-nodeps.jar.asc",
                "semver4j-0.16.4-nodeps.jar.md5", "semver4j-0.16.4-nodeps.jar.sha1",
                "semver4j-0.16.4-sources.jar.asc", "semver4j-0.16.4-sources.jar.md5",
                "semver4j-0.16.4-sources.jar.sha1", "semver4j-0.16.4.jar.asc",
                "semver4j-0.16.4.jar.md5", "semver4j-0.16.4.jar.sha1",
                "semver4j-0.16.4.pom.asc", "semver4j-0.16.4.pom.md5",
                "semver4j-0.16.4.pom.sha1"));

        assertEquals(expected, maven.index(SEMVER_4_J_0_16_4));
        maven.loadAll(expected, TEMP);
    }

    @Test
    void javafx_plugin() throws IOException {
        MavenListing expected = new MavenListing(GRADLE_PLUGINS, "/org/openjfx/javafx-plugin/0.0.10/");
        expected.getFiles().add("javafx-plugin-0.0.10-sources.jar");
        expected.getFiles().add("javafx-plugin-0.0.10.jar");
        expected.getFiles().add("javafx-plugin-0.0.10.pom");
        expected.getExcludedByClassifier().add("javafx-plugin-0.0.10-javadoc.jar");
        expected.getExcludedByExtension().addAll(Arrays.asList("javafx-plugin-0.0.10-javadoc.jar.md5",
                "javafx-plugin-0.0.10-javadoc.jar.sha1", "javafx-plugin-0.0.10-sources.jar.md5",
                "javafx-plugin-0.0.10-sources.jar.sha1", "javafx-plugin-0.0.10.jar.md5",
                "javafx-plugin-0.0.10.jar.sha1", "javafx-plugin-0.0.10.pom.md5",
                "javafx-plugin-0.0.10.pom.sha1"));

        assertEquals(expected, maven.index(JAVAFX_PLUGIN_0_0_10));
        maven.loadAll(expected, TEMP);
    }

    @Test
    void kotlin_reflect() throws IOException {

        MavenListing expected = new MavenListing(REPO1, "/org/jetbrains/kotlin/kotlin-reflect/1.5.31/");
        expected.getFiles().add("kotlin-reflect-1.5.31-modular.jar");
        expected.getFiles().add("kotlin-reflect-1.5.31-sources.jar");
        expected.getFiles().add("kotlin-reflect-1.5.31.jar");
        expected.getFiles().add("kotlin-reflect-1.5.31.pom");
        expected.getExcludedByClassifier().add("kotlin-reflect-1.5.31-javadoc.jar");
        expected.getExcludedByExtension().addAll(Arrays.asList("kotlin-reflect-1.5.31-javadoc.jar.asc",
                "kotlin-reflect-1.5.31-javadoc.jar.asc.md5", "kotlin-reflect-1.5.31-javadoc.jar.asc.sha1",
                "kotlin-reflect-1.5.31-javadoc.jar.md5", "kotlin-reflect-1.5.31-javadoc.jar.sha1",
                "kotlin-reflect-1.5.31-modular.jar.asc", "kotlin-reflect-1.5.31-modular.jar.asc.md5",
                "kotlin-reflect-1.5.31-modular.jar.asc.sha1", "kotlin-reflect-1.5.31-modular.jar.md5",
                "kotlin-reflect-1.5.31-modular.jar.sha1", "kotlin-reflect-1.5.31-sources.jar.asc",
                "kotlin-reflect-1.5.31-sources.jar.asc.md5", "kotlin-reflect-1.5.31-sources.jar.asc.sha1",
                "kotlin-reflect-1.5.31-sources.jar.md5", "kotlin-reflect-1.5.31-sources.jar.sha1",
                "kotlin-reflect-1.5.31.jar.asc", "kotlin-reflect-1.5.31.jar.asc.md5",
                "kotlin-reflect-1.5.31.jar.asc.sha1", "kotlin-reflect-1.5.31.jar.md5",
                "kotlin-reflect-1.5.31.jar.sha1", "kotlin-reflect-1.5.31.pom.asc",
                "kotlin-reflect-1.5.31.pom.asc.md5", "kotlin-reflect-1.5.31.pom.asc.sha1",
                "kotlin-reflect-1.5.31.pom.md5", "kotlin-reflect-1.5.31.pom.sha1"));

        assertEquals(expected, maven.index(KOTLIN_REFLECT_1_5_31));
        maven.loadAll(expected, TEMP);
    }

    @Test
    void kotlinx_coroutines_core() throws IOException {

        MavenListing expected = new MavenListing(REPO1, "/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.5.0/");
        expected.getFiles().add("kotlinx-coroutines-core-jvm-1.5.0-sources.jar");
        expected.getFiles().add("kotlinx-coroutines-core-jvm-1.5.0.jar");
        expected.getFiles().add("kotlinx-coroutines-core-jvm-1.5.0.module");
        expected.getFiles().add("kotlinx-coroutines-core-jvm-1.5.0.pom");
        expected.getExcludedByClassifier().add("kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar");
        expected.getExcludedByExtension().addAll(Arrays.asList("kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.asc",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.asc.md5",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.asc.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.asc.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.asc.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.md5",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0-javadoc.jar.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.asc",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.asc.md5",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.asc.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.asc.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.asc.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.md5",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0-sources.jar.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.asc",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.asc.md5",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.asc.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.asc.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.asc.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.md5",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0.jar.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0.module.asc",
                "kotlinx-coroutines-core-jvm-1.5.0.module.asc.md5",
                "kotlinx-coroutines-core-jvm-1.5.0.module.asc.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0.module.asc.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0.module.asc.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0.module.md5",
                "kotlinx-coroutines-core-jvm-1.5.0.module.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0.module.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0.module.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.asc",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.asc.md5",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.asc.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.asc.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.asc.sha512",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.md5",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.sha1",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.sha256",
                "kotlinx-coroutines-core-jvm-1.5.0.pom.sha512"));

        assertEquals(expected, maven.index(KOTLINX_COROUTINES_1_5_0));
        maven.loadAll(expected, TEMP);
    }

    @Test
    void listenablefuture() throws IOException {
        MavenListing expected = new MavenListing(REPO1, "/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/");
        expected.getFiles().add("listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar");
        expected.getFiles().add("listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.pom");
        expected.getExcludedByExtension().addAll(Arrays.asList("listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar.asc",
                "listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar.md5",
                "listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar.sha1",
                "listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.pom.asc",
                "listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.pom.md5",
                "listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.pom.sha1"));

        assertEquals(expected, maven.index(LISTABLE_FUTURE_9999));
        maven.loadAll(expected, TEMP);
    }

    @Test
    void metadata() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertTrue(maven.copy("/org/checkerframework/checker-qual/maven-metadata.xml", out));
        assertEquals("HTTP/1.1 200 OK\n" +
                "content-length: 3440\n" + //TODO fragile !!!
                "\n" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<metadata>\n" +
                "  <groupId>org.checkerframework</groupId>\n" +
                "  <artifactId>checker-qual</artifactId>\n" +
                "  <versioning>\n" +
                "    <latest>", out.toString().substring(0,200).replaceAll("\r",""));
    }

}