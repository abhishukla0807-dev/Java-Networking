
/*
Problem:
Creating a new thread for each client does not scale well. Under heavy load, resources are quickly exhausted.

*Concept:
Use a thread pool to manage client connections. Threads are reused, and concurrency is controlled by pool limits.

*Solution:
TcpServerThreadPool listens on port 8080, accepts client sockets, and submits each to a ThreadPoolExecutor. The pool handles execution, queueing, and overload gracefully.

*Steps to Implement:
1. Define pool parameters: core threads, max threads, queue capacity, rejection policy.
2. Create a ThreadPoolExecutor with these parameters.
3. Start a ServerSocket on port 8080.
4. Accept client connections in a loop.
5. Submit each client socket to a ClientHandler via the pool.
6. Shut down the pool gracefully when the server stops.
*/



package com.aurexiris.networking.runtime.servers.threadpool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class TcpServerThreadPool {

    private static final int PORT = 8080;
    private static final int CORE_THREADS = 10;
    private static final int MAX_THREADS = 20;
    private static final int QUEUE_CAPACITY = 100;

    public static void main(String[] args) {

        // Work queue to hold tasks before execution
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        // Policy when pool and queue are full
        RejectedExecutionHandler rejectionHandler = (r, executor) -> {
            System.out.println("Server overloaded. Connection rejected.");
        };

        // Thread pool configuration
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                CORE_THREADS,
                MAX_THREADS,
                60L, TimeUnit.SECONDS,
                workQueue,
                rejectionHandler
        );

        System.out.println("Server starting on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client: " + clientSocket);
                pool.execute(new ClientHandler(clientSocket));
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown(pool);
        }
    }

    // Graceful shutdown of thread pool
    private static void shutdown(ThreadPoolExecutor pool) {
        System.out.println("Shutting down thread pool...");
        pool.shutdown();
        try {
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            pool.shutdownNow();
        }
    }
}
