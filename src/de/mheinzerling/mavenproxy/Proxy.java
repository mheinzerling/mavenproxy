package de.mheinzerling.mavenproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static de.mheinzerling.mavenproxy.Utils.transferTo;

public final class Proxy {

    private final Properties properties;
    private final Maven maven;

    Proxy(Properties properties) {
        this.properties = properties;
        this.maven = new Maven(properties);
    }

    public void clear() throws IOException {
        Utils.deleteDirectory(properties.proxyLocation);
    }

    static class Result {
        final String responseString;
        final boolean fromCache;

        Result(String responseString, boolean fromCache) {
            this.responseString = responseString;
            this.fromCache = fromCache;
        }

        String getSimpleResponseString() {
            return responseString.trim().replaceAll("\r\n", " ");
        }
    }

    public Result handle(String requestLine, OutputStream outputStream) throws IOException {
        final String[] parts = requestLine.split(" ");
        final String method = parts[0];
        if (method.equals("CONNECT")) throw new RuntimeException("HTTPS not supported");
        final boolean isHead = method.equals("HEAD");
        final boolean isGet = method.equals("GET");
        if (!isHead && !isGet) throw new RuntimeException(method + " not supported");


        final String requestUri = parts[1];
        int pos = requestUri.lastIndexOf("/");
        String dir = requestUri.substring(0, pos + 1);
        String file = requestUri.substring(pos + 1);
        Path cacheSubDir = properties.proxyLocation.resolve(dir.substring(1));

        if (file.endsWith("maven-metadata.xml")) {
            if (properties.proxyOffline)
                throw new AssertionError("Can't check meta data " + requestLine + " in offline mode");

            if (maven.copy(requestUri, outputStream)) {
                return new Result("[maven-metadata.xml]", false);
            }
        }

        boolean hit = true;
        if (missing(cacheSubDir)) {
            if (properties.proxyOffline)
                throw new AssertionError("Missed cache for " + requestLine + " in offline mode");
            MavenListing listing = maven.index(dir);
            //System.out.println(listing);
            maven.loadAll(listing, properties.proxyLocation);
            hit = false;
        }

        if (file.endsWith(".sha1") || file.endsWith(".sha256") || file.endsWith(".sha512") || file.endsWith(".md5")) {
            Path data = cacheSubDir.resolve(file.substring(0, file.lastIndexOf(".")));
            if (Files.exists(data)) {

                String algorithm;
                int length;
                if (file.endsWith(".sha1")) {
                    algorithm = "SHA-1";
                    length = 40;
                } else if (file.endsWith(".sha256")) {
                    algorithm = "SHA-256";
                    length = 64;
                } else if (file.endsWith(".sha512")) {
                    algorithm = "SHA-512";
                    length = 128;
                } else /*if (file.endsWith(".md5")) */ {
                    algorithm = "MD5";
                    length = 32;
                }
                final String responseString = "HTTP/1.1 200 OK\r\n" +
                        "content-length: " + length + "\r\n\r\n";
                outputStream.write(responseString.getBytes(StandardCharsets.UTF_8));
                if (isGet) {

                    outputStream.write(Utils.checksum(data, algorithm).getBytes(StandardCharsets.UTF_8));
                }
                return new Result(responseString, hit);
            }
        } else {
            Path data = cacheSubDir.resolve(file);
            if (Files.exists(data)) {
                final String responseString = "HTTP/1.1 200 OK\r\n" +
                        "content-length: " + Files.size(data) + "\r\n\r\n";
                outputStream.write(responseString.getBytes(StandardCharsets.UTF_8));
                if (isGet) {
                    try (InputStream is = Files.newInputStream(data)) {
                        transferTo(is, outputStream);
                    }
                }
                return new Result(responseString, hit);
            }
        }
        final String responseString = "HTTP/1.1 404 Not Found\r\n\r\n";
        outputStream.write(responseString.getBytes(StandardCharsets.UTF_8));
        System.out.println("Missing: " + requestUri);
        return new Result(responseString, hit);
    }

    private boolean missing(Path cacheSubDir) throws IOException {
        if (!Files.exists(cacheSubDir)) return true;
        try (Stream<Path> files = Files.list(cacheSubDir)) {
            return !files.findAny().isPresent();
        }
    }

    void handle(Socket socket) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final String requestLine = reader.readLine(); //ignore header and body
        final OutputStream outputStream = socket.getOutputStream();
        final Result result = handle(requestLine, outputStream);
        outputStream.flush();
        if (!result.fromCache) {
            System.out.println(requestLine + " -> " + result.getSimpleResponseString());
        }
    }
}

