package de.mheinzerling.mavenproxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProxyTest {
    private final Proxy online = new Proxy(new Properties(Collections.singletonMap("proxy.offline", "false")));
    private final Proxy offline = new Proxy(new Properties(Collections.singletonMap("proxy.offline", "true")));

    @BeforeEach
    void setUp() throws IOException {
        online.clear();
        offline.clear();
    }

    @Test
    void pom1() throws IOException {
        assertFull("/org/jetbrains/kotlin/kotlin-reflect/1.5.31/kotlin-reflect-1.5.31.pom",
                "HTTP/1.1 200 OK\r\n" +
                        "content-length: 1375\r\n" +
                        "\r\n",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project/>\n",
                s -> s.replaceAll("(?s)<project.+?</project>", "<project/>"),
                "de2931195ce691e75ddbf4ec2ddff41a0afcbd8a");
    }

    @Test
    void pom2() throws IOException {
        assertFull("/org/openjfx/javafx-plugin/0.0.10/javafx-plugin-0.0.10.pom",
                "HTTP/1.1 200 OK\r\n" +
                        "content-length: 810\r\n" +
                        "\r\n",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project/>\n",
                s -> s.replaceAll("(?s)<project.+?</project>", "<project/>"),
                "579823028373b16c50110cf91ae9f7ea2a265f54");
    }

    @Test
    void jar() throws IOException {
        assertFull("/org/jetbrains/kotlin/kotlin-build-common/1.5.31/kotlin-build-common-1.5.31.jar",
                "HTTP/1.1 200 OK\r\n" +
                        "content-length: 436065\r\n" +
                        "\r\n",
                "PK",
                s -> s.substring(0, 45),
                "0831c3253d3b4fc174feddf001c20aea37eeacc3");
    }

    @Test
    void some404() throws IOException {
        assertFull("/org/javamodularity/moduleplugin/1.8.2/moduleplugin-1.8.2.module",
                "HTTP/1.1 404 Not Found\r\n\r\n",
                "",
                Function.identity(), null);
    }

    private void assertFull(String request, String expected, String expectedOut, Function<String, String> filterOut, String sha1) throws IOException {
        final String head = "HEAD " + request + "  HTTP/1.1";
        final String get = "GET " + request + "  HTTP/1.1";
        final String getSha = "GET " + request + ".sha1  HTTP/1.1";

        assertThrows(AssertionError.class, () -> offline.handle(get, new ByteArrayOutputStream())); //not cached yet
        assertHead(online, head, expected); //fill cache
        assertHead(offline, head, expected);
        assertGet(offline, get, expected, expectedOut, filterOut);
        if (sha1 != null) assertSha(offline, getSha, sha1);
    }

    private void assertSha(Proxy proxy, String get, String sha) throws IOException {
        String expected = "HTTP/1.1 200 OK\r\n" +
                "content-length: 40\r\n" +
                "\r\n";
        final ByteArrayOutputStream outGet = new ByteArrayOutputStream();
        final String responseGet = proxy.handle(get, outGet).responseString;
        assertEquals(expected, responseGet);
        assertEquals(expected + sha, outGet.toString());
    }


    private void assertGet(Proxy proxy, String get, String expected, String expectedOut, Function<String, String> filterOut) throws IOException {
        final ByteArrayOutputStream outGet = new ByteArrayOutputStream();
        final String responseGet = proxy.handle(get, outGet).responseString;
        assertEquals(expected, responseGet);
        assertEquals(expected + expectedOut, filterOut.apply(outGet.toString()));
    }

    private void assertHead(Proxy proxy, String head, String expected) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String response = proxy.handle(head, out).responseString;
        assertEquals(expected, response);
        assertEquals(expected, out.toString());
    }

    @Test
    public void sha512() throws IOException {
        String expected = "HTTP/1.1 200 OK\r\n" +
                "content-length: 128\r\n" +
                "\r\n";
        String request = "/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.5.0/kotlinx-coroutines-core-jvm-1.5.0.module.sha512";
        assertGet(online, "GET " + request + " HTTP/1.1", expected, "b0ca6a56172ec06b7fab93c441b9f2ba4950253a022ea7895a63e6276d73430f7c55f5390058ba5150dfe41cb5a5177b38b1b6fb4825528ed5f32ab644074e94", Function.identity());
    }

    @Test
    public void sha256() throws IOException {
        String expected = "HTTP/1.1 200 OK\r\n" +
                "content-length: 64\r\n" +
                "\r\n";
        String request = "/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.5.0/kotlinx-coroutines-core-jvm-1.5.0.module.sha256";
        assertGet(online, "GET " + request + " HTTP/1.1", expected, "c885dd0281076c5843826de317e3cbcdc3d8859dbeef53ae1cfacd1b9c60f96e", Function.identity());
    }

    @Test
    public void md5() throws IOException {
        String expected = "HTTP/1.1 200 OK\r\n" +
                "content-length: 32\r\n" +
                "\r\n";
        String request = "/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.5.0/kotlinx-coroutines-core-jvm-1.5.0.module.md5";
        assertGet(online, "GET " + request + " HTTP/1.1", expected, "6649c24dd40128bebc2812683ffeae17", Function.identity());
    }
}