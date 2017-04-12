package happylife.demo;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

class SimpleHttpServer {

    void start(int port) throws IOException {
//        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("server started at " + port);
            server.createContext("/", new handlers.EchoGetHandler());
            server.createContext("/echoHeader", new handlers.EchoHeaderHandler());
            server.createContext("/echoGet", new handlers.EchoGetHandler());
            server.createContext("/echoPost", new handlers.EchoPostHandler());
            server.setExecutor(null);
            server.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
