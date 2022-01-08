package de.mheinzerling.mavenproxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Formatter;

public class Utils {

    static void loadToFile(String sourceUrl, Path target) throws IOException {
        URLConnection conn = new URL(sourceUrl).openConnection();
        try (InputStream in = conn.getInputStream();
             OutputStream out = Files.newOutputStream(target)
        ) {
            transferTo(in, out);
        }
    }

    static String loadToString(String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        try (InputStream in = conn.getInputStream();
             OutputStream out = new ByteArrayOutputStream()
        ) {
            transferTo(in, out);
            return out.toString();
        }
        catch (FileNotFoundException e){
            return null;
        }
    }

    static void transferTo(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    static String checksum(Path file, String algorithm) throws IOException {
        try (Formatter formatter = new Formatter()) {
            byte[] content = Files.readAllBytes(file);

            MessageDigest crypt = MessageDigest.getInstance(algorithm);
            crypt.reset();
            crypt.update(content);
            byte[] hash = crypt.digest();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    static void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        //noinspection ResultOfMethodCallIgnored
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
