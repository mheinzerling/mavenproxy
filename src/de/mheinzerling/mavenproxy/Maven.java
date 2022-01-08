package de.mheinzerling.mavenproxy;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.mheinzerling.mavenproxy.Utils.checksum;
import static de.mheinzerling.mavenproxy.Utils.loadToFile;
import static de.mheinzerling.mavenproxy.Utils.loadToString;

public class Maven {
    public static final String REPO1 = "https://repo1.maven.org/maven2";
    public static final String APACHE = "https://repo.maven.apache.org/maven2";
    public static final String GRADLE_PLUGINS = "https://plugins.gradle.org/m2";

    private final Properties properties;

    public Maven(Properties properties) {
        this.properties = properties;
    }

    public MavenListing index(String dir) throws IOException {
        for (String repo : properties.remoteRepos) {
            MavenListing listing = new MavenListing(repo, dir);
            listing.fetch(properties);
            if (!listing.isEmpty()) return listing;
        }
        throw new IOException("Index missing in all repos for " + dir);
    }

    public void loadAll(MavenListing source, Path target) throws IOException {
        String sourceDirectoryUrl = source.getRepo() + source.getPath();
        System.out.println("Loading " + sourceDirectoryUrl + "...");
        Path targetDirectory = target.resolve(source.getPath().substring(1));
        Files.createDirectories(targetDirectory);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(properties.remoteThreads);
        for (String file : source.getFiles()) {
            executor.submit(() -> download(sourceDirectoryUrl, targetDirectory, file));
        }

        executor.shutdown();
        try {
            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void download(String sourceDirectoryUrl, Path targetDirectory, String file) {
        try {
            String sourceUrl = sourceDirectoryUrl + file;
            Path path = targetDirectory.resolve(file);
            loadToFile(sourceUrl, path);
            String remoteHash = loadToString(sourceUrl + ".sha1");
            String localHash = checksum(path, "SHA-1");
            if (!Objects.equals(remoteHash, localHash)) {
                throw new AssertionError("Hash mismatch for " + path + "; remote: " + remoteHash + ", local: " + localHash);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Utils.deleteDirectory(targetDirectory);
            } catch (IOException ex) {
                throw new AssertionError(ex);
            }

        }
    }

    public boolean copy(String file, OutputStream outputStream) throws IOException {
        for (String repo : properties.remoteRepos) {

            String content = loadToString(repo + file);
            if (content != null) {
                final String responseString = "HTTP/1.1 200 OK\r\n" +
                        "content-length: " + content.length() + "\r\n\r\n";
                outputStream.write(responseString.getBytes(StandardCharsets.UTF_8));
                outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                return true;
            }
        }
        return false;
    }
}
