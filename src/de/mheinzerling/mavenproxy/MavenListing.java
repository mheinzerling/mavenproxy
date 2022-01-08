package de.mheinzerling.mavenproxy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenListing {
    private static final Pattern fileLink = Pattern.compile("<a href=\"(.+?)\".*?>(.+?)</a>");

    private final String repo;
    private final String path;
    private final List<String> files = new ArrayList<>();
    private final List<String> excludedByExtension = new ArrayList<>();
    private final List<String> excludedByClassifier = new ArrayList<>();

    public MavenListing(String repo, String path) {
        this.repo = repo;
        this.path = path;
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    public void fetch(Properties properties) throws IOException {
        URLConnection conn = asURL().openConnection();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                Matcher matcher = fileLink.matcher(line);
                if (!matcher.find()) continue;
                String href = matcher.group(1);
                String text = matcher.group(2);
                if (!(href.startsWith(text.substring(0, text.length() - 3)))) continue;
                if (href.endsWith("/")) continue;
                else if (properties.remoteExcludeExtensions.stream().anyMatch(href::endsWith)) {
                    excludedByExtension.add(href);
                    continue;
                } else if (properties.remoteExcludeClassifiers.stream().anyMatch(href::contains)) {
                    excludedByClassifier.add(href);
                    continue;
                }
                files.add(href);
            }
        } catch (FileNotFoundException e) {
            // do nothing
        }
    }

    private URL asURL() {
        try {
            return new URL(repo + path);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    public String getRepo() {
        return repo;
    }

    public String getPath() {
        return path;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<String> getExcludedByClassifier() {
        return excludedByClassifier;
    }

    public List<String> getExcludedByExtension() {
        return excludedByExtension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MavenListing that = (MavenListing) o;

        if (!repo.equals(that.repo)) return false;
        if (!path.equals(that.path)) return false;
        if (!files.equals(that.files)) return false;
        if (!excludedByExtension.equals(that.excludedByExtension)) return false;
        return excludedByClassifier.equals(that.excludedByClassifier);
    }

    @Override
    public int hashCode() {
        int result = repo.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + files.hashCode();
        result = 31 * result + excludedByExtension.hashCode();
        result = 31 * result + excludedByClassifier.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MavenListing{" +
                "repo='" + repo + '\'' +
                ", path='" + path + '\'' +
                ", files=" + files +
                ", excludedByExtension=" + excludedByExtension +
                ", excludedByClassifier=" + excludedByClassifier +
                '}';
    }
}
