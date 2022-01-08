package de.mheinzerling.mavenproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        final Properties properties = new Properties(args.length > 0 ? args[0] : null);

        final ExecutorService executor = Executors.newFixedThreadPool(properties.proxyThreads);
        final ServerSocket serverSocket = new ServerSocket(properties.proxyPort);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print("Closing Server..");
            executor.shutdown();
            try {
                serverSocket.close();
            } catch (IOException e) {
                //ignore
            }
            System.out.print("done.");
        }));

        final Proxy proxy = new Proxy(properties);
        System.out.println("Listening...");
        //noinspection InfiniteLoopStatement
        while (true) {
            Socket socket = serverSocket.accept();
            executor.submit(() -> {
                try {
                    //noinspection TryFinallyCanBeTryWithResources
                    try {
                        proxy.handle(socket);
                    } finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
