package de.mheinzerling.mavenproxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Properties {
    final int proxyThreads;
    final Path proxyLocation;
    final int proxyPort;
    final boolean proxyOffline;
    final List<String> remoteRepos;
    final List<String> remoteExcludeExtensions;
    final List<String> remoteExcludeClassifiers;
    final int remoteThreads;

    private Properties(java.util.Properties props) {
        proxyThreads = integer(props, "proxy.threads", 8);
        proxyLocation = path(props, "proxy.location", ".cache");
        proxyPort = integer(props, "proxy.port", 3000);
        proxyOffline = bool(props, "proxy.offline", false);

        remoteRepos = list(props, "remote.repos", Maven.REPO1, Maven.APACHE, Maven.GRADLE_PLUGINS);
        remoteExcludeExtensions = list(props, "remote.exclude.extensions", ".asc", ".sha1", ".sha512", ".sha256", ".md5", "-release.zip", "-site.xml");
        remoteExcludeClassifiers = list(props, "remote.exclude.classifiers", "-javadoc.", "-tests.", "-tests.", "-test-sources.", "-groovydoc.");
        remoteThreads = integer(props, "remote.threads", 8);
    }

    public Properties() {
        this(fromDefault());
    }

    private static java.util.Properties fromDefault() {
        java.util.Properties props = new java.util.Properties();
        System.out.println("Loading default properties");
        return props;
    }


    public Properties(String file) throws IOException {
        this(fromFile(file));
    }

    private static java.util.Properties fromFile(String file) throws IOException {
        java.util.Properties props = new java.util.Properties();
        System.out.println("Loading properties file: " + file);
        if (file != null) try (InputStream input = new FileInputStream(file)) {
            props.load(input);
        }
        return props;
    }

    public Properties(Map<String, String> map) {
        this(fromMap(map));
    }

    private static java.util.Properties fromMap(Map<String, String> map) {
        java.util.Properties props = new java.util.Properties();
        System.out.println("Loading properties from map");
        props.putAll(map);
        return props;
    }

    private int integer(java.util.Properties props, String key, int def) {
        String value = props.getProperty(key);
        int result = value == null ? def : Integer.parseInt(value);
        System.out.println("  " + key + "=" + result);
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean bool(java.util.Properties props, String key, boolean def) {
        String value = props.getProperty(key);
        boolean result = value == null ? def : Boolean.parseBoolean(value);
        System.out.println("  " + key + "=" + result);
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private Path path(java.util.Properties props, String key, String def) {
        String value = props.getProperty(key);
        Path result = Paths.get(value == null ? def : value).toAbsolutePath();
        System.out.println("  " + key + "=" + result);
        return result;
    }

    private List<String> list(java.util.Properties props, String key, String... defs) {
        String value = props.getProperty(key);
        List<String> result = Arrays.asList(value == null ? defs : value.split(","));
        System.out.println("  " + key + "=" + result);
        return result;
    }
}